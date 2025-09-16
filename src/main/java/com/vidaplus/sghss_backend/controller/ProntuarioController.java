package com.vidaplus.sghss_backend.controller;

import com.vidaplus.sghss_backend.dto.CriarProntuarioRequest;
import com.vidaplus.sghss_backend.dto.ProntuarioDTO;
import com.vidaplus.sghss_backend.model.Paciente;
import com.vidaplus.sghss_backend.model.Prontuario;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.repository.PacienteRepository;
import com.vidaplus.sghss_backend.service.ProntuarioService;
import jakarta.persistence.EntityNotFoundException;
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
    private final PacienteRepository pacienteRepository;

    // Listar todos os prontuários
    @GetMapping
    public ResponseEntity<List<ProntuarioDTO>> listarProntuarios(
            @AuthenticationPrincipal Usuario usuarioLogado) {

        List<ProntuarioDTO> prontuarios = prontuarioService.listarProntuarios(usuarioLogado)
                .stream()
                .map(ProntuarioDTO::from)
                .toList();
        return ResponseEntity.ok(prontuarios);
    }

    // Buscar prontuário por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProntuarioDTO> buscarProntuario(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Prontuario prontuario = prontuarioService.buscarPorId(id, usuarioLogado);
        return ResponseEntity.ok(ProntuarioDTO.from(prontuario));
    }

    // Criar novo prontuário
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<ProntuarioDTO> criarProntuario(
            @RequestBody CriarProntuarioRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        // Busca paciente do banco (entidade gerenciada)
        Paciente paciente = pacienteRepository.findById(request.pacienteId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado."));

        // Cria prontuário e associa ao paciente
        Prontuario prontuario = new Prontuario();
        prontuario.setRegistros(request.registros());
        prontuario.setPrescricoes(request.prescricoes());
        prontuario.setPaciente(paciente);

        Prontuario novoProntuario = prontuarioService.criarProntuario(prontuario, usuarioLogado);
        return ResponseEntity.ok(ProntuarioDTO.from(novoProntuario));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<ProntuarioDTO> atualizarProntuario(
            @PathVariable Long id,
            @RequestBody CriarProntuarioRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Prontuario prontuarioAtualizado = prontuarioService.atualizarProntuario(id, request, usuarioLogado);
        return ResponseEntity.ok(ProntuarioDTO.from(prontuarioAtualizado));
    }


    // Deletar prontuário
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarProntuario(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        prontuarioService.deletarProntuario(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
