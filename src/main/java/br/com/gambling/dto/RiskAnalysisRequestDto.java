package br.com.gambling.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para solicitação de análise de risco de um usuário
 */
public class RiskAnalysisRequestDto {

    @NotNull(message = "ID do usuário é obrigatório")
    private Long userId;

    private LocalDateTime analysisDate;
    private List<String> additionalFactors;
    private Boolean forceReanalysis = false;

    // Construtores
    public RiskAnalysisRequestDto() {}

    public RiskAnalysisRequestDto(Long userId) {
        this.userId = userId;
        this.analysisDate = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getAnalysisDate() {
        return analysisDate;
    }

    public void setAnalysisDate(LocalDateTime analysisDate) {
        this.analysisDate = analysisDate;
    }

    public List<String> getAdditionalFactors() {
        return additionalFactors;
    }

    public void setAdditionalFactors(List<String> additionalFactors) {
        this.additionalFactors = additionalFactors;
    }

    public Boolean getForceReanalysis() {
        return forceReanalysis;
    }

    public void setForceReanalysis(Boolean forceReanalysis) {
        this.forceReanalysis = forceReanalysis;
    }
}