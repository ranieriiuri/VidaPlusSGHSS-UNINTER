-- 1️⃣ Criar a coluna medico_id permitindo NULL
ALTER TABLE prontuarios
ADD COLUMN medico_id BIGINT NULL;

-- 2️⃣ Criar um médico “genérico” para registros existentes (opcional)
INSERT INTO medicos (nome, crm, especialidade, usuario_id)
VALUES ('Médico Genérico', '000000', 'Genérico', NULL);

-- 3️⃣ Atualizar prontuários existentes para apontar para o médico genérico
UPDATE prontuarios
SET medico_id = (SELECT id FROM medicos WHERE crm = '000000')
WHERE medico_id IS NULL;

-- 4️⃣ Adicionar a foreign key
ALTER TABLE prontuarios
ADD CONSTRAINT fk_prontuario_medico
FOREIGN KEY (medico_id) REFERENCES medicos(id);

-- 5️⃣ Tornar a coluna NOT NULL agora que todos os registros possuem médico_id
ALTER TABLE prontuarios
MODIFY COLUMN medico_id BIGINT NOT NULL;
