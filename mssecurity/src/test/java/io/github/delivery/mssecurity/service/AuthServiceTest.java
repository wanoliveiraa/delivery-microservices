package io.github.delivery.mssecurity.service;

import io.github.delivery.mssecurity.dto.LoginRequestDTO;
import io.github.delivery.mssecurity.dto.RegisterRequestDTO;
import io.github.delivery.mssecurity.exception.EmailAlreadyExistsException;
import io.github.delivery.mssecurity.model.Role;
import io.github.delivery.mssecurity.model.User;
import io.github.delivery.mssecurity.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;


    @Test
    void registerValidUser() {

        var request = new RegisterRequestDTO("novo@teste.com", "Senha@123");

        when(userRepository.existsByEmail("novo@teste.com")).thenReturn(false);

        when(passwordEncoder.encode("Senha@123")).thenReturn("encodedPassword");


        var savedUser = User.builder()
                .id(UUID.randomUUID())
                .email("novo@teste.com")
                .password("encodedPassword")
                .role(Role.CLIENT)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        var response = authService.register(request);

        assertNotNull(response);
        assertEquals("novo@teste.com", response.email());
        assertNotNull(response.id());

        verify(userRepository).save(any(User.class));

    }

    @Test
    void throwExceptionWhenEmailAlreadyExists() {

        var request = new RegisterRequestDTO("existe@teste.com", "Senha@123");
        when(userRepository.existsByEmail("existe@teste.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> {
            authService.register(request);
        });

        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void loginValidUser() {

        var request = new LoginRequestDTO("login@teste.com", "Senha@123");

        when(authenticationManager.authenticate(any())).thenReturn(null);


        var user = User.builder()
                .id(UUID.randomUUID())
                .email("login@teste.com")
                .role(Role.CLIENT)
                .build();
        when(userRepository.findByEmail("login@teste.com")).thenReturn(Optional.of(user));

        when(jwtService.generateToken(user)).thenReturn("token-falso-123");

        var response = authService.login(request);

        assertNotNull(response);
        assertEquals("token-falso-123", response.token());
        assertEquals("Bearer", response.type());

    }

    @Test
    void loginInvalidCredentials() {
        var request = new LoginRequestDTO("login@teste.com", "SenhaErrada");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> {
            authService.login(request);
        });

        verify(jwtService, never()).generateToken(any());
    }
}
