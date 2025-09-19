package br.com.gambling.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

/**
 * DTO para criação de sessões de apostas
 */
public class GamblingSessionRequestDto {

    @NotNull(message = "ID do usuário é obrigatório")
    private Long userId;

    @NotNull(message = "Início da sessão é obrigatório")
    private LocalDateTime sessionStart;

    private LocalDateTime sessionEnd;
    private String gameType;
    private String platform;

    // Construtores
    public GamblingSessionRequestDto() {}

    public GamblingSessionRequestDto(Long userId, LocalDateTime sessionStart) {
        this.userId = userId;
        this.sessionStart = sessionStart;
    }

    // Getters e Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
}