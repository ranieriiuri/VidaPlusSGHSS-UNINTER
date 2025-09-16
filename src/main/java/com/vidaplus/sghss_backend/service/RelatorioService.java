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

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;

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

    public byte[] gerarPdfRelatorioCompleto() {
        try (PDDocument doc = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            doc.addPage(page);
            PDPageContentStream content = new PDPageContentStream(doc, page);

            int y = 750; // posição inicial

            // Método auxiliar para quebrar página se necessário
            y = escreverLinha(content, y, "Relatório Completo do Sistema", PDType1Font.HELVETICA_BOLD, 18);

            // Pacientes
            y = escreverLinha(content, y - 20, "Pacientes:", PDType1Font.HELVETICA_BOLD, 14);
            List<Paciente> pacientes = pacienteService.listarTodosPacientes();
            for (Paciente p : pacientes) {
                y = escreverLinhaComQuebraPagina(doc, content, y, "- " + p.getNome() + " | CPF: " + p.getCpf() + " | Telefone: " + p.getTelefone(), PDType1Font.HELVETICA, 12);
            }

            // Médicos
            y = escreverLinha(content, y - 20, "Médicos:", PDType1Font.HELVETICA_BOLD, 14);
            List<Medico> medicos = medicoService.listarTodosMedicos();
            for (Medico m : medicos) {
                y = escreverLinhaComQuebraPagina(doc, content, y, "- " + m.getNome() + " | CRM: " + m.getCrm() + " | Especialidade: " + m.getEspecialidade(), PDType1Font.HELVETICA, 12);
            }

            // Consultas
            y = escreverLinha(content, y - 20, "Consultas:", PDType1Font.HELVETICA_BOLD, 14);
            List<Consulta> consultas = consultaService.listarTodasConsultas();
            for (Consulta c : consultas) {
                y = escreverLinhaComQuebraPagina(doc, content, y, "- Paciente: " + c.getPaciente().getNome() +
                        " | Médico: " + c.getMedico().getNome() +
                        " | Data: " + c.getData() +
                        " | Hora: " + c.getHora() +
                        " | Status: " + c.getStatus(), PDType1Font.HELVETICA, 12);
            }

            // Prontuários
            y = escreverLinha(content, y - 20, "Prontuários:", PDType1Font.HELVETICA_BOLD, 14);
            List<Prontuario> prontuarios = prontuarioService.listarTodosProntuarios();
            for (Prontuario p : prontuarios) {
                y = escreverLinhaComQuebraPagina(doc, content, y, "- Paciente: " + p.getPaciente().getNome() + " | Registros: " + p.getRegistros(), PDType1Font.HELVETICA, 12);
            }

            // Agenda Médica
            y = escreverLinha(content, y - 20, "Agenda Médica:", PDType1Font.HELVETICA_BOLD, 14);
            List<AgendaMedicaSlot> slots = agendaSlotService.listarTodosSlots();
            for (AgendaMedicaSlot s : slots) {
                y = escreverLinhaComQuebraPagina(doc, content, y, "- Médico: " + s.getMedico().getNome() +
                        " | Data: " + s.getData() +
                        " | Hora: " + s.getHora(), PDType1Font.HELVETICA, 12);
            }

            content.close();
            doc.save(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF do relatório completo", e);
        }
    }

    // Escreve uma linha e retorna o novo y
    private int escreverLinha(PDPageContentStream content, int y, String texto, PDType1Font fonte, int tamanho) throws Exception {
        content.setFont(fonte, tamanho);
        content.beginText();
        content.newLineAtOffset(50, y);
        content.showText(texto);
        content.endText();
        return y - (tamanho + 5);
    }

    // Escreve uma linha e cria nova página se y < 50
    private int escreverLinhaComQuebraPagina(PDDocument doc, PDPageContentStream content, int y, String texto, PDType1Font fonte, int tamanho) throws Exception {
        if (y < 50) {
            content.close();
            PDPage page = new PDPage();
            doc.addPage(page);
            content = new PDPageContentStream(doc, page);
            y = 750;
        }
        return escreverLinha(content, y, texto, fonte, tamanho);
    }

    // Esse é o que gera apartir de dados salvos no DB
    public byte[] gerarPdfRelatorioCompleto(RelatorioCompletoDTO relatorioDTO) {
        try (PDDocument doc = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            doc.addPage(page);
            PDPageContentStream content = new PDPageContentStream(doc, page);

            int y = 750; // posição inicial

            // Cabeçalho
            y = escreverLinha(content, y, "Relatório Completo do Sistema", PDType1Font.HELVETICA_BOLD, 18);

            // Pacientes
            y = escreverLinha(content, y - 20, "Pacientes:", PDType1Font.HELVETICA_BOLD, 14);
            for (Paciente p : relatorioDTO.getPacientes()) {
                y = escreverLinhaComQuebraPagina(doc, content, y, "- " + p.getNome() + " | CPF: " + p.getCpf() + " | Telefone: " + p.getTelefone(), PDType1Font.HELVETICA, 12);
            }

            // Médicos
            y = escreverLinha(content, y - 20, "Médicos:", PDType1Font.HELVETICA_BOLD, 14);
            for (Medico m : relatorioDTO.getMedicos()) {
                y = escreverLinhaComQuebraPagina(doc, content, y, "- " + m.getNome() + " | CRM: " + m.getCrm() + " | Especialidade: " + m.getEspecialidade(), PDType1Font.HELVETICA, 12);
            }

            // Consultas
            y = escreverLinha(content, y - 20, "Consultas:", PDType1Font.HELVETICA_BOLD, 14);
            for (Consulta c : relatorioDTO.getConsultas()) {
                y = escreverLinhaComQuebraPagina(doc, content, y, "- Paciente: " + c.getPaciente().getNome() +
                        " | Médico: " + c.getMedico().getNome() +
                        " | Data: " + c.getData() +
                        " | Hora: " + c.getHora() +
                        " | Status: " + c.getStatus(), PDType1Font.HELVETICA, 12);
            }

            // Prontuários
            y = escreverLinha(content, y - 20, "Prontuários:", PDType1Font.HELVETICA_BOLD, 14);
            for (Prontuario p : relatorioDTO.getProntuarios()) {
                y = escreverLinhaComQuebraPagina(doc, content, y, "- Paciente: " + p.getPaciente().getNome() + " | Registros: " + p.getRegistros(), PDType1Font.HELVETICA, 12);
            }

            // Agenda Médica
            y = escreverLinha(content, y - 20, "Agenda Médica:", PDType1Font.HELVETICA_BOLD, 14);
            for (AgendaMedicaSlot s : relatorioDTO.getSlots()) {
                y = escreverLinhaComQuebraPagina(doc, content, y, "- Médico: " + s.getMedico().getNome() +
                        " | Data: " + s.getData() +
                        " | Hora: " + s.getHora(), PDType1Font.HELVETICA, 12);
            }

            content.close();
            doc.save(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF do relatório completo a partir do DTO", e);
        }
    }
}