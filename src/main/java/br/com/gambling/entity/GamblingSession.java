package br.com.gambling.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidade Sessão de Apostas - representa uma sessão de apostas de um usuário
 */
@Entity
@Table(name = "sessoes_apostas")
public class GamblingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    @NotNull(message = "Usuário é obrigatório")
    private User user;

    @Column(name = "inicio_sessao", nullable = false)
    @NotNull(message = "Início da sessão é obrigatório")
    private LocalDateTime sessionStart;

    @Column(name = "fim_sessao")
    private LocalDateTime sessionEnd;

    @Column(name = "duracao_minutos")
    private Long durationMinutes;

    @Column(name = "valor_total_apostas")
    private Double totalBetAmount = 0.0;

    @Column(name = "valor_total_ganhos")
    private Double totalWinAmount = 0.0;

    @Column(name = "resultado_liquido")
    private Double netResult = 0.0;

    @Column(name = "contador_apostas")
    private Integer betCount = 0;

    @Column(name = "valor_maximo_aposta")
    private Double maxBetAmount = 0.0;

    @Column(name = "tipo_jogo")
    private String gameType;

    @Column(name = "plataforma")
    private String platform;

    @Column(name = "alta_frequencia")
    private Boolean isHighFrequency = false;

    @Column(name = "perseguicao_perdas")
    private Boolean hasLossChasing = false;

    @Column(name = "indicadores_risco")
    private String riskIndicators; // JSON string com indicadores de risco

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Construtores
    public GamblingSession() {}

    public GamblingSession(User user, LocalDateTime sessionStart) {
        this.user = user;
        this.sessionStart = sessionStart;
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

    // Métodos de negócio
    public void endSession() {
        this.sessionEnd = LocalDateTime.now();
        if (this.sessionStart != null) {
            this.durationMinutes = java.time.Duration.between(this.sessionStart, this.sessionEnd).toMinutes();
        }
    }

    public void addBet(Double amount) {
        this.totalBetAmount += amount;
        this.betCount++;
        if (amount > this.maxBetAmount) {
            this.maxBetAmount = amount;
        }
    }

    public void addWin(Double amount) {
        this.totalWinAmount += amount;
    }

    public void calculateNetResult() {
        this.netResult = this.totalWinAmount - this.totalBetAmount;
    }

    public boolean isSessionActive() {
        return this.sessionEnd == null;
    }

    public Double getAverageBetAmount() {
        return betCount > 0 ? totalBetAmount / betCount : 0.0;
    }
}