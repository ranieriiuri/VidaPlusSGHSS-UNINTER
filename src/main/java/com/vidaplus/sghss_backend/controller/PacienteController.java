package com.vidaplus.sghss_backend.controller;

import com.vidaplus.sghss_backend.dto.AtualizarPacienteRequest;
import com.vidaplus.sghss_backend.model.Paciente;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.service.PacienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;

    @GetMapping
    public ResponseEntity<List<Paciente>> listarPacientes(@AuthenticationPrincipal Usuario usuarioLogado) {
        return ResponseEntity.ok(pacienteService.listarPacientes(usuarioLogado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> buscarPaciente(@PathVariable Long id,
                                                   @AuthenticationPrincipal Usuario usuarioLogado) {
        return ResponseEntity.ok(pacienteService.buscarPorId(id, usuarioLogado));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<Paciente> cadastrarPaciente(@RequestBody Paciente paciente,
                                                      @AuthenticationPrincipal Usuario usuarioLogado) {
        return ResponseEntity.ok(pacienteService.cadastrarPaciente(paciente, usuarioLogado));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<Paciente> atualizarPaciente(
            @PathVariable Long id,
            @RequestBody AtualizarPacienteRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Paciente pacienteAtualizado = pacienteService.atualizarPaciente(id, request, usuarioLogado);
        return ResponseEntity.ok(pacienteAtualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarPaciente(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        pacienteService.deletarPaciente(id, usuarioLogado);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
