package com.vidaplus.sghss_backend.controller;

import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.service.UsuarioService;
import com.vidaplus.sghss_backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Autentica usuário
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
            );

            // Busca usuário no banco
            Usuario usuario = usuarioService.buscarPorEmail(request.getEmail());

            // Gera token JWT
            String token = jwtUtil.generateToken(usuario.getEmail());

            return ResponseEntity.ok(new JwtResponse(token));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body("Email ou senha inválidos");
        }
    }

    // DTOs
    @lombok.Data
    public static class LoginRequest {
        private String email;
        private String senha;
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    public static class JwtResponse {
        private String token;
    }

    // Cadastro de usuário
    @PostMapping("/register")
    public ResponseEntity<?> registro(@RequestBody RegistroRequest request) {
        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setSenhaHash(passwordEncoder.encode(request.getSenha()));
        usuario.setPerfil(request.getPerfil());

        usuarioService.criarUsuario(usuario);

        return ResponseEntity.ok("Usuário registrado com sucesso");
    }

    // DTO de regisatro
    @lombok.Data
    public static class RegistroRequest {
        private String email;
        private String senha;
        private String perfil; // "PACIENTE", "MEDICO", "ADMIN"
    }
}
