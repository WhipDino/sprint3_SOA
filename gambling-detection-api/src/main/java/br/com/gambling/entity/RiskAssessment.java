package br.com.gambling.entity;

import br.com.gambling.enums.RiskLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidade Avaliação de Risco - representa uma avaliação de risco de um usuário
 */
@Entity
@Table(name = "avaliacoes_risco")
public class RiskAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    @NotNull(message = "Usuário é obrigatório")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_risco", nullable = false)
    @NotNull(message = "Nível de risco é obrigatório")
    private RiskLevel riskLevel;

    @Column(name = "pontuacao_risco")
    private Double riskScore;

    @Column(name = "motivo_avaliacao", columnDefinition = "TEXT")
    private String assessmentReason;

    @Column(name = "indicadores_comportamentais", columnDefinition = "TEXT")
    private String behavioralIndicators; // JSON string com indicadores comportamentais

    @Column(name = "indicadores_financeiros", columnDefinition = "TEXT")
    private String financialIndicators; // JSON string com indicadores financeiros

    @Column(name = "indicadores_temporais", columnDefinition = "TEXT")
    private String temporalIndicators; // JSON string com indicadores temporais

    @Column(name = "analise_sessoes", columnDefinition = "TEXT")
    private String sessionAnalysis; // JSON string com análise das sessões

    @Column(name = "recomendacoes", columnDefinition = "TEXT")
    private String recommendations; // JSON string com recomendações

    @Column(name = "automatica")
    private Boolean isAutomatic = true;

    @Column(name = "avaliado_por")
    private String assessedBy; // Sistema ou profissional que fez a avaliação

    @Column(name = "data_avaliacao", nullable = false)
    @NotNull(message = "Data da avaliação é obrigatória")
    private LocalDateTime assessmentDate;

    @Column(name = "valida_ate")
    private LocalDateTime validUntil;

    @Column(name = "ativa")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Construtores
    public RiskAssessment() {}

    public RiskAssessment(User user, RiskLevel riskLevel, Double riskScore) {
        this.user = user;
        this.riskLevel = riskLevel;
        this.riskScore = riskScore;
        this.assessmentDate = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }

    public String getAssessmentReason() {
        return assessmentReason;
    }

    public void setAssessmentReason(String assessmentReason) {
        this.assessmentReason = assessmentReason;
    }

    public String getBehavioralIndicators() {
        return behavioralIndicators;
    }

    public void setBehavioralIndicators(String behavioralIndicators) {
        this.behavioralIndicators = behavioralIndicators;
    }

    public String getFinancialIndicators() {
        return financialIndicators;
    }

    public void setFinancialIndicators(String financialIndicators) {
        this.financialIndicators = financialIndicators;
    }

    public String getTemporalIndicators() {
        return temporalIndicators;
    }

    public void setTemporalIndicators(String temporalIndicators) {
        this.temporalIndicators = temporalIndicators;
    }

    public String getSessionAnalysis() {
        return sessionAnalysis;
    }

    public void setSessionAnalysis(String sessionAnalysis) {
        this.sessionAnalysis = sessionAnalysis;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    public Boolean getIsAutomatic() {
        return isAutomatic;
    }

    public void setIsAutomatic(Boolean isAutomatic) {
        this.isAutomatic = isAutomatic;
    }

    public String getAssessedBy() {
        return assessedBy;
    }

    public void setAssessedBy(String assessedBy) {
        this.assessedBy = assessedBy;
    }

    public LocalDateTime getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(LocalDateTime assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Métodos de negócio
    public boolean isExpired() {
        return validUntil != null && LocalDateTime.now().isAfter(validUntil);
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void setValidityPeriod(int days) {
        this.validUntil = this.assessmentDate.plusDays(days);
    }
}