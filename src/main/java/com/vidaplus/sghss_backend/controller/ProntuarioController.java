package com.vidaplus.sghss_backend.controller;

import com.vidaplus.sghss_backend.model.Prontuario;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.service.ProntuarioService;
import com.vidaplus.sghss_backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prontuarios")
@RequiredArgsConstructor
public class ProntuarioController {

    private final ProntuarioService prontuarioService;
    private final UsuarioService usuarioService;

    /**
     * Listar todos os prontuários
     */
    @GetMapping
    public ResponseEntity<List<Prontuario>> listarProntuarios(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        List<Prontuario> prontuarios = prontuarioService.listarProntuarios(usuarioLogado);
        return ResponseEntity.ok(prontuarios);
    }

    /**
     * Buscar prontuário por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Prontuario> buscarProntuario(
            @PathVariable Long id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        Prontuario prontuario = prontuarioService.buscarPorId(id, usuarioLogado);
        return ResponseEntity.ok(prontuario);
    }

    /**
     * Criar novo prontuário
     * Apenas ADMIN ou MEDICO
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<Prontuario> criarProntuario(
            @RequestBody Prontuario prontuario,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        Prontuario novoProntuario = prontuarioService.criarProntuario(prontuario, usuarioLogado);
        return ResponseEntity.ok(novoProntuario);
    }

    /**
     * Atualizar prontuário
     * Apenas ADMIN ou MEDICO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<Prontuario> atualizarProntuario(
            @PathVariable Long id,
            @RequestBody Prontuario prontuarioAtualizado,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        Prontuario prontuario = prontuarioService.atualizarProntuario(id, prontuarioAtualizado, usuarioLogado);
        return ResponseEntity.ok(prontuario);
    }

    /**
     * Deletar prontuário
     * Apenas ADMIN
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarProntuario(
            @PathVariable Long id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        prontuarioService.deletarProntuario(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
