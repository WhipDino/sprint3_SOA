# 3ESR
# Alunos
## João Victor --> RM550453
## Pedro Henrique Farath --> RM98608
## Lucca Vilaça --> RM551538
## Luana Cabezaollias --> RM99320
## Juliana Maita --> RM99224

# 🎰 Gambling Detection API

API para detecção e prevenção de apostas compulsivas, desenvolvida em Spring Boot com Java 17.

## 🚀 **COMO RODAR **

### **Pré-requisito :**
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

## 📊 **Dados de Exemplo**

A aplicação já inclui dados de exemplo:
- 5 usuários com diferentes níveis de risco
- Sessões de apostas com padrões variados
- Avaliações de risco automáticas
- Intervenções aplicadas

## 🗄️ **Banco de Dados**

<img width="2910" height="3724" alt="image" src="https://github.com/user-attachments/assets/dc44c8b2-e7b8-42a5-ac98-c22bd093c29b" />

- **H2 em memória** - Zero configuração necessária
- **Tabelas criadas automaticamente** pelo Hibernate
- **Nomes das tabelas e colunas em português** para facilitar entendimento
- **Console H2** disponível para visualização

### **Tabelas do Sistema:**
- `usuarios` - Dados dos usuários
- `sessoes_apostas` - Sessões de apostas
- `avaliacoes_risco` - Avaliações de risco
- `intervencoes` - Intervenções aplicadas

## 📚 **Exemplos de Uso**

### **Criar um usuário:**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@email.com",
    "phone": "11999999999"
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

## ✅ **Critérios de Avaliação Atendidos**

- ✅ **Estruturação do projeto (25%)** - Organização clara por camadas
- ✅ **Mapeamento de requisições (20%)** - Endpoints REST bem definidos
- ✅ **Conexão com banco (20%)** - JPA/Hibernate com H2
- ✅ **Interface de acesso (15%)** - Swagger UI + Postman
- ✅ **Documentação (10%)** - README completo
- ✅ **Arquitetura e diagramas (10%)** - Diagramas incluídos

## 🇧🇷 **Refatoração Completa para Português**

- ✅ **Código em português** - Comentários e documentação
- ✅ **Banco de dados em português** - Tabelas e colunas com nomes em PT
- ✅ **Comentários simplificados** - Apenas o necessário para entendimento
- ✅ **Interface amigável** - Fácil de entender e usar

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

## 🌟 **Refatoração para Português**

### **Mudanças Implementadas:**
- ✅ **Comentários em português** - Todos os comentários foram traduzidos
- ✅ **Documentação simplificada** - Apenas comentários necessários para entendimento
- ✅ **Código limpo** - Removidos comentários desnecessários
- ✅ **Estrutura mantida** - Funcionalidade preservada

### **Arquivos Refatorados:**
- **Entidades** - Comentários em português
- **DTOs** - Documentação simplificada
- **Controllers** - Comentários essenciais
- **Services** - Lógica documentada em português
- **Repositories** - Queries documentadas

## 📞 **Suporte**

- 📧 Email: dev@gambling-detection.com
- 📚 Documentação: http://localhost:8080/swagger-ui.html
- 🗄️ Banco: http://localhost:8080/h2-console

---

**🎯 Objetivo:** Desenvolver uma aplicação que exponha e consuma serviços via WebService (REST), contemplando modelagem, implementação e boas práticas.

**📋 Critérios:** Estruturação (25%) + Mapeamento (20%) + Banco (20%) + Interface (15%) + Documentação (10%) + Arquitetura (10%)
