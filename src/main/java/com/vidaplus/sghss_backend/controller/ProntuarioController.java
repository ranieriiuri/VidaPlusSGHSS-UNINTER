package com.vidaplus.sghss_backend.controller;

import com.vidaplus.sghss_backend.dto.CriarProntuarioRequest;
import com.vidaplus.sghss_backend.dto.ProntuarioDTO;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.service.ProntuarioService;
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

    // Listar todos os prontuários (ADMIN/MEDICO) ou apenas do paciente logado
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<List<ProntuarioDTO>> listarProntuarios(
            @AuthenticationPrincipal Usuario usuarioLogado) {

        List<ProntuarioDTO> prontuarios = prontuarioService.listarProntuarios(usuarioLogado);
        return ResponseEntity.ok(prontuarios);
    }

    // Buscar prontuário por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProntuarioDTO> buscarProntuario(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        ProntuarioDTO prontuario = prontuarioService.buscarPorId(id, usuarioLogado);
        return ResponseEntity.ok(prontuario);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<ProntuarioDTO> criarProntuario(
            @RequestBody CriarProntuarioRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        ProntuarioDTO novoProntuario = prontuarioService.criarProntuario(request, usuarioLogado);
        return ResponseEntity.ok(novoProntuario);
    }

    // Atualizar prontuário (ADMIN ou MEDICO)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<ProntuarioDTO> atualizarProntuario(
            @PathVariable Long id,
            @RequestBody CriarProntuarioRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        // Agora o service retorna ProntuarioDTO direto
        ProntuarioDTO prontuarioAtualizado = prontuarioService.atualizarProntuario(id, request, usuarioLogado);
        return ResponseEntity.ok(prontuarioAtualizado);
    }


    // Deletar prontuário (apenas ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarProntuario(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        prontuarioService.deletarProntuario(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
