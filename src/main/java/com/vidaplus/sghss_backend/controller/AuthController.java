package com.vidaplus.sghss_backend.controller;

import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.security.JwtUtil;
import com.vidaplus.sghss_backend.service.UsuarioService;
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

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    // Registro público - cria apenas PACIENTE
    @PostMapping("/register")
    public ResponseEntity<?> registro(@RequestBody RegistroRequest request) {
        Usuario usuario = new Usuario();
        usuario.setEmail(request.email());
        usuario.setSenhaHash(passwordEncoder.encode(request.senha()));
        usuario.setPerfil("PACIENTE"); // sempre PACIENTE no registro público

        usuarioService.criarUsuarioPublico(usuario);

        return ResponseEntity.ok("Usuário registrado com sucesso");
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Autentica via Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.senha())
            );

            Usuario usuario = usuarioService.buscarPorEmail(request.email());
            String token = jwtUtil.generateToken(usuario.getEmail());

            return ResponseEntity.ok(new JwtResponse(token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Email ou senha inválidos");
        }
    }

    // DTOs internos
    public record RegistroRequest(String email, String senha) {}
    public record LoginRequest(String email, String senha) {}
    public record JwtResponse(String token) {}
}
