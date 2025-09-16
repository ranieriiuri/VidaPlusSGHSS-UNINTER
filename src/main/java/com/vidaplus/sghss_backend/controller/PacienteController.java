package com.vidaplus.sghss_backend.controller;

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

    /**
     * Listar pacientes
     * ADMIN e MEDICO veem todos
     * PACIENTE vê apenas ele mesmo
     */
    @GetMapping
    public ResponseEntity<List<Paciente>> listarPacientes(@AuthenticationPrincipal Usuario usuarioLogado) {
        return ResponseEntity.ok(pacienteService.listarPacientes(usuarioLogado));
    }

    /**
     * Buscar paciente por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Paciente> buscarPaciente(@PathVariable Long id,
                                                   @AuthenticationPrincipal Usuario usuarioLogado) {
        return ResponseEntity.ok(pacienteService.buscarPorId(id, usuarioLogado));
    }

    /**
     * Cadastrar paciente
     * Apenas ADMIN ou MEDICO
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<Paciente> cadastrarPaciente(@RequestBody Paciente paciente,
                                                      @AuthenticationPrincipal Usuario usuarioLogado) {
        return ResponseEntity.ok(pacienteService.cadastrarPaciente(paciente, usuarioLogado));
    }

    /**
     * Atualizar paciente
     * Apenas ADMIN ou MEDICO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<Paciente> atualizarPaciente(@PathVariable Long id,
                                                      @RequestBody Paciente pacienteAtualizado,
                                                      @AuthenticationPrincipal Usuario usuarioLogado) {
        return ResponseEntity.ok(pacienteService.atualizarPaciente(id, pacienteAtualizado, usuarioLogado));
    }

    /**
     * Deletar paciente
     * Apenas ADMIN
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarPaciente(@PathVariable Long id,
                                                @AuthenticationPrincipal Usuario usuarioLogado) {
        pacienteService.deletarPaciente(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
