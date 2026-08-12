package io.github.delivery.mssecurity.config.advice;

import io.github.delivery.mssecurity.dto.ErrorResponseDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        var errorResponse = new ErrorResponseDTO(LocalDateTime.now(), 401, "Unauthorized", "Token missing or invalid");
        response.setStatus(401);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
