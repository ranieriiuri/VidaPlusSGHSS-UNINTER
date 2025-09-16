package com.vidaplus.sghss_backend.controller;

import com.vidaplus.sghss_backend.dto.NotificacaoRequest;
import com.vidaplus.sghss_backend.model.Notificacao;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.service.NotificacaoService;
import com.vidaplus.sghss_backend.service.PacienteService;
import com.vidaplus.sghss_backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pacientes/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {

    private final PacienteService pacienteService;
    private final UsuarioService usuarioService; // para obter usuário logado, se necessário
    private final NotificacaoService notificacaoService;

    /**
     * Listar todas as notificações do paciente
     */
    @GetMapping
    public ResponseEntity<List<Notificacao>> listarNotificacoes(
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        Long pacienteId = usuarioLogado.getPaciente().getId();
        List<Notificacao> notificacoes = pacienteService.listarNotificacoes(pacienteId, usuarioLogado);
        return ResponseEntity.ok(notificacoes);
    }

    /**
     * Listar notificações não lidas do paciente
     */
    @GetMapping("/nao-lidas")
    public ResponseEntity<List<Notificacao>> listarNaoLidas(
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        Long pacienteId = usuarioLogado.getPaciente().getId();
        List<Notificacao> notificacoes = pacienteService.listarNotificacoes(pacienteId, usuarioLogado)
                .stream()
                .filter(n -> !n.isLida())
                .toList();
        return ResponseEntity.ok(notificacoes);
    }

    /**
     * Marcar uma notificação como lida
     */
    @PostMapping("/{id}/marcar-como-lida")
    public ResponseEntity<Void> marcarComoLida(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        pacienteService.marcarNotificacaoComoLida(id, usuarioLogado);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/enviar/{pacienteId}")
    public ResponseEntity<Notificacao> enviarNotificacao(
            @PathVariable Long pacienteId,
            @RequestBody NotificacaoRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        // Verificar permissão
        if (usuarioLogado.getPerfil() != com.vidaplus.sghss_backend.model.enums.PerfilUsuario.ADMIN &&
                usuarioLogado.getPerfil() != com.vidaplus.sghss_backend.model.enums.PerfilUsuario.MEDICO) {
            throw new AccessDeniedException("Apenas ADMIN ou MÉDICO podem enviar notificações.");
        }

        // Buscar paciente
        var paciente = pacienteService.buscarPorId(pacienteId, usuarioLogado);

        // Enviar notificação
        Notificacao notificacao = notificacaoService.enviarNotificacao(
                paciente,
                request.mensagem(),
                request.tipo()
        );

        return ResponseEntity.ok(notificacao);
    }
}
