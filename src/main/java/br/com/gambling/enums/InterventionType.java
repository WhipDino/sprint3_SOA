package br.com.gambling.enums;

/**
 * Enum que representa os tipos de intervenção disponíveis para usuários em risco.
 */
public enum InterventionType {
    
    /**
     * Alerta simples sobre comportamento de risco
     */
    WARNING("Aviso", "Alerta sobre comportamento de risco"),
    
    /**
     * Sugestão de alternativas saudáveis
     */
    ALTERNATIVE_SUGGESTION("Sugestão de Alternativa", "Oferecimento de atividades alternativas"),
    
    /**
     * Bloqueio temporário de apostas
     */
    TEMPORARY_BLOCK("Bloqueio Temporário", "Bloqueio temporário de apostas"),
    
    /**
     * Conexão com profissional de saúde mental
     */
    PROFESSIONAL_REFERRAL("Encaminhamento Profissional", "Conexão com profissional de saúde mental"),
    
    /**
     * Participação em grupo de apoio
     */
    SUPPORT_GROUP("Grupo de Apoio", "Participação em grupo de apoio");
    
    private final String description;
    private final String details;
    
    InterventionType(String description, String details) {
        this.description = description;
        this.details = details;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getDetails() {
        return details;
    }
}
