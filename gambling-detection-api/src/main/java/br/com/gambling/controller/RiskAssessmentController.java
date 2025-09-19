package br.com.gambling.controller;

import br.com.gambling.dto.RiskAnalysisRequestDto;
import br.com.gambling.dto.RiskAssessmentResponseDto;
import br.com.gambling.enums.RiskLevel;
import br.com.gambling.service.RiskAssessmentService;
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
 * Controlador REST para operações relacionadas às avaliações de risco
 */
@RestController
@RequestMapping("/api/risk-assessments")
@Tag(name = "Avaliações de Risco", description = "Operações relacionadas às avaliações de risco dos usuários")
public class RiskAssessmentController {

    @Autowired
    private RiskAssessmentService riskAssessmentService;

    @PostMapping("/analyze")
    @Operation(summary = "Realizar análise de risco", description = "Realiza uma análise de risco completa de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Análise realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<RiskAssessmentResponseDto> performRiskAnalysis(@Valid @RequestBody RiskAnalysisRequestDto analysisRequest) {
        try {
            RiskAssessmentResponseDto assessment = riskAssessmentService.performRiskAnalysis(analysisRequest);
            return ResponseEntity.ok(assessment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}/latest")
    @Operation(summary = "Buscar avaliação mais recente", description = "Retorna a avaliação de risco mais recente de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avaliação encontrada"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado ou sem avaliações")
    })
    public ResponseEntity<RiskAssessmentResponseDto> getLatestAssessmentByUser(@PathVariable Long userId) {
        Optional<RiskAssessmentResponseDto> assessment = riskAssessmentService.findLatestByUser(userId);
        return assessment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Listar avaliações de um usuário", description = "Retorna todas as avaliações de risco de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de avaliações retornada com sucesso")
    })
    public ResponseEntity<List<RiskAssessmentResponseDto>> getAssessmentsByUser(@PathVariable Long userId) {
        List<RiskAssessmentResponseDto> assessments = riskAssessmentService.findByUser(userId);
        return ResponseEntity.ok(assessments);
    }

    @GetMapping("/risk-level/{riskLevel}")
    @Operation(summary = "Listar avaliações por nível de risco", description = "Retorna avaliações com um nível de risco específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de avaliações retornada com sucesso")
    })
    public ResponseEntity<List<RiskAssessmentResponseDto>> getAssessmentsByRiskLevel(@PathVariable RiskLevel riskLevel) {
        List<RiskAssessmentResponseDto> assessments = riskAssessmentService.findByRiskLevel(riskLevel);
        return ResponseEntity.ok(assessments);
    }

    @GetMapping("/high-risk")
    @Operation(summary = "Listar avaliações de alto risco", description = "Retorna avaliações com nível de risco alto ou crítico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de avaliações retornada com sucesso")
    })
    public ResponseEntity<List<RiskAssessmentResponseDto>> getHighRiskAssessments() {
        List<RiskAssessmentResponseDto> assessments = riskAssessmentService.findHighRiskAssessments();
        return ResponseEntity.ok(assessments);
    }

    @GetMapping("/expired")
    @Operation(summary = "Listar avaliações expiradas", description = "Retorna avaliações que já expiraram")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de avaliações retornada com sucesso")
    })
    public ResponseEntity<List<RiskAssessmentResponseDto>> getExpiredAssessments() {
        List<RiskAssessmentResponseDto> assessments = riskAssessmentService.findExpiredAssessments();
        return ResponseEntity.ok(assessments);
    }

    @GetMapping("/needing-renewal")
    @Operation(summary = "Listar avaliações que precisam de renovação", description = "Retorna avaliações que precisam ser renovadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de avaliações retornada com sucesso")
    })
    public ResponseEntity<List<RiskAssessmentResponseDto>> getAssessmentsNeedingRenewal(
            @Parameter(description = "Número de dias para considerar necessidade de renovação") @RequestParam(defaultValue = "7") int days) {
        
        List<RiskAssessmentResponseDto> assessments = riskAssessmentService.findAssessmentsNeedingRenewal(days);
        return ResponseEntity.ok(assessments);
    }

    @GetMapping
    @Operation(summary = "Listar avaliações com filtros", description = "Retorna uma lista paginada de avaliações com filtros opcionais")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de avaliações retornada com sucesso")
    })
    public ResponseEntity<Page<RiskAssessmentResponseDto>> getAssessments(
            @Parameter(description = "ID do usuário para filtrar") @RequestParam(required = false) Long userId,
            @Parameter(description = "Nível de risco para filtrar") @RequestParam(required = false) RiskLevel riskLevel,
            @Parameter(description = "Filtrar por avaliações automáticas") @RequestParam(required = false) Boolean isAutomatic,
            @Parameter(description = "Avaliador para filtrar") @RequestParam(required = false) String assessedBy,
            @Parameter(description = "Filtrar por avaliações ativas") @RequestParam(required = false) Boolean isActive,
            Pageable pageable) {
        
        Page<RiskAssessmentResponseDto> assessments = riskAssessmentService.findAssessmentsWithFilters(
                userId, riskLevel, isAutomatic, assessedBy, isActive, pageable);
        return ResponseEntity.ok(assessments);
    }

    @GetMapping("/statistics/period")
    @Operation(summary = "Obter estatísticas de avaliações por período", description = "Retorna estatísticas das avaliações em um período específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso")
    })
    public ResponseEntity<Object[]> getAssessmentStatisticsByPeriod(
            @Parameter(description = "Data de início (formato: yyyy-MM-ddTHH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Data de fim (formato: yyyy-MM-ddTHH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        Object[] statistics = riskAssessmentService.calculateAssessmentStatisticsByPeriod(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/risk-level-counts")
    @Operation(summary = "Contar avaliações por nível de risco", description = "Retorna contagem de avaliações por nível de risco")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contagens retornadas com sucesso")
    })
    public ResponseEntity<List<Object[]>> countAssessmentsByRiskLevel() {
        List<Object[]> counts = riskAssessmentService.countAssessmentsByRiskLevel();
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/users-needing-assessment")
    @Operation(summary = "Listar usuários que precisam de nova avaliação", description = "Retorna usuários que precisam de nova avaliação de risco")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    })
    public ResponseEntity<List<Object>> getUsersNeedingNewAssessment(
            @Parameter(description = "Número de dias para considerar necessidade de nova avaliação") @RequestParam(defaultValue = "30") int days) {
        
        List<Object> users = riskAssessmentService.findUsersNeedingNewAssessment(days).stream()
                .map(user -> {
                    // Retorna apenas informações básicas do usuário
                    return new Object() {
                        public final Long id = user.getId();
                        public final String name = user.getName();
                        public final String email = user.getEmail();
                        public final RiskLevel currentRiskLevel = user.getCurrentRiskLevel();
                        public final LocalDateTime lastActivity = user.getLastActivity();
                    };
                })
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PostMapping("/{assessmentId}/deactivate-old")
    @Operation(summary = "Desativar avaliações antigas", description = "Desativa avaliações antigas de um usuário, mantendo apenas a atual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avaliações antigas desativadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    public ResponseEntity<Void> deactivateOldAssessments(@PathVariable Long assessmentId, 
                                                        @RequestParam Long userId) {
        riskAssessmentService.deactivateOldAssessments(userId, assessmentId);
        return ResponseEntity.ok().build();
    }
}