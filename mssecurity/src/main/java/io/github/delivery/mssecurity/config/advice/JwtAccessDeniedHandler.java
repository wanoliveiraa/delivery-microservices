package io.github.delivery.mssecurity.config.advice;

import io.github.delivery.mssecurity.dto.ErrorResponseDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        var errorResponse = new ErrorResponseDTO(LocalDateTime.now(),403,"Forbidden","You do not have permission to access this resource");
        response.setStatus(403);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
