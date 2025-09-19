package br.com.gambling.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Usuário - representa um usuário do sistema
 */
@Entity
@Table(name = "usuarios")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(name = "nome", nullable = false)
    private String name;

    @Email(message = "Email deve ser válido")
    @NotBlank(message = "Email é obrigatório")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "telefone")
    private String phoneNumber;

    @Column(name = "data_nascimento")
    private LocalDateTime dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_risco_atual")
    private br.com.gambling.enums.RiskLevel currentRiskLevel = br.com.gambling.enums.RiskLevel.LOW;

    @Column(name = "total_depositos")
    private Double totalDeposits = 0.0;

    @Column(name = "total_saques")
    private Double totalWithdrawals = 0.0;

    @Column(name = "contador_sessoes")
    private Integer sessionCount = 0;

    @Column(name = "ultima_atividade")
    private LocalDateTime lastActivity;

    @Column(name = "ativo")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GamblingSession> gamblingSessions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RiskAssessment> riskAssessments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Intervention> interventions = new ArrayList<>();

    // Construtores
    public User() {}

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public br.com.gambling.enums.RiskLevel getCurrentRiskLevel() {
        return currentRiskLevel;
    }

    public void setCurrentRiskLevel(br.com.gambling.enums.RiskLevel currentRiskLevel) {
        this.currentRiskLevel = currentRiskLevel;
    }

    public Double getTotalDeposits() {
        return totalDeposits;
    }

    public void setTotalDeposits(Double totalDeposits) {
        this.totalDeposits = totalDeposits;
    }

    public Double getTotalWithdrawals() {
        return totalWithdrawals;
    }

    public void setTotalWithdrawals(Double totalWithdrawals) {
        this.totalWithdrawals = totalWithdrawals;
    }

    public Integer getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(Integer sessionCount) {
        this.sessionCount = sessionCount;
    }

    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<GamblingSession> getGamblingSessions() {
        return gamblingSessions;
    }

    public void setGamblingSessions(List<GamblingSession> gamblingSessions) {
        this.gamblingSessions = gamblingSessions;
    }

    public List<RiskAssessment> getRiskAssessments() {
        return riskAssessments;
    }

    public void setRiskAssessments(List<RiskAssessment> riskAssessments) {
        this.riskAssessments = riskAssessments;
    }

    public List<Intervention> getInterventions() {
        return interventions;
    }

    public void setInterventions(List<Intervention> interventions) {
        this.interventions = interventions;
    }

    // Métodos de negócio
    public void incrementSessionCount() {
        this.sessionCount++;
        this.lastActivity = LocalDateTime.now();
    }

    public void addDeposit(Double amount) {
        this.totalDeposits += amount;
        this.lastActivity = LocalDateTime.now();
    }

    public void addWithdrawal(Double amount) {
        this.totalWithdrawals += amount;
        this.lastActivity = LocalDateTime.now();
    }

    public Double getNetBalance() {
        return totalDeposits - totalWithdrawals;
    }
}