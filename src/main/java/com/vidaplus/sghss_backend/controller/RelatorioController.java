package com.vidaplus.sghss_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vidaplus.sghss_backend.dto.RelatorioCompletoDTO;
import com.vidaplus.sghss_backend.model.Relatorio;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')") // Apenas ADMIN pode acessar qualquer endpoint
public class RelatorioController {

    private final RelatorioService relatorioService;
    private final ObjectMapper objectMapper; // injeta direto aqui

    /**
     * Endpoint para gerar relatório completo e salvar no banco
     */
    @PostMapping("/completo")
    public ResponseEntity<Relatorio> gerarRelatorioCompleto(@AuthenticationPrincipal Usuario usuarioLogado) {
        Relatorio relatorio = relatorioService.gerarRelatorioCompleto(usuarioLogado);
        return ResponseEntity.ok(relatorio);
    }

    /**
     * Endpoint para listar todos os relatórios gerados
     */
    @GetMapping
    public ResponseEntity<List<Relatorio>> listarRelatorios(@AuthenticationPrincipal Usuario usuarioLogado) {
        List<Relatorio> relatorios = relatorioService.listarRelatorios(usuarioLogado);
        return ResponseEntity.ok(relatorios);
    }

    /**
     * Endpoint para buscar relatório específico pelo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Relatorio> buscarRelatorioPorId(@PathVariable Long id,
                                                          @AuthenticationPrincipal Usuario usuarioLogado) {
        Relatorio relatorio = relatorioService.buscarPorId(id, usuarioLogado);
        return ResponseEntity.ok(relatorio);
    }

    /**
     * Endpoint para gerar e baixar PDF do relatório completo
     */
    @GetMapping("/completo/pdf")
    public ResponseEntity<byte[]> baixarPdfRelatorioCompleto() {
        byte[] pdf = relatorioService.gerarPdfRelatorioCompleto();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_completo.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> baixarRelatorioPdf(@PathVariable Long id, @AuthenticationPrincipal Usuario usuarioLogado) {
        Relatorio relatorio = relatorioService.buscarPorId(id, usuarioLogado);
        RelatorioCompletoDTO dto;
        try {
            dto = objectMapper.readValue(relatorio.getConteudoJson(), RelatorioCompletoDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler JSON do relatório", e);
        }

        byte[] pdf = relatorioService.gerarPdfRelatorioCompleto(dto);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"relatorio.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
