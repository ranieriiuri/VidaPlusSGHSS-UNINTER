package com.vidaplus.sghss_backend.controller;

import com.vidaplus.sghss_backend.model.Consulta;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.service.ConsultaService;
import com.vidaplus.sghss_backend.service.UsuarioService;
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
    private final UsuarioService usuarioService;

    /**
     * Listar consultas
     * ADMIN → todas
     * MEDICO → apenas suas
     * PACIENTE → apenas suas
     */
    @GetMapping
    public ResponseEntity<List<Consulta>> listarConsultas(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        List<Consulta> consultas = consultaService.listarConsultas(usuarioLogado);
        return ResponseEntity.ok(consultas);
    }

    /**
     * Buscar consulta por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Consulta> buscarConsulta(
            @PathVariable Long id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        Consulta consulta = consultaService.buscarPorId(id, usuarioLogado);
        return ResponseEntity.ok(consulta);
    }

    /**
     * Criar nova consulta
     * Apenas ADMIN ou MEDICO
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<Consulta> criarConsulta(
            @RequestBody Consulta consulta,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        Consulta novaConsulta = consultaService.criarConsulta(consulta, usuarioLogado);
        return ResponseEntity.ok(novaConsulta);
    }

    /**
     * Atualizar consulta
     * Apenas ADMIN ou MEDICO (apenas suas)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<Consulta> atualizarConsulta(
            @PathVariable Long id,
            @RequestBody Consulta consultaAtualizada,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        Consulta consulta = consultaService.atualizarConsulta(id, consultaAtualizada, usuarioLogado);
        return ResponseEntity.ok(consulta);
    }

    /**
     * Deletar consulta
     * Apenas ADMIN
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarConsulta(
            @PathVariable Long id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
        consultaService.deletarConsulta(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
