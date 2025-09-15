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

    @GetMapping
    public ResponseEntity<List<Medico>> listarMedicos(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        List<Medico> medicos = medicoService.listarMedicos(usuarioLogado);
        return ResponseEntity.ok(medicos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Medico> buscarMedico(@PathVariable Long id,
                                               @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        Medico medico = medicoService.buscarPorId(id, usuarioLogado);
        return ResponseEntity.ok(medico);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Medico> criarMedico(@RequestBody Medico medico,
                                              @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        Medico novoMedico = medicoService.criarMedico(medico, usuarioLogado);
        return ResponseEntity.ok(novoMedico);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<Medico> atualizarMedico(@PathVariable Long id,
                                                  @RequestBody Medico medicoAtualizado,
                                                  @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        Medico medico = medicoService.atualizarMedico(id, medicoAtualizado, usuarioLogado);
        return ResponseEntity.ok(medico);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarMedico(@PathVariable Long id,
                                              @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        medicoService.deletarMedico(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
