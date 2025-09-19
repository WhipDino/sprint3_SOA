package br.com.gambling.dto;

import java.time.LocalDateTime;

/**
 * DTO para resposta de dados de sessões de apostas
 */
public class GamblingSessionResponseDto {

    private Long id;
    private Long userId;
    private String userName;
    private LocalDateTime sessionStart;
    private LocalDateTime sessionEnd;
    private Long durationMinutes;
    private Double totalBetAmount;
    private Double totalWinAmount;
    private Double netResult;
    private Integer betCount;
    private Double maxBetAmount;
    private Double averageBetAmount;
    private String gameType;
    private String platform;
    private Boolean isHighFrequency;
    private Boolean hasLossChasing;
    private String riskIndicators;
    private LocalDateTime createdAt;

    // Construtores
    public GamblingSessionResponseDto() {}

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

    public LocalDateTime getSessionStart() {
        return sessionStart;
    }

    public void setSessionStart(LocalDateTime sessionStart) {
        this.sessionStart = sessionStart;
    }

    public LocalDateTime getSessionEnd() {
        return sessionEnd;
    }

    public void setSessionEnd(LocalDateTime sessionEnd) {
        this.sessionEnd = sessionEnd;
    }

    public Long getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Long durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Double getTotalBetAmount() {
        return totalBetAmount;
    }

    public void setTotalBetAmount(Double totalBetAmount) {
        this.totalBetAmount = totalBetAmount;
    }

    public Double getTotalWinAmount() {
        return totalWinAmount;
    }

    public void setTotalWinAmount(Double totalWinAmount) {
        this.totalWinAmount = totalWinAmount;
    }

    public Double getNetResult() {
        return netResult;
    }

    public void setNetResult(Double netResult) {
        this.netResult = netResult;
    }

    public Integer getBetCount() {
        return betCount;
    }

    public void setBetCount(Integer betCount) {
        this.betCount = betCount;
    }

    public Double getMaxBetAmount() {
        return maxBetAmount;
    }

    public void setMaxBetAmount(Double maxBetAmount) {
        this.maxBetAmount = maxBetAmount;
    }

    public Double getAverageBetAmount() {
        return averageBetAmount;
    }

    public void setAverageBetAmount(Double averageBetAmount) {
        this.averageBetAmount = averageBetAmount;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Boolean getIsHighFrequency() {
        return isHighFrequency;
    }

    public void setIsHighFrequency(Boolean isHighFrequency) {
        this.isHighFrequency = isHighFrequency;
    }

    public Boolean getHasLossChasing() {
        return hasLossChasing;
    }

    public void setHasLossChasing(Boolean hasLossChasing) {
        this.hasLossChasing = hasLossChasing;
    }

    public String getRiskIndicators() {
        return riskIndicators;
    }

    public void setRiskIndicators(String riskIndicators) {
        this.riskIndicators = riskIndicators;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}