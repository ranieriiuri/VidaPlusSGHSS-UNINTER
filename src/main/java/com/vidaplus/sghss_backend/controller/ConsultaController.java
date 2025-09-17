package com.vidaplus.sghss_backend.controller;

import com.vidaplus.sghss_backend.dto.AtualizarConsultaRequest;
import com.vidaplus.sghss_backend.dto.ConsultaDTO;
import com.vidaplus.sghss_backend.dto.CriarConsultaRequest;
import com.vidaplus.sghss_backend.model.Consulta;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.repository.MedicoRepository;
import com.vidaplus.sghss_backend.repository.PacienteRepository;
import com.vidaplus.sghss_backend.service.ConsultaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultas")
@RequiredArgsConstructor
public class ConsultaController {

    private final ConsultaService consultaService;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    // Listar consultas
    @GetMapping
    public ResponseEntity<List<ConsultaDTO>> listarConsultas(@AuthenticationPrincipal Usuario usuarioLogado) {
        return ResponseEntity.ok(consultaService.listarConsultas(usuarioLogado));
    }

    // Buscar consulta por ID
    @GetMapping("/{id}")
    public ResponseEntity<ConsultaDTO> buscarConsulta(@PathVariable Long id,
                                                      @AuthenticationPrincipal Usuario usuarioLogado) {
        return ResponseEntity.ok(consultaService.buscarPorId(id, usuarioLogado));
    }

    // Criar nova consulta
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<Consulta> criarConsulta(
            @RequestBody CriarConsultaRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Consulta consulta = consultaService.criarConsulta(request, usuarioLogado);
        return ResponseEntity.ok(consulta);
    }

    // Atualizar consulta (apenas ADMIN)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Consulta> atualizarConsulta(
            @PathVariable Long id,
            @RequestBody AtualizarConsultaRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Consulta consultaAtualizada = consultaService.atualizarConsulta(id, request, usuarioLogado);
        return ResponseEntity.ok(consultaAtualizada);
    }

    // Deletar consulta (apenas ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarConsulta(@PathVariable Long id,
                                                @AuthenticationPrincipal Usuario usuarioLogado) {
        consultaService.deletarConsulta(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
