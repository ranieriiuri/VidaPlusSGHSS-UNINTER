package com.vidaplus.sghss_backend.controller;

import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.model.enums.PerfilUsuario;
import com.vidaplus.sghss_backend.security.JwtUtil;
import com.vidaplus.sghss_backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    @Operation(
            summary = "Registro de usuário",
            description = "Cria uma nova conta no sistema como PACIENTE. Endpoint público, não requer autenticação."
    )
    public ResponseEntity<?> registro(@RequestBody RegistroRequest request) {
        Usuario usuario = new Usuario();
        usuario.setEmail(request.email());
        usuario.setSenhaHash(passwordEncoder.encode(request.senha()));
        usuario.setPerfil(PerfilUsuario.PACIENTE);

        Usuario criado = usuarioService.criarUsuarioPublico(usuario);

        return ResponseEntity.ok(new UsuarioDTO(criado.getId(), criado.getEmail(), criado.getPerfil()));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login de usuário",
            description = "Realiza autenticação e retorna um token JWT. Endpoint público, não requer autenticação."
    )
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.senha())
            );

            Usuario usuario = (Usuario) auth.getPrincipal();
            String token = jwtUtil.generateToken(usuario.getEmail());

            return ResponseEntity.ok(new JwtResponse(token,
                    new UsuarioDTO(usuario.getId(), usuario.getEmail(), usuario.getPerfil())));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Email ou senha inválidos");
        }
    }

    @GetMapping("/me")
    @Operation(
            summary = "Usuário logado",
            description = "Retorna os dados do usuário autenticado. Requer token JWT.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    public ResponseEntity<?> getMe(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(new UsuarioDTO(usuario.getId(), usuario.getEmail(), usuario.getPerfil()));
    }

    // DTOs internos
    public record RegistroRequest(String email, String senha) {}
    public record LoginRequest(String email, String senha) {}
    public record JwtResponse(String token, UsuarioDTO usuario) {}
    public record UsuarioDTO(Long id, String email, PerfilUsuario perfil) {}
}
