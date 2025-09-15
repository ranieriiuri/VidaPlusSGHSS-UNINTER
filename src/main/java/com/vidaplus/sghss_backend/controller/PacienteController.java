package com.vidaplus.sghss_backend.controller;

import com.vidaplus.sghss_backend.model.Paciente;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.service.PacienteService;
import com.vidaplus.sghss_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@RestController
@RequestMapping("/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;
    private final UsuarioRepository usuarioRepository;

    private Usuario getUsuarioLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado."));
    }

    @GetMapping
    public ResponseEntity<List<Paciente>> listarPacientes() {
        Usuario usuarioLogado = getUsuarioLogado();
        List<Paciente> pacientes = pacienteService.listarPacientes(usuarioLogado);
        return ResponseEntity.ok(pacientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> buscarPaciente(@PathVariable Long id) {
        Usuario usuarioLogado = getUsuarioLogado();
        Paciente paciente = pacienteService.buscarPorId(id, usuarioLogado);
        return ResponseEntity.ok(paciente);
    }

    @PostMapping
    public ResponseEntity<Paciente> cadastrarPaciente(@RequestBody Paciente paciente) {
        Usuario usuarioLogado = getUsuarioLogado();
        Paciente novoPaciente = pacienteService.cadastrarPaciente(paciente, usuarioLogado);
        return ResponseEntity.ok(novoPaciente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paciente> atualizarPaciente(@PathVariable Long id, @RequestBody Paciente pacienteAtualizado) {
        Usuario usuarioLogado = getUsuarioLogado();
        Paciente paciente = pacienteService.atualizarPaciente(id, pacienteAtualizado, usuarioLogado);
        return ResponseEntity.ok(paciente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPaciente(@PathVariable Long id) {
        Usuario usuarioLogado = getUsuarioLogado();
        pacienteService.deletarPaciente(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
