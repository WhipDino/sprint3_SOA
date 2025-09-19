package br.com.gambling.entity;

import br.com.gambling.enums.InterventionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidade Intervenção - representa uma intervenção aplicada a um usuário
 */
@Entity
@Table(name = "intervencoes")
public class Intervention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    @NotNull(message = "Usuário é obrigatório")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_intervencao", nullable = false)
    @NotNull(message = "Tipo de intervenção é obrigatório")
    private InterventionType interventionType;

    @Column(name = "titulo", nullable = false)
    @NotNull(message = "Título é obrigatório")
    private String title;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String description;

    @Column(name = "mensagem", columnDefinition = "TEXT")
    private String message;

    @Column(name = "acao_necessaria")
    private String actionRequired;

    @Column(name = "info_contato")
    private String contactInfo;

    @Column(name = "agendada_para")
    private LocalDateTime scheduledFor;

    @Column(name = "executada_em")
    private LocalDateTime executedAt;

    @Column(name = "expira_em")
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InterventionStatus status = InterventionStatus.PENDING;

    @Column(name = "prioridade")
    private Integer priority = 1; // 1 = Baixa, 2 = Média, 3 = Alta, 4 = Crítica

    @Column(name = "automatica")
    private Boolean isAutomatic = true;

    @Column(name = "criada_por")
    private String createdBy = "SISTEMA";

    @Column(name = "executada_por")
    private String executedBy;

    @Column(name = "notas_execucao", columnDefinition = "TEXT")
    private String executionNotes;

    @Column(name = "resposta_usuario", columnDefinition = "TEXT")
    private String userResponse;

    @Column(name = "pontuacao_efetividade")
    private Integer effectivenessScore; // 1-5 escala de efetividade

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Construtores
    public Intervention() {}

    public Intervention(User user, InterventionType interventionType, String title, String description) {
        this.user = user;
        this.interventionType = interventionType;
        this.title = title;
        this.description = description;
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

    public InterventionType getInterventionType() {
        return interventionType;
    }

    public void setInterventionType(InterventionType interventionType) {
        this.interventionType = interventionType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getActionRequired() {
        return actionRequired;
    }

    public void setActionRequired(String actionRequired) {
        this.actionRequired = actionRequired;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public LocalDateTime getScheduledFor() {
        return scheduledFor;
    }

    public void setScheduledFor(LocalDateTime scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public InterventionStatus getStatus() {
        return status;
    }

    public void setStatus(InterventionStatus status) {
        this.status = status;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getIsAutomatic() {
        return isAutomatic;
    }

    public void setIsAutomatic(Boolean isAutomatic) {
        this.isAutomatic = isAutomatic;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getExecutedBy() {
        return executedBy;
    }

    public void setExecutedBy(String executedBy) {
        this.executedBy = executedBy;
    }

    public String getExecutionNotes() {
        return executionNotes;
    }

    public void setExecutionNotes(String executionNotes) {
        this.executionNotes = executionNotes;
    }

    public String getUserResponse() {
        return userResponse;
    }

    public void setUserResponse(String userResponse) {
        this.userResponse = userResponse;
    }

    public Integer getEffectivenessScore() {
        return effectivenessScore;
    }

    public void setEffectivenessScore(Integer effectivenessScore) {
        this.effectivenessScore = effectivenessScore;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Métodos de negócio
    public void execute(String executedBy) {
        this.status = InterventionStatus.EXECUTED;
        this.executedAt = LocalDateTime.now();
        this.executedBy = executedBy;
    }

    public void cancel(String reason) {
        this.status = InterventionStatus.CANCELLED;
        this.executionNotes = reason;
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isScheduled() {
        return scheduledFor != null && LocalDateTime.now().isBefore(scheduledFor);
    }

    public void setExpirationPeriod(int hours) {
        this.expiresAt = LocalDateTime.now().plusHours(hours);
    }

    // Enum para status da intervenção
    public enum InterventionStatus {
        PENDING("Pendente"),
        SCHEDULED("Agendada"),
        EXECUTED("Executada"),
        CANCELLED("Cancelada"),
        EXPIRED("Expirada");

        private final String description;

        InterventionStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}