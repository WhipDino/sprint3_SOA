package br.com.gambling.dto;

import br.com.gambling.enums.RiskLevel;

import java.time.LocalDateTime;

/**
 * DTO para resposta de avaliações de risco
 */
public class RiskAssessmentResponseDto {

    private Long id;
    private Long userId;
    private String userName;
    private RiskLevel riskLevel;
    private Double riskScore;
    private String assessmentReason;
    private String behavioralIndicators;
    private String financialIndicators;
    private String temporalIndicators;
    private String sessionAnalysis;
    private String recommendations;
    private Boolean isAutomatic;
    private String assessedBy;
    private LocalDateTime assessmentDate;
    private LocalDateTime validUntil;
    private Boolean isActive;
    private LocalDateTime createdAt;

    // Construtores
    public RiskAssessmentResponseDto() {}

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
}