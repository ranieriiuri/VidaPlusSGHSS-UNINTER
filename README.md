# SGHSS - Sistema de Gestão Hospitalar e Serviços de Saúde

## 📌 Descrição
O **SGHSS** é um sistema de gestão hospitalar desenvolvido em **Java (Spring Boot)** com autenticação JWT, arquitetura em camadas e suporte a multiusuários.  
Seu objetivo é oferecer funcionalidades para **pacientes, médicos e administradores**, incluindo **agendamentos, prontuários, relatórios, notificações e auditoria**.

---

## ⚙️ Tecnologias Utilizadas
- **Java 17+**
- **Spring Boot 3**
- **Spring Security (JWT)**
- **Spring Data JPA / Hibernate**
- **MySQL**
- **Lombok**
- **Maven**
- **k6 (testes de carga)**
- **Postman (testes de API)**

---

## 📥 Como Clonar e Rodar o Projeto

### 1️⃣ Pré-requisitos
- Java 17 instalado
- Maven 3.9+ instalado
- MySQL rodando na sua máquina
- Git instalado

### 2️⃣ Clonar o repositório
```bash
git clone https://github.com/ranieriiuri/VidaPlusSGHSS-UNINTER.git
cd sghss-backend
```
### 3️⃣ Configurar o banco de dados
```
CREATE DATABASE sghss;
```
- Edite o arquivo src/main/resources/application.properties (ou application.yml) e configure suas credenciais:

```
spring.datasource.url=jdbc:mysql://localhost:3306/sghss
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```
### 4️⃣ Rodar a aplicação
Na raiz do projeto:
```
mvn spring-boot:run
```

Se tudo estiver correto, a API estará disponível em:
```
http://localhost:8080
```

### 5️⃣ Testar no Postman
- Importar os endpoints já definidos (ex.: /auth/register, /auth/login, /usuarios etc.)
- Usar Bearer Token JWT para acessar rotas protegidas

---
## 📂 Estrutura de Classes Principais

- **Usuário** → cadastro e autenticação
- **Paciente** → dados pessoais, consultas, prontuários, notificações
- **Médico** → CRM, especialidade, agenda médica
- **Consulta** → data, hora, status, paciente, médico
- **Prontuário** → registros e prescrições
- **Agenda Médica** → slots de horários disponíveis
- **Notificação** → mensagens enviadas ao paciente
- **Relatório** → relatórios administrativos em JSON e PDF
- **Audit Log** → registro de ações de usuários
- **Hospital** → início de suporte a multi-tenancy

---

## 🚀 Endpoints Disponíveis

### 🔐 Autenticação
- `POST /auth/register` → Registro de usuários
- `POST /auth/login` → Autenticação de usuários

### 👤 Usuários
- `POST /usuarios` → Criar usuário
- `PUT /usuarios/{id}` → Atualizar usuário
- `DELETE /usuarios/{id}` → Deletar usuário

### 🧑‍⚕️ Pacientes
- `GET /pacientes` → Listar pacientes
- `GET /pacientes/{id}` → Buscar paciente por ID
- `POST /pacientes` → Cadastrar paciente
- `DELETE /pacientes/{id}` → Deletar paciente

### 👨‍⚕️ Médicos
- `GET /medicos` → Listar médicos
- `GET /medicos/{id}` → Buscar médico
- `POST /medicos` → Criar médico
- `PUT /medicos/{id}` → Atualizar médico
- `DELETE /medicos/{id}` → Deletar médico

### 📋 Consultas
- `GET /consultas` → Listar consultas
- `GET /consultas/{id}` → Buscar consulta
- `POST /consultas/{id}` → Criar consulta
- `PUT /consultas/{id}` → Atualizar consulta
- `GET /consultas/{id}/valor` → Obter valor de consulta
- `GET /consultas/total-valores` → Valor total de todas as consultas
- `DELETE /consultas/{id}` → Deletar consulta

### 📝 Prontuários
- `GET /prontuarios` → Listar prontuários
- `GET /prontuarios/{id}` → Buscar prontuário
- `POST /prontuarios` → Criar prontuário
- `DELETE /prontuarios/{id}` → Deletar prontuário

### 🔔 Notificações
- `GET /pacientes/notificacoes` → Notificações do paciente logado
- `GET /pacientes/notificacoes/todas` → Listar todas notificações
- `POST /pacientes/notificacoes/{id}/marcar-como-lida` → Marcar como lida
- `POST /pacientes/notificacoes/enviar/{pacienteId}` → Enviar notificação

### 📊 Relatórios
- `POST /relatorios/completo` → Gerar relatório completo
- `GET /relatorios` → Listar relatórios
- `GET /relatorios/{id}` → Buscar relatório
- `GET /relatorios/completo/pdf` → Baixar relatório em PDF

### 📜 Audit Log
- `GET /audit` → Listar todos os logs
- `GET /audit/usuario/{id}` → Logs por usuário

### 📅 Agenda Médica
- `GET /agenda-medica/medico/{medicoId}` → Listar agenda de um médico
- `GET /agenda-medica/medico/{medicoId}/disponiveis` → Listar horários disponíveis
- `POST /agenda-medica/medico/{medicoId}/novo` → Criar slot na agenda
- `POST /agenda-medica/{slotId}/agendar` → Agendar consulta
- `PATCH /agenda-medica/{slotId}/disponivel` → Atualizar disponibilidade

---

## 🧪 Testes
- **Postman**: recomendado para testar endpoints manualmente
- **k6**: utilizado para testes de carga (ex.: 200 usuários simultâneos)
  ```bash
  k6 run teste_agendamento.js

---

## 👨‍💻 Autor
Projeto desenvolvido por Iuri R. O. Batista, como parte do Projeto multidisciplinar da graduação em Análise e Desenvolvimento de Sistemas pelo Centro Universitário Internacional.