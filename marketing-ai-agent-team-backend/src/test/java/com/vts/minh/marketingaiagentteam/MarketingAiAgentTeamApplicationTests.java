package com.vts.minh.marketingaiagentteam;

import com.vts.minh.marketingaiagentteam.support.MariaDbTestDatabase;
import com.vts.minh.marketingaiagentteam.user.entity.User;
import com.vts.minh.marketingaiagentteam.user.enums.UserStatus;
import com.vts.minh.marketingaiagentteam.user.repository.UserRepository;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"spring.config.import=", "spring.jpa.show-sql=false"})
@AutoConfigureMockMvc
class MarketingAiAgentTeamApplicationTests {
    private static final MariaDbTestDatabase DATABASE = MariaDbTestDatabase.instance();
    private static final String APPLICATION_DATABASE = DATABASE.createDatabase();
    private static final String PASSWORD = "Synthetic-password-123";

    @DynamicPropertySource
    static void isolatedProperties(DynamicPropertyRegistry properties) {
        properties.add("spring.datasource.url", () -> APPLICATION_DATABASE);
        properties.add("spring.datasource.username", DATABASE::username);
        properties.add("spring.datasource.password", DATABASE::password);
        properties.add("spring.app.jwt.secret-base64", () -> Base64.getEncoder().encodeToString(
                "synthetic-jwt-test-key-32-bytes-or-more".getBytes(StandardCharsets.UTF_8)));
    }

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired UserRepository users;
    @Autowired PasswordEncoder passwords;
    @Autowired JwtDecoder decoder;
    @Autowired Flyway flyway;

    @Test
    void contextLoadsWithFlywayAndHibernateValidation() {
        assertThat(flyway.info().current().getVersion().getVersion()).isEqualTo("1");
        assertThat(flyway.validateWithResult().validationSuccessful).isTrue();
        assertThat(flyway.migrate().migrationsExecuted).isZero();
    }

    @Test
    void registerLoginAndMePreserveIdentityHashAndJwtRole() throws Exception {
        String email = uniqueEmail();
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(registration("  Nguyễn Synthetic  ", email.toUpperCase())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("email").value(email))
                .andExpect(jsonPath("fullName").value("Nguyễn Synthetic"))
                .andExpect(jsonPath("role").value("CUSTOMER"))
                .andExpect(jsonPath("passwordHash").doesNotExist());

        User saved = users.findByEmail(email).orElseThrow();
        assertThat(saved.getId()).isPositive();
        assertThat(saved.getPasswordHash()).isNotEqualTo(PASSWORD);
        assertThat(passwords.matches(PASSWORD, saved.getPasswordHash())).isTrue();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();

        String body = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(Map.of("email", email.toUpperCase(), "password", PASSWORD))))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String token = mapper.readTree(body).get("accessToken").asString();
        assertThat(decoder.decode(token).getSubject()).isEqualTo(saved.getId().toString());
        assertThat(decoder.decode(token).getClaimAsString("role")).isEqualTo("CUSTOMER");
        mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("userId").value(saved.getId().toString()))
                .andExpect(jsonPath("role").value("CUSTOMER"));
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(Map.of("email", email, "password", "Wrong-synthetic-password"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registrationPersistsEmailLongerThanLegacySqlLimit() throws Exception {
        String email = "synthetic-" + UUID.randomUUID() + "@" + "a".repeat(50)
                + "." + "b".repeat(50) + ".example.test";
        assertThat(email.length()).isBetween(101, 255);
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(registration("Long Email Synthetic", email)))
                .andExpect(status().isCreated());
        assertThat(users.findByEmail(email)).isPresent();
    }

    @Test
    void duplicateRegistrationReturnsConflict() throws Exception {
        String email = uniqueEmail();
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(registration("Synthetic", email))).andExpect(status().isCreated());
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(registration("Duplicate", email.toUpperCase()))).andExpect(status().isConflict());
    }

    @Test
    void concurrentDuplicateRegistrationCreatesOnlyOneUser() throws Exception {
        String email = uniqueEmail();
        String payload = registration("Concurrent Synthetic", email);
        CountDownLatch start = new CountDownLatch(1);
        Callable<Integer> request = () -> {
            assertThat(start.await(10, TimeUnit.SECONDS)).isTrue();
            return mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(payload))
                    .andReturn().getResponse().getStatus();
        };
        try (var executor = Executors.newFixedThreadPool(2)) {
            var first = executor.submit(request);
            var second = executor.submit(request);
            start.countDown();
            assertThat(java.util.List.of(first.get(30, TimeUnit.SECONDS), second.get(30, TimeUnit.SECONDS)))
                    .containsExactlyInAnyOrder(201, 409);
        }
        assertThat(users.findAll().stream().filter(user -> user.getEmail().equals(email))).hasSize(1);
    }

    @Test
    void loginRejectsBlockedUsersAndUsersWithoutPassword() throws Exception {
        User blocked = new User("Blocked Synthetic", uniqueEmail());
        blocked.setPasswordHash(passwords.encode(PASSWORD));
        blocked.setStatus(UserStatus.BLOCKED);
        users.saveAndFlush(blocked);
        User passwordless = users.saveAndFlush(new User("Passwordless Synthetic", uniqueEmail()));
        for (User user : java.util.List.of(blocked, passwordless)) {
            mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(Map.of("email", user.getEmail(), "password", PASSWORD))))
                    .andExpect(status().isUnauthorized());
        }
        mvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
    }

    private String registration(String fullName, String email) {
        return mapper.writeValueAsString(Map.of("fullName", fullName, "email", email, "password", PASSWORD));
    }

    private static String uniqueEmail() {
        return "synthetic-" + UUID.randomUUID() + "@example.test";
    }
}
