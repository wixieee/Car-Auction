package edu.lpnu.auction.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public abstract class AbstractIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine")
            .withDatabaseName("auction_test_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("app.scheduling.enabled", () -> "false");
        registry.add("JWT_SECRET", () -> "fb540b54a5b09d123a626e75f957eb34d05dff98e79eee00e7fff550b4c422a3");
        registry.add("JWT_EXPIRATION", () -> "3600000");
        registry.add("OAUTH_REDIRECT_URL", () -> "http://localhost:3000/oauth2/redirect");
        registry.add("GOOGLE_CLIENT_ID", () -> "dummy");
        registry.add("GOOGLE_CLIENT_SECRET", () -> "dummy");
        registry.add("spring.cloud.gcp.core.enabled", () -> "false");
        registry.add("spring.cloud.gcp.storage.enabled", () -> "false");
    }
}