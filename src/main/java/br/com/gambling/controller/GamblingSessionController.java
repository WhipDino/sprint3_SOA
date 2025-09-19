package br.com.gambling.controller;

import br.com.gambling.dto.GamblingSessionRequestDto;
import br.com.gambling.dto.GamblingSessionResponseDto;
import br.com.gambling.service.GamblingSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para operações relacionadas às sessões de apostas
 */
@RestController
@RequestMapping("/api/sessions")
@Tag(name = "Sessões de Apostas", description = "Operações relacionadas às sessões de apostas")
public class GamblingSessionController {

    @Autowired
    private GamblingSessionService gamblingSessionService;

    @PostMapping
    @Operation(summary = "Criar nova sessão", description = "Cria uma nova sessão de apostas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sessão criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<GamblingSessionResponseDto> createSession(@Valid @RequestBody GamblingSessionRequestDto sessionRequest) {
        Optional<GamblingSessionResponseDto> createdSession = gamblingSessionService.createSession(sessionRequest);
        return createdSession.map(session -> ResponseEntity.status(HttpStatus.CREATED).body(session))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar sessão por ID", description = "Retorna os dados de uma sessão específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sessão encontrada"),
            @ApiResponse(responseCode = "404", description = "Sessão não encontrada")
    })
    public ResponseEntity<GamblingSessionResponseDto> getSessionById(@PathVariable Long id) {
        Optional<GamblingSessionResponseDto> session = gamblingSessionService.findById(id);
        return session.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/end")
    @Operation(summary = "Finalizar sessão", description = "Finaliza uma sessão de apostas e calcula indicadores de risco")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sessão finalizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Sessão não encontrada")
    })
    public ResponseEntity<GamblingSessionResponseDto> endSession(@PathVariable Long id) {
        Optional<GamblingSessionResponseDto> endedSession = gamblingSessionService.endSession(id);
        return endedSession.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/bets")
    @Operation(summary = "Adicionar aposta", description = "Adiciona uma aposta à sessão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aposta adicionada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Sessão não encontrada")
    })
    public ResponseEntity<Void> addBet(@PathVariable Long id, 
                                      @RequestParam Double amount) {
        boolean added = gamblingSessionService.addBet(id, amount);
        return added ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/wins")
    @Operation(summary = "Adicionar ganho", description = "Adiciona um ganho à sessão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ganho adicionado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Sessão não encontrada")
    })
    public ResponseEntity<Void> addWin(@PathVariable Long id, 
                                      @RequestParam Double amount) {
        boolean added = gamblingSessionService.addWin(id, amount);
        return added ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Listar sessões de um usuário", description = "Retorna todas as sessões de um usuário específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de sessões retornada com sucesso")
    })
    public ResponseEntity<List<GamblingSessionResponseDto>> getSessionsByUser(@PathVariable Long userId) {
        List<GamblingSessionResponseDto> sessions = gamblingSessionService.findByUser(userId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/user/{userId}/active")
    @Operation(summary = "Listar sessões ativas de um usuário", description = "Retorna sessões ativas (não finalizadas) de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de sessões ativas retornada com sucesso")
    })
    public ResponseEntity<List<GamblingSessionResponseDto>> getActiveSessionsByUser(@PathVariable Long userId) {
        List<GamblingSessionResponseDto> sessions = gamblingSessionService.findActiveSessionsByUser(userId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/period")
    @Operation(summary = "Listar sessões por período", description = "Retorna sessões dentro de um período específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de sessões retornada com sucesso")
    })
    public ResponseEntity<List<GamblingSessionResponseDto>> getSessionsByPeriod(
            @Parameter(description = "Data de início (formato: yyyy-MM-ddTHH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Data de fim (formato: yyyy-MM-ddTHH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<GamblingSessionResponseDto> sessions = gamblingSessionService.findSessionsByPeriod(startDate, endDate);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/user/{userId}/period")
    @Operation(summary = "Listar sessões de usuário por período", description = "Retorna sessões de um usuário dentro de um período específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de sessões retornada com sucesso")
    })
    public ResponseEntity<List<GamblingSessionResponseDto>> getUserSessionsByPeriod(
            @PathVariable Long userId,
            @Parameter(description = "Data de início (formato: yyyy-MM-ddTHH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Data de fim (formato: yyyy-MM-ddTHH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<GamblingSessionResponseDto> sessions = gamblingSessionService.findUserSessionsByPeriod(userId, startDate, endDate);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/high-frequency")
    @Operation(summary = "Listar sessões de alta frequência", description = "Retorna sessões identificadas como de alta frequência")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de sessões retornada com sucesso")
    })
    public ResponseEntity<List<GamblingSessionResponseDto>> getHighFrequencySessions() {
        List<GamblingSessionResponseDto> sessions = gamblingSessionService.findHighFrequencySessions();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/loss-chasing")
    @Operation(summary = "Listar sessões com perda chasing", description = "Retorna sessões identificadas com comportamento de perda chasing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de sessões retornada com sucesso")
    })
    public ResponseEntity<List<GamblingSessionResponseDto>> getLossChasingSessions() {
        List<GamblingSessionResponseDto> sessions = gamblingSessionService.findLossChasingSessions();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/significant-losses")
    @Operation(summary = "Listar sessões com perdas significativas", description = "Retorna sessões com perdas acima do valor máximo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de sessões retornada com sucesso")
    })
    public ResponseEntity<List<GamblingSessionResponseDto>> getSessionsWithSignificantLosses(
            @Parameter(description = "Valor máximo de perda") @RequestParam(defaultValue = "-500.0") Double maxLoss) {
        
        List<GamblingSessionResponseDto> sessions = gamblingSessionService.findSessionsWithSignificantLosses(maxLoss);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/high-bets")
    @Operation(summary = "Listar sessões com apostas altas", description = "Retorna sessões com apostas acima do valor mínimo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de sessões retornada com sucesso")
    })
    public ResponseEntity<List<GamblingSessionResponseDto>> getSessionsWithHighBets(
            @Parameter(description = "Valor mínimo de aposta") @RequestParam(defaultValue = "1000.0") Double minBetAmount) {
        
        List<GamblingSessionResponseDto> sessions = gamblingSessionService.findSessionsWithHighBets(minBetAmount);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/long-sessions")
    @Operation(summary = "Listar sessões longas", description = "Retorna sessões com duração acima do tempo mínimo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de sessões retornada com sucesso")
    })
    public ResponseEntity<List<GamblingSessionResponseDto>> getLongSessions(
            @Parameter(description = "Duração mínima em minutos") @RequestParam(defaultValue = "240") Long minDuration) {
        
        List<GamblingSessionResponseDto> sessions = gamblingSessionService.findLongSessions(minDuration);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping
    @Operation(summary = "Listar sessões com filtros", description = "Retorna uma lista paginada de sessões com filtros opcionais")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de sessões retornada com sucesso")
    })
    public ResponseEntity<Page<GamblingSessionResponseDto>> getSessions(
            @Parameter(description = "ID do usuário para filtrar") @RequestParam(required = false) Long userId,
            @Parameter(description = "Tipo de jogo para filtrar") @RequestParam(required = false) String gameType,
            @Parameter(description = "Plataforma para filtrar") @RequestParam(required = false) String platform,
            @Parameter(description = "Filtrar por alta frequência") @RequestParam(required = false) Boolean isHighFrequency,
            @Parameter(description = "Filtrar por perda chasing") @RequestParam(required = false) Boolean hasLossChasing,
            Pageable pageable) {
        
        Page<GamblingSessionResponseDto> sessions = gamblingSessionService.findSessionsWithFilters(
                userId, gameType, platform, isHighFrequency, hasLossChasing, pageable);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/user/{userId}/statistics")
    @Operation(summary = "Obter estatísticas de sessões do usuário", description = "Retorna estatísticas das sessões de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Object[]> getUserSessionStatistics(@PathVariable Long userId) {
        Object[] statistics = gamblingSessionService.calculateUserSessionStatistics(userId);
        return statistics != null ? ResponseEntity.ok(statistics) : ResponseEntity.notFound().build();
    }

    @GetMapping("/statistics/period")
    @Operation(summary = "Obter estatísticas de sessões por período", description = "Retorna estatísticas das sessões em um período específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso")
    })
    public ResponseEntity<Object[]> getSessionStatisticsByPeriod(
            @Parameter(description = "Data de início (formato: yyyy-MM-ddTHH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Data de fim (formato: yyyy-MM-ddTHH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        Object[] statistics = gamblingSessionService.calculateSessionStatisticsByPeriod(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/needing-risk-analysis")
    @Operation(summary = "Listar sessões que precisam de análise de risco", description = "Retorna sessões que ainda não foram analisadas para indicadores de risco")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de sessões retornada com sucesso")
    })
    public ResponseEntity<List<GamblingSessionResponseDto>> getSessionsNeedingRiskAnalysis() {
        List<GamblingSessionResponseDto> sessions = gamblingSessionService.findSessionsNeedingRiskAnalysis();
        return ResponseEntity.ok(sessions);
    }
}