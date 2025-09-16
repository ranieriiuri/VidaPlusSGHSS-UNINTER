-- ============================
-- Migration V2 - Adicionar Notificações e Teleconsulta
-- ============================

-- Criar tabela de notificações
CREATE TABLE notificacoes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    mensagem VARCHAR(500) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    data_criacao DATETIME NOT NULL,
    lida BOOLEAN NOT NULL DEFAULT FALSE,
    paciente_id BIGINT NOT NULL,
    CONSTRAINT fk_notificacao_paciente FOREIGN KEY (paciente_id) REFERENCES pacientes(id)
);

-- Adicionar coluna para teleconsulta em pacientes
ALTER TABLE pacientes
ADD COLUMN teleconsulta_info VARCHAR(500);
