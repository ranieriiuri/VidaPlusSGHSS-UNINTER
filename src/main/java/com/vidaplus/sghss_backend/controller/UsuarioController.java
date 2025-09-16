package com.vidaplus.sghss_backend.controller;

import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Criar usuário (ADMIN ou MEDICO)
    @PostMapping
    public ResponseEntity<UsuarioDTO> criarUsuario(@RequestBody Usuario usuario,
                                                   @AuthenticationPrincipal Usuario usuarioLogado) {
        Usuario novoUsuario = usuarioService.criarUsuario(usuario, usuarioLogado);
        return ResponseEntity.ok(new UsuarioDTO(novoUsuario.getId(), novoUsuario.getEmail(), novoUsuario.getPerfil()));
    }

    // Atualizar usuário
    @PutMapping("/{email}")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(@PathVariable String email,
                                                       @RequestBody Usuario usuarioAtualizado,
                                                       @AuthenticationPrincipal Usuario usuarioLogado) {
        usuarioAtualizado.setEmail(email);
        Usuario atualizado = usuarioService.atualizarUsuario(usuarioAtualizado, usuarioLogado);
        return ResponseEntity.ok(new UsuarioDTO(atualizado.getId(), atualizado.getEmail(), atualizado.getPerfil()));
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
        if ("PACIENTE".equals(usuarioLogado.getPerfil()) && !usuarioLogado.getEmail().equals(email)) {
            return ResponseEntity.status(403).build();
        }

        Usuario usuario = usuarioService.buscarPorEmail(email);
        return ResponseEntity.ok(new UsuarioDTO(usuario.getId(), usuario.getEmail(), usuario.getPerfil()));
    }

    // Listar todos usuários (ADMIN)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.listarTodosUsuarios().stream()
                .map(u -> new UsuarioDTO(u.getId(), u.getEmail(), u.getPerfil()))
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    // DTO interno
    public record UsuarioDTO(Long id, String email, String perfil) {}
}
