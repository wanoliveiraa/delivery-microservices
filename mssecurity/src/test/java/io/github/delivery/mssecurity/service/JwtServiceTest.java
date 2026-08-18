package io.github.delivery.mssecurity.service;

import io.github.delivery.mssecurity.model.Role;
import io.github.delivery.mssecurity.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(jwtService, "jwtSecret",
                "bXlTdXBlclNlY3JldEtleUZvckpXVEhTMjU2QXRMZWFzdDMyQ2hhcnNMb25n");
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 86400000L);

        user = User.builder()
                .id(UUID.randomUUID())
                .email("teste@teste.com")
                .role(Role.CLIENT)
                .build();
    }

    @Test
    void GenerateValidToken() {
        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void extractEmailFromToken() {
        String token = jwtService.generateToken(user);
        String email = jwtService.extractUsername(token);
        assertEquals("teste@teste.com", email);
    }

    @Test
    void validateValidToken() {
        String token = jwtService.generateToken(user);
        assertTrue(jwtService.isTokenValid(token, user));

    }

    @Test
    void rejectAnotherUsersToken() {
        String token = jwtService.generateToken(user);

        var auxUser = User.builder()
                .id(UUID.randomUUID())
                .email("outro@teste.com")
                .role(Role.CLIENT)
                .build();

        assertFalse(jwtService.isTokenValid(token, auxUser));
    }
}
