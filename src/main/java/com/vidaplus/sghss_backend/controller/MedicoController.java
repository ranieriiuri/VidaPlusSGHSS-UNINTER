package com.vidaplus.sghss_backend.controller;

import com.vidaplus.sghss_backend.dto.MedicoDTO;
import com.vidaplus.sghss_backend.model.Medico;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.service.MedicoService;
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

    /**
     * Listar todos os médicos
     * ADMIN e MEDICO podem listar; PACIENTE não
     */
    @GetMapping
    public ResponseEntity<List<MedicoDTO>> listarMedicos(@AuthenticationPrincipal Usuario usuarioLogado) {
        List<MedicoDTO> medicos = medicoService.listarMedicos(usuarioLogado);
        return ResponseEntity.ok(medicos);
    }

    /**
     * Buscar médico por ID
     * ADMIN pode qualquer médico; MEDICO só pode o próprio; PACIENTE não
     */
    @GetMapping("/{id}")
    public ResponseEntity<MedicoDTO> buscarMedico(@PathVariable Long id,
                                                  @AuthenticationPrincipal Usuario usuarioLogado) {
        MedicoDTO medico = medicoService.buscarPorId(id, usuarioLogado);
        return ResponseEntity.ok(medico);
    }


    /**
     * Criar médico
     * Apenas ADMIN
     * O JSON enviado deve conter usuario.id válido (não paciente e não vinculado a outro médico)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Medico> criarMedico(@RequestBody Medico medico,
                                              @AuthenticationPrincipal Usuario usuarioLogado) {
        Medico novoMedico = medicoService.criarMedico(medico, usuarioLogado);
        return ResponseEntity.ok(novoMedico);
    }

    /**
     * Atualizar médico
     * ADMIN pode atualizar qualquer médico
     * MEDICO só pode atualizar seus próprios dados
     * Não é possível alterar o usuário associado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<Medico> atualizarMedico(@PathVariable Long id,
                                                  @RequestBody Medico medicoAtualizado,
                                                  @AuthenticationPrincipal Usuario usuarioLogado) {
        Medico medico = medicoService.atualizarMedico(id, medicoAtualizado, usuarioLogado);
        return ResponseEntity.ok(medico);
    }

    /**
     * Deletar médico
     * Apenas ADMIN
     * Desvincula o usuário antes de deletar para evitar erro de TransientObjectException
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarMedico(@PathVariable Long id,
                                              @AuthenticationPrincipal Usuario usuarioLogado) {
        medicoService.deletarMedico(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
