package br.com.gambling.enums;

/**
 * Enum que representa os níveis de risco para apostas compulsivas.
 * 
 * Utilizado para classificar usuários baseado em padrões comportamentais
 * identificados através de algoritmos de machine learning.
 */
public enum RiskLevel {
    
    /**
     * Usuário com comportamento normal, sem sinais de risco
     */
    LOW("Baixo", "Comportamento normal, sem sinais de risco"),
    
    /**
     * Usuário com alguns sinais de alerta, mas ainda controlável
     */
    MEDIUM("Médio", "Alguns sinais de alerta detectados"),
    
    /**
     * Usuário com alto risco de desenvolver comportamento compulsivo
     */
    HIGH("Alto", "Alto risco de comportamento compulsivo"),
    
    /**
     * Usuário já apresenta comportamento compulsivo confirmado
     */
    CRITICAL("Crítico", "Comportamento compulsivo confirmado");
    
    private final String description;
    private final String details;
    
    RiskLevel(String description, String details) {
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
