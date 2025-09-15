package com.vidaplus.sghss_backend.repository;

import com.vidaplus.sghss_backend.model.Consulta;
import com.vidaplus.sghss_backend.model.Paciente;
import com.vidaplus.sghss_backend.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    // Consultar por paciente
    List<Consulta> findByPaciente(Paciente paciente);

    // Consultas por médico
    List<Consulta> findByMedico(Medico medico);
}