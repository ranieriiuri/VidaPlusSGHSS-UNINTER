package com.vidaplus.sghss_backend.controller;

import com.vidaplus.sghss_backend.model.Medico;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.service.MedicoService;
import com.vidaplus.sghss_backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicos")
@RequiredArgsConstructor
public class MedicoController {

    private final MedicoService medicoService;
    private final UsuarioService usuarioService;

    /**
     * Listar todos os médicos
     * ADMIN e MEDICO podem listar
     */
    @GetMapping
    public ResponseEntity<List<Medico>> listarMedicos(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        List<Medico> medicos = medicoService.listarMedicos(usuarioLogado);
        return ResponseEntity.ok(medicos);
    }

    /**
     * Buscar médico por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Medico> buscarMedico(@PathVariable Long id,
                                               @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        Medico medico = medicoService.buscarPorId(id, usuarioLogado);
        return ResponseEntity.ok(medico);
    }

    /**
     * Criar médico
     * Apenas ADMIN
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Medico> criarMedico(@RequestBody Medico medico,
                                              @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        Medico novoMedico = medicoService.criarMedico(medico, usuarioLogado);
        return ResponseEntity.ok(novoMedico);
    }

    /**
     * Atualizar médico
     * ADMIN pode atualizar qualquer um
     * MEDICO só pode atualizar ele mesmo
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<Medico> atualizarMedico(@PathVariable Long id,
                                                  @RequestBody Medico medicoAtualizado,
                                                  @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        Medico medico = medicoService.atualizarMedico(id, medicoAtualizado, usuarioLogado);
        return ResponseEntity.ok(medico);
    }

    /**
     * Deletar médico
     * Apenas ADMIN
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarMedico(@PathVariable Long id,
                                              @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        medicoService.deletarMedico(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
