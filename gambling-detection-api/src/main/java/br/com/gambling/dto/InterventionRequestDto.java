package br.com.gambling.dto;

import br.com.gambling.enums.InterventionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

/**
 * DTO para criação de intervenções
 */
public class InterventionRequestDto {

    @NotNull(message = "ID do usuário é obrigatório")
    private Long userId;

    @NotNull(message = "Tipo de intervenção é obrigatório")
    private InterventionType interventionType;

    @NotBlank(message = "Título é obrigatório")
    private String title;

    private String description;
    private String message;
    private String actionRequired;
    private String contactInfo;
    private LocalDateTime scheduledFor;
    private LocalDateTime expiresAt;
    private Integer priority = 1;
    private Boolean isAutomatic = false;
    private String createdBy = "MANUAL";

    // Construtores
    public InterventionRequestDto() {}

    public InterventionRequestDto(Long userId, InterventionType interventionType, String title) {
        this.userId = userId;
        this.interventionType = interventionType;
        this.title = title;
    }

    // Getters e Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
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
}