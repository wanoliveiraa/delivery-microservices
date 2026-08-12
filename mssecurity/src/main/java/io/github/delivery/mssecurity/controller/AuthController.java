package io.github.delivery.mssecurity.controller;

import io.github.delivery.mssecurity.dto.LoginRequestDTO;
import io.github.delivery.mssecurity.dto.RegisterRequestDTO;
import io.github.delivery.mssecurity.dto.TokenResponseDTO;
import io.github.delivery.mssecurity.dto.UserResponseDTO;
import io.github.delivery.mssecurity.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Cadastra um novo usuário",
            description = "Cria um novo usuário a partir dos dados informados."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de cadastro inválidos"),
            @ApiResponse(responseCode = "409", description = "Usuário já cadastrado")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody RegisterRequestDTO request) {
        var created = authService.register(request);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(
            summary = "Autentica um usuário",
            description = "Autentica o usuário com as credenciais informadas e retorna um token de acesso."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário autenticado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de autenticação inválidos"),
            @ApiResponse(responseCode = "409", description = "Credenciais inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO request) {
        var token = authService.login(request);
        return ResponseEntity.ok(token);
    }
}
