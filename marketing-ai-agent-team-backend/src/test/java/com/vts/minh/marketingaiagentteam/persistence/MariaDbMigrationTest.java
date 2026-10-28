package com.vts.minh.marketingaiagentteam.persistence;

import com.vts.minh.marketingaiagentteam.support.MariaDbTestDatabase;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MariaDbMigrationTest {
    private Flyway flyway;
    private JdbcTemplate jdbc;

    @BeforeEach
    void createIsolatedSchema() {
        MariaDbTestDatabase database = MariaDbTestDatabase.instance();
        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                database.createDatabase(), database.username(), database.password());
        jdbc = new JdbcTemplate(dataSource);
        flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/mariadb")
                .validateMigrationNaming(true)
                .baselineOnMigrate(false)
                .cleanDisabled(true)
                .load();
    }

    @Test
    void freshMigrationMatchesUserSchemaAndRerunPreservesData() {
        assertThat(flyway.migrate().migrationsExecuted).isEqualTo(1);
        jdbc.update("INSERT INTO users (full_name, email) VALUES (?, ?)",
                "Synthetic User", "schema@example.test");

        assertThat(jdbc.queryForObject("""
                SELECT CHARACTER_MAXIMUM_LENGTH FROM information_schema.columns
                WHERE table_schema = DATABASE() AND table_name = 'users' AND column_name = 'email'
                """, Integer.class)).isEqualTo(255);
        assertThat(jdbc.queryForObject("SELECT role FROM users", String.class)).isEqualTo("CUSTOMER");
        assertThat(jdbc.queryForObject("SELECT status FROM users", String.class)).isEqualTo("ACTIVE");
        assertThat(jdbc.queryForObject("SELECT id FROM users", Long.class)).isPositive();
        assertThat(jdbc.queryForObject("SELECT created_at FROM users", java.sql.Timestamp.class)).isNotNull();
        assertThat(flyway.migrate().migrationsExecuted).isZero();
        assertThat(flyway.validateWithResult().validationSuccessful).isTrue();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM users", Integer.class)).isEqualTo(1);
        assertThat(jdbc.queryForObject("""
                SELECT COUNT(*) FROM flyway_schema_history WHERE version = '1' AND success = 1
                """, Integer.class)).isEqualTo(1);
    }

    @Test
    void nonEmptySchemaWithoutHistoryIsRejectedWithoutChangingExistingRows() {
        jdbc.execute("CREATE TABLE users (id BIGINT PRIMARY KEY, full_name VARCHAR(100), email VARCHAR(100))");
        jdbc.update("INSERT INTO users VALUES (73, 'Existing Synthetic User', 'existing@example.test')");

        assertThatThrownBy(flyway::migrate).isInstanceOf(FlywayException.class)
                .hasMessageContaining("non-empty");
        assertThat(jdbc.queryForObject("SELECT id FROM users", Long.class)).isEqualTo(73L);
        assertThat(jdbc.queryForObject("SELECT email FROM users", String.class)).isEqualTo("existing@example.test");
        assertThat(jdbc.queryForObject("""
                SELECT COUNT(*) FROM information_schema.tables
                WHERE table_schema = DATABASE() AND table_name = 'flyway_schema_history'
                """, Integer.class)).isZero();
    }

    @Test
    void changedHistoryChecksumIsDetected() {
        flyway.migrate();
        jdbc.update("UPDATE flyway_schema_history SET checksum = checksum + 1 WHERE version = '1'");

        assertThat(flyway.validateWithResult().validationSuccessful).isFalse();
        assertThatThrownBy(flyway::migrate).isInstanceOf(FlywayException.class)
                .hasMessageContaining("checksum mismatch");
    }

    @Test
    void explicitBaselineOfMatchingSchemaPreservesExistingIdentityAndCredentials() throws Exception {
        try (var connection = jdbc.getDataSource().getConnection()) {
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("db/migration/mariadb/V1__create_users.sql"));
        }
        String syntheticHash = "{bcrypt}synthetic-existing-hash-not-a-real-password";
        jdbc.update("""
                INSERT INTO users (id, full_name, email, password_hash, role, status)
                VALUES (73, 'Existing Synthetic User', 'adopted@example.test', ?, 'ADMIN', 'DISABLED')
                """, syntheticHash);

        // Only an explicitly reviewed matching schema is eligible for manual adoption.
        flyway.baseline();
        assertThat(flyway.migrate().migrationsExecuted).isZero();
        assertThat(flyway.validateWithResult().validationSuccessful).isTrue();
        assertThat(jdbc.queryForObject("SELECT id FROM users", Long.class)).isEqualTo(73L);
        assertThat(jdbc.queryForObject("SELECT password_hash FROM users", String.class)).isEqualTo(syntheticHash);
        assertThat(jdbc.queryForObject("SELECT role FROM users", String.class)).isEqualTo("ADMIN");
        assertThat(jdbc.queryForObject("SELECT status FROM users", String.class)).isEqualTo("DISABLED");
        jdbc.update("INSERT INTO users (full_name, email) VALUES ('Next Synthetic User', 'next@example.test')");
        assertThat(jdbc.queryForObject("SELECT id FROM users WHERE email = 'next@example.test'", Long.class))
                .isGreaterThan(73L);
    }
}
