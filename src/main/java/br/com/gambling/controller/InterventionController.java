package br.com.gambling.controller;

import br.com.gambling.dto.InterventionRequestDto;
import br.com.gambling.dto.InterventionResponseDto;
import br.com.gambling.entity.Intervention;
import br.com.gambling.enums.InterventionType;
import br.com.gambling.service.InterventionService;
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
 * Controlador REST para operações relacionadas às intervenções
 */
@RestController
@RequestMapping("/api/interventions")
@Tag(name = "Intervenções", description = "Operações relacionadas às intervenções para usuários em risco")
public class InterventionController {

    @Autowired
    private InterventionService interventionService;

    @PostMapping
    @Operation(summary = "Criar nova intervenção", description = "Cria uma nova intervenção para um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Intervenção criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<InterventionResponseDto> createIntervention(@Valid @RequestBody InterventionRequestDto interventionRequest) {
        Optional<InterventionResponseDto> createdIntervention = interventionService.createIntervention(interventionRequest);
        return createdIntervention.map(intervention -> ResponseEntity.status(HttpStatus.CREATED).body(intervention))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar intervenção por ID", description = "Retorna os dados de uma intervenção específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Intervenção encontrada"),
            @ApiResponse(responseCode = "404", description = "Intervenção não encontrada")
    })
    public ResponseEntity<InterventionResponseDto> getInterventionById(@PathVariable Long id) {
        Optional<InterventionResponseDto> intervention = interventionService.findById(id);
        return intervention.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/execute")
    @Operation(summary = "Executar intervenção", description = "Marca uma intervenção como executada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Intervenção executada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Intervenção não encontrada")
    })
    public ResponseEntity<Void> executeIntervention(@PathVariable Long id, 
                                                   @RequestParam String executedBy,
                                                   @RequestParam(required = false) String executionNotes) {
        boolean executed = interventionService.executeIntervention(id, executedBy, executionNotes);
        return executed ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancelar intervenção", description = "Cancela uma intervenção pendente ou agendada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Intervenção cancelada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Intervenção não encontrada")
    })
    public ResponseEntity<Void> cancelIntervention(@PathVariable Long id, 
                                                  @RequestParam String reason) {
        boolean cancelled = interventionService.cancelIntervention(id, reason);
        return cancelled ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/evaluate")
    @Operation(summary = "Avaliar efetividade da intervenção", description = "Avalia a efetividade de uma intervenção executada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Intervenção avaliada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Intervenção não encontrada"),
            @ApiResponse(responseCode = "400", description = "Score de efetividade inválido")
    })
    public ResponseEntity<Void> evaluateIntervention(@PathVariable Long id, 
                                                    @RequestParam Integer effectivenessScore,
                                                    @RequestParam(required = false) String userResponse) {
        if (effectivenessScore < 1 || effectivenessScore > 5) {
            return ResponseEntity.badRequest().build();
        }
        
        boolean evaluated = interventionService.evaluateIntervention(id, effectivenessScore, userResponse);
        return evaluated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Listar intervenções de um usuário", description = "Retorna todas as intervenções de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de intervenções retornada com sucesso")
    })
    public ResponseEntity<List<InterventionResponseDto>> getInterventionsByUser(@PathVariable Long userId) {
        List<InterventionResponseDto> interventions = interventionService.findByUser(userId);
        return ResponseEntity.ok(interventions);
    }

    @GetMapping("/pending")
    @Operation(summary = "Listar intervenções pendentes", description = "Retorna intervenções que estão pendentes de execução")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de intervenções retornada com sucesso")
    })
    public ResponseEntity<List<InterventionResponseDto>> getPendingInterventions() {
        List<InterventionResponseDto> interventions = interventionService.findPendingInterventions();
        return ResponseEntity.ok(interventions);
    }

    @GetMapping("/scheduled")
    @Operation(summary = "Listar intervenções agendadas para execução", description = "Retorna intervenções agendadas que devem ser executadas agora")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de intervenções retornada com sucesso")
    })
    public ResponseEntity<List<InterventionResponseDto>> getScheduledInterventionsToExecute() {
        List<InterventionResponseDto> interventions = interventionService.findScheduledInterventionsToExecute();
        return ResponseEntity.ok(interventions);
    }

    @GetMapping("/expired")
    @Operation(summary = "Listar intervenções expiradas", description = "Retorna intervenções que expiraram sem serem executadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de intervenções retornada com sucesso")
    })
    public ResponseEntity<List<InterventionResponseDto>> getExpiredInterventions() {
        List<InterventionResponseDto> interventions = interventionService.findExpiredInterventions();
        return ResponseEntity.ok(interventions);
    }

    @GetMapping("/high-priority")
    @Operation(summary = "Listar intervenções de alta prioridade", description = "Retorna intervenções com prioridade alta ou crítica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de intervenções retornada com sucesso")
    })
    public ResponseEntity<List<InterventionResponseDto>> getHighPriorityInterventions(
            @Parameter(description = "Prioridade mínima") @RequestParam(defaultValue = "3") Integer minPriority) {
        
        List<InterventionResponseDto> interventions = interventionService.findHighPriorityInterventions(minPriority);
        return ResponseEntity.ok(interventions);
    }

    @GetMapping("/type/{interventionType}")
    @Operation(summary = "Listar intervenções por tipo", description = "Retorna intervenções de um tipo específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de intervenções retornada com sucesso")
    })
    public ResponseEntity<List<InterventionResponseDto>> getInterventionsByType(@PathVariable InterventionType interventionType) {
        List<InterventionResponseDto> interventions = interventionService.findByType(interventionType);
        return ResponseEntity.ok(interventions);
    }

    @GetMapping("/period")
    @Operation(summary = "Listar intervenções por período", description = "Retorna intervenções criadas em um período específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de intervenções retornada com sucesso")
    })
    public ResponseEntity<List<InterventionResponseDto>> getInterventionsByPeriod(
            @Parameter(description = "Data de início (formato: yyyy-MM-ddTHH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Data de fim (formato: yyyy-MM-ddTHH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<InterventionResponseDto> interventions = interventionService.findInterventionsByPeriod(startDate, endDate);
        return ResponseEntity.ok(interventions);
    }

    @GetMapping("/executed/period")
    @Operation(summary = "Listar intervenções executadas por período", description = "Retorna intervenções executadas em um período específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de intervenções retornada com sucesso")
    })
    public ResponseEntity<List<InterventionResponseDto>> getExecutedInterventionsByPeriod(
            @Parameter(description = "Data de início (formato: yyyy-MM-ddTHH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Data de fim (formato: yyyy-MM-ddTHH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<InterventionResponseDto> interventions = interventionService.findExecutedInterventionsByPeriod(startDate, endDate);
        return ResponseEntity.ok(interventions);
    }

    @GetMapping("/needing-follow-up")
    @Operation(summary = "Listar intervenções que precisam de acompanhamento", description = "Retorna intervenções executadas que precisam de avaliação de efetividade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de intervenções retornada com sucesso")
    })
    public ResponseEntity<List<InterventionResponseDto>> getInterventionsNeedingFollowUp(
            @Parameter(description = "Número de dias para considerar necessidade de acompanhamento") @RequestParam(defaultValue = "7") int days) {
        
        List<InterventionResponseDto> interventions = interventionService.findInterventionsNeedingFollowUp(days);
        return ResponseEntity.ok(interventions);
    }

    @GetMapping("/low-effectiveness")
    @Operation(summary = "Listar intervenções com baixa efetividade", description = "Retorna intervenções com score de efetividade baixo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de intervenções retornada com sucesso")
    })
    public ResponseEntity<List<InterventionResponseDto>> getInterventionsWithLowEffectiveness(
            @Parameter(description = "Score máximo de efetividade") @RequestParam(defaultValue = "2") Integer maxScore) {
        
        List<InterventionResponseDto> interventions = interventionService.findInterventionsWithLowEffectiveness(maxScore);
        return ResponseEntity.ok(interventions);
    }

    @GetMapping
    @Operation(summary = "Listar intervenções com filtros", description = "Retorna uma lista paginada de intervenções com filtros opcionais")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de intervenções retornada com sucesso")
    })
    public ResponseEntity<Page<InterventionResponseDto>> getInterventions(
            @Parameter(description = "ID do usuário para filtrar") @RequestParam(required = false) Long userId,
            @Parameter(description = "Tipo de intervenção para filtrar") @RequestParam(required = false) InterventionType interventionType,
            @Parameter(description = "Status da intervenção para filtrar") @RequestParam(required = false) Intervention.InterventionStatus status,
            @Parameter(description = "Filtrar por intervenções automáticas") @RequestParam(required = false) Boolean isAutomatic,
            @Parameter(description = "Criador para filtrar") @RequestParam(required = false) String createdBy,
            Pageable pageable) {
        
        Page<InterventionResponseDto> interventions = interventionService.findInterventionsWithFilters(
                userId, interventionType, status, isAutomatic, createdBy, pageable);
        return ResponseEntity.ok(interventions);
    }

    @GetMapping("/statistics/period")
    @Operation(summary = "Obter estatísticas de intervenções por período", description = "Retorna estatísticas das intervenções em um período específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso")
    })
    public ResponseEntity<Object[]> getInterventionStatisticsByPeriod(
            @Parameter(description = "Data de início (formato: yyyy-MM-ddTHH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Data de fim (formato: yyyy-MM-ddTHH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        Object[] statistics = interventionService.calculateInterventionStatisticsByPeriod(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/type-counts")
    @Operation(summary = "Contar intervenções por tipo", description = "Retorna contagem de intervenções por tipo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contagens retornadas com sucesso")
    })
    public ResponseEntity<List<Object[]>> countInterventionsByType() {
        List<Object[]> counts = interventionService.countInterventionsByType();
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/status-counts")
    @Operation(summary = "Contar intervenções por status", description = "Retorna contagem de intervenções por status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contagens retornadas com sucesso")
    })
    public ResponseEntity<List<Object[]>> countInterventionsByStatus() {
        List<Object[]> counts = interventionService.countInterventionsByStatus();
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/effectiveness-by-type")
    @Operation(summary = "Calcular efetividade por tipo", description = "Retorna efetividade média por tipo de intervenção")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Efetividade calculada com sucesso")
    })
    public ResponseEntity<List<Object[]>> calculateEffectivenessByType() {
        List<Object[]> effectiveness = interventionService.calculateEffectivenessByType();
        return ResponseEntity.ok(effectiveness);
    }

    @GetMapping("/user/{userId}/has-pending")
    @Operation(summary = "Verificar se usuário tem intervenções pendentes", description = "Verifica se um usuário tem intervenções pendentes ou agendadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificação realizada com sucesso")
    })
    public ResponseEntity<Boolean> hasPendingInterventions(@PathVariable Long userId) {
        boolean hasPending = interventionService.hasPendingInterventions(userId);
        return ResponseEntity.ok(hasPending);
    }

    @PostMapping("/execute-scheduled-automatic")
    @Operation(summary = "Executar intervenções automáticas agendadas", description = "Executa automaticamente intervenções agendadas que devem ser executadas agora")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Intervenções executadas com sucesso")
    })
    public ResponseEntity<List<InterventionResponseDto>> executeScheduledAutomaticInterventions() {
        List<InterventionResponseDto> executedInterventions = interventionService.executeScheduledAutomaticInterventions();
        return ResponseEntity.ok(executedInterventions);
    }
}