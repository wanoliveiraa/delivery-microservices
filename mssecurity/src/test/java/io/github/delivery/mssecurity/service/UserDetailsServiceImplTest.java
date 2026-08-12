package io.github.delivery.mssecurity.service;

import io.github.delivery.mssecurity.model.Role;
import io.github.delivery.mssecurity.model.User;
import io.github.delivery.mssecurity.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsernameWhenUserExists() {
        var user = User.builder()
                .id(UUID.randomUUID())
                .email("existe@teste.com")
                .password("hashFalso")
                .role(Role.CLIENT)
                .build();
        when(userRepository.findByEmail("existe@teste.com")).thenReturn(Optional.of(user));

        var result = userDetailsService.loadUserByUsername("existe@teste.com");

        assertNotNull(result);
        assertEquals("existe@teste.com", result.getUsername());
    }

    @Test
    void loadUserByUsernameWhenUserNotFound() {
        when(userRepository.findByEmail("naoexiste@teste.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("naoexiste@teste.com");
        });
    }
}
