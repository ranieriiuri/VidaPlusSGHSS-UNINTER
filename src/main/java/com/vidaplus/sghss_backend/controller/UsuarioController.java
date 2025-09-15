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

    /**
     * Criar usuário
     * ADMIN pode criar qualquer usuário
     * MEDICO pode criar PACIENTE
     */
    @PostMapping
    public ResponseEntity<Usuario> criarUsuario(@RequestBody Usuario usuario,
                                                @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        Usuario novoUsuario = usuarioService.criarUsuario(usuario, usuarioLogado);
        return ResponseEntity.ok(novoUsuario);
    }

    /**
     * Atualizar usuário
     */
    @PutMapping("/{email}")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable String email,
                                                    @RequestBody Usuario usuarioAtualizado,
                                                    @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());

        // Certifica-se de atualizar o usuário correto
        usuarioAtualizado.setEmail(email);

        Usuario usuario = usuarioService.atualizarUsuario(usuarioAtualizado, usuarioLogado);
        return ResponseEntity.ok(usuario);
    }

    /**
     * Deletar usuário
     * Apenas ADMIN
     */
    @DeleteMapping("/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarUsuario(@PathVariable String email,
                                               @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        usuarioService.deletarUsuario(email, usuarioLogado);
        return ResponseEntity.noContent().build();
    }

    /**
     * Buscar usuário pelo email
     */
    @GetMapping("/{email}")
    public ResponseEntity<Usuario> buscarUsuario(@PathVariable String email,
                                                 @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());

        // Paciente só pode ver ele mesmo
        if ("PACIENTE".equals(usuarioLogado.getPerfil()) && !usuarioLogado.getEmail().equals(email)) {
            return ResponseEntity.status(403).build();
        }

        Usuario usuario = usuarioService.buscarPorEmail(email);
        return ResponseEntity.ok(usuario);
    }

    /**
     * Listar todos usuários
     * Apenas ADMIN
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarTodosUsuarios();
        return ResponseEntity.ok(usuarios);
    }
}
