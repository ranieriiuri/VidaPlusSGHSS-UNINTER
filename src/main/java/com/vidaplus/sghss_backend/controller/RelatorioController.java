package com.vidaplus.sghss_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vidaplus.sghss_backend.dto.AdminRespostaDTO;
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
@PreAuthorize("hasRole('ADMIN')") // Apenas ADMIN pode acessar qualquer endpoint
public class RelatorioController {

    private final RelatorioService relatorioService;
    private final ObjectMapper objectMapper; // injeta direto aqui

   //Traz um relatorio atual completo em json
   @PostMapping("/completo")
   public ResponseEntity<RelatorioCompletoDTO> gerarRelatorioCompleto(@AuthenticationPrincipal Usuario usuarioLogado) {
       Relatorio relatorio = relatorioService.gerarRelatorioCompleto(usuarioLogado);

       // Desserializa o JSON armazenado no DB para enviar ao front-end
       RelatorioCompletoDTO relatorioDTO;
       try {
           relatorioDTO = objectMapper.readValue(relatorio.getConteudoJson(), RelatorioCompletoDTO.class);
       } catch (Exception e) {
           throw new RuntimeException("Erro ao ler JSON do relatório", e);
       }

       return ResponseEntity.ok(relatorioDTO);
   }

    //Lista todos em json
    @GetMapping
    public ResponseEntity<List<Relatorio>> listarRelatorios(@AuthenticationPrincipal Usuario usuarioLogado) {
        List<Relatorio> relatorios = relatorioService.listarRelatorios(usuarioLogado);
        return ResponseEntity.ok(relatorios);
    }

    //Esse busca por id
    @GetMapping("/{id}")
    public ResponseEntity<Relatorio> buscarRelatorioPorId(@PathVariable Long id,
                                                          @AuthenticationPrincipal Usuario usuarioLogado) {
        Relatorio relatorio = relatorioService.buscarPorId(id, usuarioLogado);
        return ResponseEntity.ok(relatorio);
    }

    //Esse gera o pdf atual geral e já baixa
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

    //Esse busca relatorio salvo anteriormente no banco, gera o pdf e disponibiliza
    /* POR ENQUANTO, CONGELADO!

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> baixarRelatorioPdfPorId(@PathVariable Long id, @AuthenticationPrincipal Usuario usuarioLogado) {
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
     */
}
