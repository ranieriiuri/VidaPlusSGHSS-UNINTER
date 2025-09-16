package com.vidaplus.sghss_backend.controller;

import com.vidaplus.sghss_backend.dto.ConsultaDTO;
import com.vidaplus.sghss_backend.dto.CriarConsultaRequest;
import com.vidaplus.sghss_backend.model.Consulta;
import com.vidaplus.sghss_backend.model.Medico;
import com.vidaplus.sghss_backend.model.Paciente;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.repository.MedicoRepository;
import com.vidaplus.sghss_backend.repository.PacienteRepository;
import com.vidaplus.sghss_backend.service.ConsultaService;
import jakarta.persistence.EntityNotFoundException;
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
        List<ConsultaDTO> consultas = consultaService.listarConsultas(usuarioLogado)
                .stream()
                .map(ConsultaDTO::from)
                .toList();
        return ResponseEntity.ok(consultas);
    }

    // Buscar consulta por ID
    @GetMapping("/{id}")
    public ResponseEntity<ConsultaDTO> buscarConsulta(@PathVariable Long id,
                                                      @AuthenticationPrincipal Usuario usuarioLogado) {
        Consulta consulta = consultaService.buscarPorId(id, usuarioLogado);
        return ResponseEntity.ok(ConsultaDTO.from(consulta));
    }

    // Criar nova consulta
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<ConsultaDTO> criarConsulta(@RequestBody CriarConsultaRequest request,
                                                     @AuthenticationPrincipal Usuario usuarioLogado) {

        Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado."));
        Medico medico = medicoRepository.findById(request.getMedicoId())
                .orElseThrow(() -> new EntityNotFoundException("Médico não encontrado."));

        Consulta consulta = Consulta.builder()
                .data(request.getData())
                .hora(request.getHora())
                .status(request.getStatus())
                .paciente(paciente)
                .medico(medico)
                .build();

        Consulta novaConsulta = consultaService.criarConsulta(consulta, usuarioLogado);
        return ResponseEntity.ok(ConsultaDTO.from(novaConsulta));
    }

    // Atualizar consulta (apenas ADMIN)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConsultaDTO> atualizarConsulta(@PathVariable Long id,
                                                         @RequestBody CriarConsultaRequest request,
                                                         @AuthenticationPrincipal Usuario usuarioLogado) {

        Consulta consultaExistente = consultaService.buscarPorId(id, usuarioLogado);

        // Apenas data, hora e status podem ser alterados
        consultaExistente.setData(request.getData());
        consultaExistente.setHora(request.getHora());
        consultaExistente.setStatus(request.getStatus());

        Consulta consultaAtualizada = consultaService.atualizarConsulta(id, consultaExistente, usuarioLogado);
        return ResponseEntity.ok(ConsultaDTO.from(consultaAtualizada));
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
