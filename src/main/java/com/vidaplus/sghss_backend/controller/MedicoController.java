package com.vidaplus.sghss_backend.controller;

import com.vidaplus.sghss_backend.dto.MedicoDTO;
import com.vidaplus.sghss_backend.dto.MedicoRespostaDTO;
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

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<List<MedicoRespostaDTO>> listarMedicos(@AuthenticationPrincipal Usuario usuarioLogado) {
        List<MedicoRespostaDTO> medicos = medicoService.listarMedicos(usuarioLogado);
        return ResponseEntity.ok(medicos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<MedicoDTO> buscarMedico(@PathVariable Long id,
                                                  @AuthenticationPrincipal Usuario usuarioLogado) {
        MedicoDTO medico = medicoService.buscarPorId(id, usuarioLogado);
        return ResponseEntity.ok(medico);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Medico> criarMedico(@RequestBody Medico medico,
                                              @AuthenticationPrincipal Usuario usuarioLogado) {
        Medico novoMedico = medicoService.criarMedico(medico, usuarioLogado);
        return ResponseEntity.ok(novoMedico);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<Medico> atualizarMedico(@PathVariable Long id,
                                                  @RequestBody Medico medicoAtualizado,
                                                  @AuthenticationPrincipal Usuario usuarioLogado) {
        Medico medico = medicoService.atualizarMedico(id, medicoAtualizado, usuarioLogado);
        return ResponseEntity.ok(medico);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarMedico(@PathVariable Long id,
                                              @AuthenticationPrincipal Usuario usuarioLogado) {
        medicoService.deletarMedico(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
