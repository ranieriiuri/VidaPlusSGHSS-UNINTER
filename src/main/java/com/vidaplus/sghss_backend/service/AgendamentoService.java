package com.vidaplus.sghss_backend.service;

import com.vidaplus.sghss_backend.dto.AgendaMedicaRespostaDTO;
import com.vidaplus.sghss_backend.dto.CriarConsultaRequest;
import com.vidaplus.sghss_backend.dto.VincularConsultaRequest;
import com.vidaplus.sghss_backend.model.Consulta;
import com.vidaplus.sghss_backend.model.Medico;
import com.vidaplus.sghss_backend.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final AgendaMedicaSlotService agendaSlotService;
    private final ConsultaService consultaService;
    private final MedicoService medicoService;
    private final AuditLogService auditLogService;

    /**
     * Agendar uma consulta usando um slot já existente
     */
    public AgendaMedicaRespostaDTO vincularConsulta(Long slotId, CriarConsultaRequest request, Usuario usuarioLogado) {

        // 1️⃣ Buscar slot existente
        AgendaMedicaRespostaDTO slotDTO = agendaSlotService.buscarPorId(slotId);

        if (!slotDTO.isDisponivel()) {
            throw new IllegalStateException("Este slot já está ocupado.");
        }

        // 2️⃣ Garantir autorização
        Medico medico = medicoService.buscarEntidadePorId(slotDTO.getMedicoId(), usuarioLogado);

        if (usuarioLogado.getPerfil() == com.vidaplus.sghss_backend.model.enums.PerfilUsuario.MEDICO &&
                !medico.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Médico só pode agendar em seus próprios slots.");
        }

        // 3️⃣ Configurar request para incluir o slot
        request.setAgendaSlotId(slotId);

        // 4️⃣ Criar consulta (vai vincular o slot automaticamente)
        Consulta consulta = consultaService.criarConsulta(request, usuarioLogado);

        // 5️⃣ Retornar DTO atualizado do slot
        return agendaSlotService.buscarPorId(slotId);
    }
}
