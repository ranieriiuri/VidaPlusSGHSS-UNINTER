package com.vidaplus.sghss_backend.controller;

import com.vidaplus.sghss_backend.dto.NotificacaoDTO;
import com.vidaplus.sghss_backend.dto.NotificacaoRequest;
import com.vidaplus.sghss_backend.model.Notificacao;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.service.NotificacaoService;
import com.vidaplus.sghss_backend.service.PacienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pacientes/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {

    private final PacienteService pacienteService;
    private final NotificacaoService notificacaoService;

    /** Listar todas as notificações do paciente logado */
    @GetMapping
    public ResponseEntity<List<NotificacaoDTO>> listarNotificacoes(
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        var paciente = pacienteService.buscarPorUsuario(usuarioLogado);
        List<NotificacaoDTO> notificacoes = notificacaoService.listarNotificacoesPaciente(paciente)
                .stream()
                .map(NotificacaoDTO::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(notificacoes);
    }

    @GetMapping("/todas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificacaoDTO>> listarTodasNotificacoes() {
        List<NotificacaoDTO> notificacoes = notificacaoService.listarTodasNotificacoes()
                .stream()
                .map(NotificacaoDTO::from)
                .toList();
        return ResponseEntity.ok(notificacoes);
    }

    /** Listar notificações não lidas do paciente logado */
    @GetMapping("/nao-lidas")
    public ResponseEntity<List<NotificacaoDTO>> listarNaoLidas(
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        var paciente = pacienteService.buscarPorUsuario(usuarioLogado);
        List<NotificacaoDTO> notificacoes = notificacaoService.listarNaoLidas(paciente)
                .stream()
                .map(NotificacaoDTO::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(notificacoes);
    }

    /** Marcar uma notificação como lida */
    @PostMapping("/{id}/marcar-como-lida")
    public ResponseEntity<Void> marcarComoLida(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        notificacaoService.marcarComoLida(id, usuarioLogado);
        return ResponseEntity.ok().build();
    }

    /** Enviar notificação para paciente (ADMIN ou MÉDICO) */
    @PostMapping("/enviar/{pacienteId}")
    public ResponseEntity<NotificacaoDTO> enviarNotificacao(
            @PathVariable Long pacienteId,
            @RequestBody NotificacaoRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        if (usuarioLogado.getPerfil() != com.vidaplus.sghss_backend.model.enums.PerfilUsuario.ADMIN &&
                usuarioLogado.getPerfil() != com.vidaplus.sghss_backend.model.enums.PerfilUsuario.MEDICO) {
            throw new AccessDeniedException("Apenas ADMIN ou MÉDICO podem enviar notificações.");
        }

        var paciente = pacienteService.buscarPorId(pacienteId, usuarioLogado);
        Notificacao notificacao = notificacaoService.enviarNotificacao(
                paciente,
                request.mensagem(),
                request.tipo(),
                usuarioLogado
        );

        return ResponseEntity.ok(NotificacaoDTO.from(notificacao));
    }

    /** Buscar notificação por ID */
    @GetMapping("/{id}")
    public ResponseEntity<NotificacaoDTO> buscarPorId(@PathVariable Long id) {
        Notificacao notificacao = notificacaoService.buscarPorId(id);
        return ResponseEntity.ok(NotificacaoDTO.from(notificacao));
    }
}
