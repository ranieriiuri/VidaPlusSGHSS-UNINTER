package com.vidaplus.sghss_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vidaplus.sghss_backend.dto.RelatorioCompletoDTO;
import com.vidaplus.sghss_backend.model.*;
import com.vidaplus.sghss_backend.model.enums.PerfilUsuario;
import com.vidaplus.sghss_backend.repository.RelatorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final PacienteService pacienteService;
    private final MedicoService medicoService;
    private final ConsultaService consultaService;
    private final ProntuarioService prontuarioService;
    private final AgendaMedicaSlotService agendaSlotService;
    private final RelatorioRepository relatorioRepository;
    private final ObjectMapper objectMapper;

    /**
     * Gera um relatório completo do sistema e salva no banco
     */
    public Relatorio gerarRelatorioCompleto(Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN) {
            throw new AccessDeniedException("Apenas administradores podem gerar relatórios.");
        }

        // Buscar dados do sistema
        List<Paciente> pacientes = pacienteService.listarTodosPacientes();
        List<Medico> medicos = medicoService.listarTodosMedicos();
        List<Consulta> consultas = consultaService.listarTodasConsultas();
        List<Prontuario> prontuarios = prontuarioService.listarTodosProntuarios();
        List<AgendaMedicaSlot> slots = agendaSlotService.listarTodosSlots();

        // Criar objeto DTO temporário
        RelatorioCompletoDTO relatorioDTO = new RelatorioCompletoDTO(
                pacientes, medicos, consultas, prontuarios, slots
        );

        // Converter para JSON
        String conteudoJson;
        try {
            conteudoJson = objectMapper.writeValueAsString(relatorioDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao gerar JSON do relatório.", e);
        }

        // Criar entidade Relatorio
        Relatorio relatorio = Relatorio.builder()
                .nome("Relatório completo - " + LocalDateTime.now())
                .conteudoJson(conteudoJson)
                .dataGeracao(LocalDateTime.now())
                .geradoPor(usuarioLogado)
                .build();

        return relatorioRepository.save(relatorio);
    }

    /**
     * Listar relatórios já gerados por ADMIN
     */
    public List<Relatorio> listarRelatorios(Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN) {
            throw new AccessDeniedException("Apenas administradores podem acessar relatórios.");
        }

        return relatorioRepository.findAll(); // ou findByGeradoPor(usuarioLogado) se quiser histórico por usuário
    }

    /**
     * Buscar relatório específico pelo ID
     */
    public Relatorio buscarPorId(Long id, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN) {
            throw new AccessDeniedException("Apenas administradores podem acessar relatórios.");
        }

        return relatorioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relatório não encontrado."));
    }
}

