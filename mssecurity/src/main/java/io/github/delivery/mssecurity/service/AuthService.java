package io.github.delivery.mssecurity.service;

import io.github.delivery.mssecurity.dto.LoginRequestDTO;
import io.github.delivery.mssecurity.dto.RegisterRequestDTO;
import io.github.delivery.mssecurity.dto.TokenResponseDTO;
import io.github.delivery.mssecurity.dto.UserResponseDTO;
import io.github.delivery.mssecurity.exception.EmailAlreadyExistsException;
import io.github.delivery.mssecurity.model.Role;
import io.github.delivery.mssecurity.model.User;
import io.github.delivery.mssecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private  final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public UserResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        var user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.CLIENT)
                .build();

        var savedUser = userRepository.save(user);

        return new UserResponseDTO(savedUser.getId(), savedUser.getEmail());
    }

    public TokenResponseDTO login(LoginRequestDTO request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password: "+ request.email()));

        var token = jwtService.generateToken(user);

        return new TokenResponseDTO(token, "Bearer");
    }
}
