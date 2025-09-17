package com.vidaplus.sghss_backend.controller;

import com.vidaplus.sghss_backend.dto.AtualizarUsuarioRequest;
import com.vidaplus.sghss_backend.dto.UsuarioRequest;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.model.enums.PerfilUsuario;
import com.vidaplus.sghss_backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Criar usuário (ADMIN ou MEDICO)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioDTO> criarUsuario(
            @RequestBody UsuarioRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Usuario novoUsuario = usuarioService.criarUsuario(request, usuarioLogado);

        return ResponseEntity.ok(new UsuarioDTO(
                novoUsuario.getId(),
                novoUsuario.getEmail(),
                novoUsuario.getPerfil().name()
        ));
    }

    // Atualizar usuário
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(
            @PathVariable Long id,
            @RequestBody AtualizarUsuarioRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Usuario atualizado = usuarioService.atualizarUsuario(id, request, usuarioLogado);

        return ResponseEntity.ok(new UsuarioDTO(
                atualizado.getId(),
                atualizado.getEmail(),
                atualizado.getPerfil().name()
        ));
    }

    // Deletar usuário (ADMIN)
    @DeleteMapping("/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarUsuario(@PathVariable String email,
                                               @AuthenticationPrincipal Usuario usuarioLogado) {
        usuarioService.deletarUsuario(email, usuarioLogado);
        return ResponseEntity.noContent().build();
    }

    // Buscar usuário
    @GetMapping("/{email}")
    public ResponseEntity<UsuarioDTO> buscarUsuario(@PathVariable String email,
                                                    @AuthenticationPrincipal Usuario usuarioLogado) {
        // PACIENTE só pode ver ele mesmo
        if (PerfilUsuario.PACIENTE.equals(usuarioLogado.getPerfil()) &&
                !usuarioLogado.getEmail().equals(email)) {
            return ResponseEntity.status(403).build();
        }

        Usuario usuario = usuarioService.buscarPorEmail(email);
        return ResponseEntity.ok(new UsuarioDTO(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getPerfil().name()
        ));
    }

    // Listar todos usuários (ADMIN)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.listarTodosUsuarios().stream()
                .map(u -> new UsuarioDTO(
                        u.getId(),
                        u.getEmail(),
                        u.getPerfil().name()
                ))
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    // DTO interno
    public record UsuarioDTO(Long id, String email, String perfil) {}
}
