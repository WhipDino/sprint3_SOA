# 🏗️ Arquitetura da Aplicação

## 📊 Diagrama de Arquitetura em Camadas

```
┌─────────────────────────────────────────────────────────────┐
│                    CLIENTE (Browser/Postman)                │
└─────────────────────┬───────────────────────────────────────┘
                      │ HTTP/REST
┌─────────────────────▼───────────────────────────────────────┐
│                 CONTROLLER LAYER                            │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐          │
│  │UserController│ │SessionController│ │RiskController│      │
│  └─────────────┘ └─────────────┘ └─────────────┘          │
└─────────────────────┬───────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────┐
│                  SERVICE LAYER                              │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐          │
│  │UserService  │ │SessionService│ │RiskService  │          │
│  └─────────────┘ └─────────────┘ └─────────────┘          │
└─────────────────────┬───────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────┐
│                REPOSITORY LAYER                             │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐          │
│  │UserRepository│ │SessionRepository│ │RiskRepository│      │
│  └─────────────┘ └─────────────┘ └─────────────┘          │
└─────────────────────┬───────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────┐
│                 DATABASE LAYER                              │
│                    H2 Database                              │
│              (In-Memory Database)                           │
└─────────────────────────────────────────────────────────────┘
```

## 🗄️ Diagrama de Entidades (ER)

```
┌─────────────────┐
│      USER       │
├─────────────────┤
│ id (PK)         │
│ name            │
│ email           │
│ phone           │
│ risk_level      │
│ created_at      │
│ updated_at      │
└─────────┬───────┘
          │
          │ 1:N
          │
┌─────────▼───────┐
│ GAMBLING_SESSION│
├─────────────────┤
│ id (PK)         │
│ user_id (FK)    │
│ start_time      │
│ end_time        │
│ total_bets      │
│ total_wins      │
│ total_losses    │
│ net_result      │
│ status          │
│ created_at      │
│ updated_at      │
└─────────┬───────┘
          │
          │ 1:N
          │
┌─────────▼───────┐
│ RISK_ASSESSMENT │
├─────────────────┤
│ id (PK)         │
│ user_id (FK)    │
│ risk_score      │
│ risk_level      │
│ factors_analyzed│
│ recommendations │
│ created_at      │
│ updated_at      │
└─────────┬───────┘
          │
          │ 1:N
          │
┌─────────▼───────┐
│  INTERVENTION   │
├─────────────────┤
│ id (PK)         │
│ user_id (FK)    │
│ type            │
│ status          │
│ message         │
│ created_at      │
│ updated_at      │
└─────────────────┘
```

## 🔄 Fluxo de Dados

```
1. Cliente faz requisição HTTP
   ↓
2. Controller recebe e valida
   ↓
3. Service processa lógica de negócio
   ↓
4. Repository acessa banco de dados
   ↓
5. Dados retornam pelas camadas
   ↓
6. Controller retorna resposta JSON
```

## 🎯 Casos de Uso Implementados

### **1. Gerenciamento de Usuários**
- Criar usuário
- Listar usuários
- Buscar usuário por ID
- Atualizar usuário

### **2. Monitoramento de Sessões**
- Iniciar sessão de aposta
- Finalizar sessão
- Listar sessões
- Calcular estatísticas

### **3. Avaliação de Risco**
- Analisar padrões de comportamento
- Calcular score de risco
- Classificar nível de risco
- Gerar recomendações

### **4. Sistema de Intervenções**
- Criar intervenção
- Executar intervenção
- Listar intervenções pendentes
- Monitorar status

## 🛠️ Tecnologias por Camada

| Camada | Tecnologia | Responsabilidade |
|--------|------------|------------------|
| **Controller** | Spring MVC | Receber requisições HTTP |
| **Service** | Spring Service | Lógica de negócio |
| **Repository** | Spring Data JPA | Acesso aos dados |
| **Entity** | JPA/Hibernate | Mapeamento objeto-relacional |
| **Database** | H2 | Persistência de dados |

## 📋 Padrões Utilizados

- **MVC (Model-View-Controller)**
- **Repository Pattern**
- **Service Layer Pattern**
- **DTO Pattern**
- **RESTful API Design**
