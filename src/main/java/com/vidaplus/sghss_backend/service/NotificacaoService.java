package com.vidaplus.sghss_backend.service;

import com.vidaplus.sghss_backend.model.Notificacao;
import com.vidaplus.sghss_backend.model.Paciente;
import com.vidaplus.sghss_backend.model.enums.TipoNotificacao;
import com.vidaplus.sghss_backend.repository.NotificacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;

    /**
     * Enviar uma nova notificação para o paciente
     */
    public Notificacao enviarNotificacao(Paciente paciente, String mensagem, String tipo) {
        Notificacao notificacao = Notificacao.builder()
                .paciente(paciente)
                .mensagem(mensagem)
                .tipo(TipoNotificacao.valueOf(tipo))
                .dataCriacao(LocalDateTime.now())
                .lida(false)
                .build();

        return notificacaoRepository.save(notificacao);
    }

    /**
     * Listar todas as notificações de um paciente
     */
    public List<Notificacao> listarNotificacoesPaciente(Paciente paciente) {
        return notificacaoRepository.findByPaciente(paciente);
    }

    /**
     * Listar notificações não lidas de um paciente
     */
    public List<Notificacao> listarNaoLidas(Paciente paciente) {
        return notificacaoRepository.findByPacienteAndLidaFalse(paciente);
    }

    /**
     * Marcar notificação como lida
     */
    public void marcarComoLida(Long notificacaoId) {
        Notificacao notificacao = notificacaoRepository.findById(notificacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Notificação não encontrada."));
        notificacao.setLida(true);
        notificacaoRepository.save(notificacao);
    }

    /**
     * Buscar notificação por ID
     */
    public Notificacao buscarPorId(Long notificacaoId) {
        return notificacaoRepository.findById(notificacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Notificação não encontrada."));
    }
}
