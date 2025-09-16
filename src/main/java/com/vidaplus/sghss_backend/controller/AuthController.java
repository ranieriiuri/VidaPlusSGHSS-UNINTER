package com.vidaplus.sghss_backend.controller;

import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.security.JwtUtil;
import com.vidaplus.sghss_backend.service.UsuarioService;
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

    // Registro público - PACIENTE
    @PostMapping("/register")
    public ResponseEntity<?> registro(@RequestBody RegistroRequest request) {
        Usuario usuario = new Usuario();
        usuario.setEmail(request.email());
        usuario.setSenhaHash(passwordEncoder.encode(request.senha()));
        usuario.setPerfil("PACIENTE");

        Usuario criado = usuarioService.criarUsuarioPublico(usuario);

        return ResponseEntity.ok(new UsuarioDTO(criado.getId(), criado.getEmail(), criado.getPerfil()));
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.senha())
            );

            Usuario usuario = (Usuario) auth.getPrincipal();
            String token = jwtUtil.generateToken(usuario.getEmail());

            return ResponseEntity.ok(new JwtResponse(token, new UsuarioDTO(usuario.getId(), usuario.getEmail(), usuario.getPerfil())));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Email ou senha inválidos");
        }
    }

    // DTOs
    public record RegistroRequest(String email, String senha) {}
    public record LoginRequest(String email, String senha) {}
    public record JwtResponse(String token, UsuarioDTO usuario) {}
    public record UsuarioDTO(Long id, String email, String perfil) {}
}
