package com.vidaplus.sghss_backend.repository;

import com.vidaplus.sghss_backend.model.AgendaMedicaSlot;
import com.vidaplus.sghss_backend.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AgendaMedicaSlotRepository extends JpaRepository<AgendaMedicaSlot, Long> {

    // Listar todos os slots de um médico
    List<AgendaMedicaSlot> findByMedico(Medico medico);

    // Listar slots disponíveis de um médico em uma data específica
    List<AgendaMedicaSlot> findByMedicoAndDataAndDisponivelTrue(Medico medico, LocalDate data);

    // Listar todos os slots disponíveis de um médico
    List<AgendaMedicaSlot> findByMedicoAndDisponivelTrue(Medico medico);

    // Verificar se existe algum slot específico já ocupado
    boolean existsByMedicoAndDataAndHora(Medico medico, LocalDate data, java.time.LocalTime hora);
}
