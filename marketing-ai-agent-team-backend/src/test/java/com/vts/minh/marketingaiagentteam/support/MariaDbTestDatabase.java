package com.vts.minh.marketingaiagentteam.support;

import org.testcontainers.mariadb.MariaDBContainer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/** Owns a disposable server; never accepts a URL for an existing database. */
public final class MariaDbTestDatabase implements AutoCloseable {
    private static final String PASSWORD = "synthetic-" + UUID.randomUUID();
    private final String serverUrl;
    private final Process nativeServer;
    private final MariaDBContainer container;

    private MariaDbTestDatabase(String serverUrl, Process nativeServer, MariaDBContainer container) {
        this.serverUrl = serverUrl;
        this.nativeServer = nativeServer;
        this.container = container;
    }

    public static MariaDbTestDatabase instance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final MariaDbTestDatabase INSTANCE = start();
    }

    private static MariaDbTestDatabase start() {
        String nativeBin = System.getProperty("test.mariadb.bin");
        MariaDbTestDatabase database;
        if (nativeBin == null || nativeBin.isBlank()) {
            MariaDBContainer container = new MariaDBContainer("mariadb:12.3.3")
                    .withUsername("root")
                    .withPassword(PASSWORD)
                    .withDatabaseName("bootstrap");
            container.start();
            database = new MariaDbTestDatabase(
                    "jdbc:mariadb://" + container.getHost() + ":" + container.getMappedPort(3306) + "/",
                    null, container);
        } else {
            database = startNative(Path.of(nativeBin));
        }
        Runtime.getRuntime().addShutdownHook(new Thread(database::close, "mariadb-test-shutdown"));
        return database;
    }

    private static MariaDbTestDatabase startNative(Path bin) {
        Process server = null;
        try {
            Path installer = bin.resolve("mariadb-install-db.exe").toRealPath();
            Path executable = bin.resolve("mariadbd.exe").toRealPath();
            Path target = Path.of("target").toAbsolutePath().normalize();
            Files.createDirectories(target);
            Path work = Files.createTempDirectory(target, "mariadb-it-");
            Path data = work.resolve("data");
            int port;
            try (ServerSocket socket = new ServerSocket(0, 0, InetAddress.getLoopbackAddress())) {
                port = socket.getLocalPort();
            }
            Process install = new ProcessBuilder(installer.toString(), "--datadir=" + data,
                    "--password=" + PASSWORD, "--port=" + port, "--silent")
                    .redirectErrorStream(true).redirectOutput(work.resolve("install.log").toFile()).start();
            if (!install.waitFor(90, TimeUnit.SECONDS)) {
                install.destroyForcibly();
                throw new IllegalStateException("Timed out initializing disposable MariaDB: " + data);
            }
            if (install.exitValue() != 0) {
                throw new IllegalStateException("Could not initialize disposable MariaDB: " + data);
            }
            server = new ProcessBuilder(executable.toString(), "--no-defaults",
                    "--basedir=" + bin.toAbsolutePath().getParent(), "--datadir=" + data,
                    "--port=" + port, "--bind-address=127.0.0.1", "--skip-named-pipe",
                    "--skip-log-bin", "--innodb-buffer-pool-size=32M", "--console")
                    .redirectErrorStream(true).redirectOutput(work.resolve("server.log").toFile()).start();
            MariaDbTestDatabase database = new MariaDbTestDatabase(
                    "jdbc:mariadb://127.0.0.1:" + port + "/", server, null);
            Instant deadline = Instant.now().plus(Duration.ofSeconds(45));
            SQLException lastFailure = null;
            while (server.isAlive() && Instant.now().isBefore(deadline)) {
                try (Connection ignored = database.connect("")) {
                    return database;
                } catch (SQLException unavailable) {
                    lastFailure = unavailable;
                    Thread.sleep(200);
                }
            }
            throw new IllegalStateException("Disposable MariaDB did not become ready: " + data, lastFailure);
        } catch (IOException | InterruptedException ex) {
            if (server != null) {
                server.destroyForcibly();
            }
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new IllegalStateException("Failed to start disposable native MariaDB", ex);
        } catch (RuntimeException ex) {
            if (server != null) {
                server.destroyForcibly();
            }
            throw ex;
        }
    }

    public String createDatabase() {
        String name = "it_" + UUID.randomUUID().toString().replace("-", "");
        try (Connection connection = connect(""); var statement = connection.createStatement()) {
            statement.execute("CREATE DATABASE " + name + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to create disposable test schema", ex);
        }
        return serverUrl + name;
    }

    private Connection connect(String database) throws SQLException {
        return DriverManager.getConnection(serverUrl + database + "?connectTimeout=1000", username(), password());
    }

    public String username() {
        return "root";
    }

    public String password() {
        return PASSWORD;
    }

    @Override
    public void close() {
        if (container != null) {
            container.stop();
        } else if (nativeServer != null && nativeServer.isAlive()) {
            try (Connection connection = connect(""); var statement = connection.createStatement()) {
                statement.execute("SHUTDOWN");
            } catch (SQLException ignored) {
                // The server can close this connection while handling SHUTDOWN.
            }
            try {
                if (!nativeServer.waitFor(10, TimeUnit.SECONDS)) {
                    nativeServer.destroyForcibly();
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                nativeServer.destroyForcibly();
            }
        }
    }
}
