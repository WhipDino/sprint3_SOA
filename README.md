# 3ESR
# Alunos
## João Victor --> RM550453
## Pedro Henrique Farath --> RM98608
## Lucca Vilaça --> RM551538
## Luana Cabezaollias --> RM99320
## Juliana Maita --> RM99224

# 3ESR
# Alunos
## João Victor --> RM550453
## Pedro Henrique Farath -->
## Lucca Vilaça -->
## Luana Cabezaollias --> 
## Juliana Maita --> 

# 🎰 Gambling Detection API

API para detecção e prevenção de apostas compulsivas, desenvolvida em Spring Boot com Java 17.

## 🚀 **COMO RODAR - SUPER SIMPLES**

### **Pré-requisito ÚNICO:**
- ✅ **Java 17+** instalado

### **Passos:**
```bash
# 1. Navegar para a pasta do projeto
cd gambling-detection-api

# 2. Executar (Maven baixa tudo automaticamente)
mvn spring-boot:run

# 3. Pronto! A API está rodando
```

### **Acessos:**
- 🌐 **API**: http://localhost:8080/api
- 📚 **Swagger (Documentação)**: http://localhost:8080/swagger-ui.html
- 🗄️ **Banco H2**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (deixe vazio)

## 📋 **Funcionalidades**

### 🧑‍💼 **Gerenciamento de Usuários**
- `GET /api/users` - Listar usuários
- `POST /api/users` - Criar usuário
- `GET /api/users/{id}` - Buscar usuário por ID
- `PUT /api/users/{id}` - Atualizar usuário

### 🎰 **Monitoramento de Sessões**
- `GET /api/sessions` - Listar sessões
- `POST /api/sessions` - Criar sessão
- `PATCH /api/sessions/{id}/end` - Finalizar sessão

### 📊 **Avaliação de Risco**
- `POST /api/risk-assessments/analyze` - Realizar análise de risco
- `GET /api/risk-assessments/user/{userId}/latest` - Última avaliação
- `GET /api/risk-assessments/high-risk` - Avaliações de alto risco

### 🚨 **Sistema de Intervenções**
- `GET /api/interventions` - Listar intervenções
- `POST /api/interventions` - Criar intervenção
- `PATCH /api/interventions/{id}/execute` - Executar intervenção

## 🛠️ **Tecnologias**

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **H2 Database** (em memória)
- **OpenAPI/Swagger** (documentação)
- **Maven** (gerenciamento de dependências)

## 🗄️ **Banco de Dados**

- **H2 em memória** - Zero configuração necessária
- **Tabelas criadas automaticamente** pelo Hibernate
- **Nomes das tabelas e colunas em português** para facilitar entendimento
- **Console H2** disponível para visualização
- **Banco vazio inicialmente** - Dados são criados conforme uso da API

### **Tabelas do Sistema:**

#### **📋 `usuarios` - Dados dos usuários**
- `id` - Identificador único
- `nome` - Nome completo do usuário
- `email` - Email único do usuário
- `telefone` - Número de telefone
- `data_nascimento` - Data de nascimento
- `nivel_risco_atual` - Nível de risco atual (LOW, MEDIUM, HIGH, CRITICAL)
- `total_depositos` - Total de depósitos realizados
- `total_saques` - Total de saques realizados
- `contador_sessoes` - Número de sessões de apostas
- `ultima_atividade` - Data da última atividade
- `ativo` - Status ativo/inativo
- `criado_em` - Data de criação
- `atualizado_em` - Data da última atualização

#### **🎰 `sessoes_apostas` - Sessões de apostas**
- `id` - Identificador único
- `id_usuario` - Referência ao usuário
- `inicio_sessao` - Data/hora de início
- `fim_sessao` - Data/hora de fim
- `duracao_minutos` - Duração em minutos
- `valor_total_apostas` - Valor total apostado
- `valor_total_ganhos` - Valor total ganho
- `resultado_liquido` - Resultado líquido (ganhos - apostas)
- `contador_apostas` - Número de apostas na sessão
- `valor_maximo_aposta` - Maior valor apostado
- `tipo_jogo` - Tipo de jogo
- `plataforma` - Plataforma utilizada
- `alta_frequencia` - Indica se houve alta frequência
- `perseguicao_perdas` - Indica se houve perseguição de perdas
- `indicadores_risco` - Indicadores de risco identificados
- `criado_em` - Data de criação

#### **📊 `avaliacoes_risco` - Avaliações de risco**
- `id` - Identificador único
- `id_usuario` - Referência ao usuário
- `nivel_risco` - Nível de risco (LOW, MEDIUM, HIGH, CRITICAL)
- `pontuacao_risco` - Pontuação numérica do risco
- `motivo_avaliacao` - Motivo da avaliação
- `indicadores_comportamentais` - Indicadores comportamentais
- `indicadores_financeiros` - Indicadores financeiros
- `indicadores_temporais` - Indicadores temporais
- `analise_sessoes` - Análise das sessões
- `recomendacoes` - Recomendações geradas
- `automatica` - Se foi avaliação automática
- `avaliado_por` - Quem realizou a avaliação
- `data_avaliacao` - Data da avaliação
- `valida_ate` - Data de validade
- `ativa` - Se a avaliação está ativa
- `criado_em` - Data de criação

#### **🚨 `intervencoes` - Intervenções aplicadas**
- `id` - Identificador único
- `id_usuario` - Referência ao usuário
- `tipo_intervencao` - Tipo (WARNING, ALTERNATIVE_SUGGESTION, TEMPORARY_BLOCK, PROFESSIONAL_REFERRAL, SUPPORT_GROUP)
- `titulo` - Título da intervenção
- `descricao` - Descrição detalhada
- `mensagem` - Mensagem para o usuário
- `acao_necessaria` - Ação necessária
- `info_contato` - Informações de contato
- `agendada_para` - Data agendada
- `executada_em` - Data de execução
- `expira_em` - Data de expiração
- `status` - Status (PENDING, SCHEDULED, EXECUTED, CANCELLED, EXPIRED)
- `prioridade` - Nível de prioridade
- `automatica` - Se foi intervenção automática
- `criada_por` - Quem criou a intervenção
- `executada_por` - Quem executou
- `notas_execucao` - Notas da execução
- `resposta_usuario` - Resposta do usuário
- `pontuacao_efetividade` - Pontuação de efetividade
- `criado_em` - Data de criação

## 📚 **Exemplos de Uso**

### **Criar um usuário:**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@email.com",
    "phoneNumber": "11999999999"
  }'
```

### **Criar uma sessão de aposta:**
```bash
curl -X POST http://localhost:8080/api/sessions \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "startTime": "2024-01-15T10:00:00"
  }'
```

### **Realizar análise de risco:**
```bash
curl -X POST http://localhost:8080/api/risk-assessments/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1
  }'
```

## 🏗️ **Arquitetura**

<img width="2910" height="3724" alt="image" src="https://github.com/user-attachments/assets/697f1ca2-bda7-4202-bf36-91d31ed9d34a" />


### **Camadas da Aplicação:**
```
┌─────────────────┐
│   Controllers   │ ← Endpoints REST
├─────────────────┤
│    Services     │ ← Lógica de negócio
├─────────────────┤
│   Repositories  │ ← Acesso aos dados
├─────────────────┤
│   Entities      │ ← Modelo de dados
└─────────────────┘
```

### **Diagrama de Entidades:**
```
User (1) ──── (N) GamblingSession
  │
  └─── (N) RiskAssessment
  │
  └─── (N) Intervention
```

## 🔧 **Desenvolvimento**

### **Estrutura do Projeto:**
```
src/main/java/br/com/gambling/
├── config/          # Configurações
├── controller/      # Controllers REST
├── dto/            # Data Transfer Objects
├── entity/         # Entidades JPA
├── enums/          # Enumeradores
├── repository/     # Repositories JPA
└── service/        # Services (lógica de negócio)
```

### **Compilar e executar JAR:**
```bash
mvn clean package
java -jar target/gambling-detection-api-1.0.0.jar
```

## 📞 **Suporte**

- 📚 Documentação: http://localhost:8080/swagger-ui.html
- 🗄️ Banco: http://localhost:8080/h2-console

---

**🎯 Objetivo:** Desenvolver uma aplicação que exponha e consuma serviços via WebService (REST), contemplando modelagem, implementação e boas práticas.
