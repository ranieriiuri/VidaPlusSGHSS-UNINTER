-- ============================
-- Migration V3 - Agenda dos Médicos
-- ============================

-- Criar tabela de horários disponíveis (AgendaSlot)
CREATE TABLE agenda_medica_slots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    data DATE NOT NULL,
    hora TIME NOT NULL,
    disponivel BOOLEAN NOT NULL DEFAULT TRUE,
    medico_id BIGINT NOT NULL,
    consulta_id BIGINT,
    CONSTRAINT fk_agenda_medico FOREIGN KEY (medico_id) REFERENCES medicos(id),
    CONSTRAINT fk_agenda_consulta FOREIGN KEY (consulta_id) REFERENCES consultas(id)
);

-- Remover coluna antiga 'agenda' da tabela medicos
ALTER TABLE medicos
DROP COLUMN agenda;
