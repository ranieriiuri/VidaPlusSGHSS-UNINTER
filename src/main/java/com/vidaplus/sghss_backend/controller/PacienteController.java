package com.vidaplus.sghss_backend.controller;

import com.vidaplus.sghss_backend.model.Paciente;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.service.PacienteService;
import com.vidaplus.sghss_backend.service.UsuarioService; // para buscar Usuario logado
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
    private final UsuarioService usuarioService; // assume que existe método para buscar usuario pelo email do login

    /**
     * Listar pacientes
     */
    @GetMapping
    public ResponseEntity<List<Paciente>> listarPacientes(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        List<Paciente> pacientes = pacienteService.listarPacientes(usuarioLogado);
        return ResponseEntity.ok(pacientes);
    }

    /**
     * Buscar paciente por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Paciente> buscarPaciente(@PathVariable Long id,
                                                   @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        Paciente paciente = pacienteService.buscarPorId(id, usuarioLogado);
        return ResponseEntity.ok(paciente);
    }

    /**
     * Cadastrar paciente
     * Apenas ADMIN ou MEDICO
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<Paciente> cadastrarPaciente(@RequestBody Paciente paciente,
                                                      @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        Paciente novoPaciente = pacienteService.cadastrarPaciente(paciente, usuarioLogado);
        return ResponseEntity.ok(novoPaciente);
    }

    /**
     * Atualizar paciente
     * Apenas ADMIN ou MEDICO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<Paciente> atualizarPaciente(@PathVariable Long id,
                                                      @RequestBody Paciente pacienteAtualizado,
                                                      @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        Paciente paciente = pacienteService.atualizarPaciente(id, pacienteAtualizado, usuarioLogado);
        return ResponseEntity.ok(paciente);
    }

    /**
     * Deletar paciente
     * Apenas ADMIN
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarPaciente(@PathVariable Long id,
                                                @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        pacienteService.deletarPaciente(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
