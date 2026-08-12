package io.github.delivery.mssecurity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequestDTO(
        @NotBlank(message = "Email is required")
        @Email(message = "The email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$",
                message = "The password must contain at least 8 characters, including one uppercase letter, one lowercase letter, one number, and one special character."
        )
        String password
) {
}