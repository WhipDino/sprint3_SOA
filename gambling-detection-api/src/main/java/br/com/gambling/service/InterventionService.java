package br.com.gambling.service;

import br.com.gambling.dto.InterventionRequestDto;
import br.com.gambling.dto.InterventionResponseDto;
import br.com.gambling.entity.Intervention;
import br.com.gambling.entity.User;
import br.com.gambling.enums.InterventionType;
import br.com.gambling.enums.RiskLevel;
import br.com.gambling.repository.InterventionRepository;
import br.com.gambling.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço para operações relacionadas às intervenções
 */
@Service
@Transactional
public class InterventionService {

    @Autowired
    private InterventionRepository interventionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Cria uma nova intervenção
     */
    public Optional<InterventionResponseDto> createIntervention(InterventionRequestDto interventionRequest) {
        return userRepository.findById(interventionRequest.getUserId())
                .map(user -> {
                    Intervention intervention = new Intervention();
                    intervention.setUser(user);
                    intervention.setInterventionType(interventionRequest.getInterventionType());
                    intervention.setTitle(interventionRequest.getTitle());
                    intervention.setDescription(interventionRequest.getDescription());
                    intervention.setMessage(interventionRequest.getMessage());
                    intervention.setActionRequired(interventionRequest.getActionRequired());
                    intervention.setContactInfo(interventionRequest.getContactInfo());
                    intervention.setScheduledFor(interventionRequest.getScheduledFor());
                    intervention.setExpiresAt(interventionRequest.getExpiresAt());
                    intervention.setPriority(interventionRequest.getPriority());
                    intervention.setIsAutomatic(interventionRequest.getIsAutomatic());
                    intervention.setCreatedBy(interventionRequest.getCreatedBy());

                    // Define status baseado no agendamento
                    if (intervention.getScheduledFor() != null && intervention.getScheduledFor().isAfter(LocalDateTime.now())) {
                        intervention.setStatus(Intervention.InterventionStatus.SCHEDULED);
                    } else {
                        intervention.setStatus(Intervention.InterventionStatus.PENDING);
                    }

                    // Define expiração padrão se não especificada
                    if (intervention.getExpiresAt() == null) {
                        intervention.setExpirationPeriod(24); // 24 horas
                    }

                    Intervention savedIntervention = interventionRepository.save(intervention);
                    return mapToResponseDto(savedIntervention);
                });
    }

    /**
     * Busca uma intervenção por ID
     */
    @Transactional(readOnly = true)
    public Optional<InterventionResponseDto> findById(Long id) {
        return interventionRepository.findById(id)
                .map(this::mapToResponseDto);
    }

    /**
     * Executa uma intervenção
     */
    public boolean executeIntervention(Long id, String executedBy, String executionNotes) {
        return interventionRepository.findById(id)
                .map(intervention -> {
                    intervention.execute(executedBy);
                    if (executionNotes != null) {
                        intervention.setExecutionNotes(executionNotes);
                    }
                    interventionRepository.save(intervention);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Cancela uma intervenção
     */
    public boolean cancelIntervention(Long id, String reason) {
        return interventionRepository.findById(id)
                .map(intervention -> {
                    intervention.cancel(reason);
                    interventionRepository.save(intervention);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Avalia a efetividade de uma intervenção
     */
    public boolean evaluateIntervention(Long id, Integer effectivenessScore, String userResponse) {
        return interventionRepository.findById(id)
                .map(intervention -> {
                    intervention.setEffectivenessScore(effectivenessScore);
                    if (userResponse != null) {
                        intervention.setUserResponse(userResponse);
                    }
                    interventionRepository.save(intervention);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Busca intervenções de um usuário
     */
    @Transactional(readOnly = true)
    public List<InterventionResponseDto> findByUser(Long userId) {
        return userRepository.findById(userId)
                .map(user -> interventionRepository.findByUserOrderByCreatedAtDesc(user)
                        .stream()
                        .map(this::mapToResponseDto)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    /**
     * Busca intervenções pendentes
     */
    @Transactional(readOnly = true)
    public List<InterventionResponseDto> findPendingInterventions() {
        return interventionRepository.findPendingInterventions()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca intervenções agendadas para execução
     */
    @Transactional(readOnly = true)
    public List<InterventionResponseDto> findScheduledInterventionsToExecute() {
        return interventionRepository.findScheduledInterventionsToExecute(LocalDateTime.now())
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca intervenções expiradas
     */
    @Transactional(readOnly = true)
    public List<InterventionResponseDto> findExpiredInterventions() {
        return interventionRepository.findExpiredInterventions()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca intervenções de alta prioridade
     */
    @Transactional(readOnly = true)
    public List<InterventionResponseDto> findHighPriorityInterventions(Integer minPriority) {
        return interventionRepository.findHighPriorityInterventions(minPriority)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca intervenções por tipo
     */
    @Transactional(readOnly = true)
    public List<InterventionResponseDto> findByType(InterventionType interventionType) {
        return interventionRepository.findByInterventionTypeOrderByCreatedAtDesc(interventionType)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca intervenções por período
     */
    @Transactional(readOnly = true)
    public List<InterventionResponseDto> findInterventionsByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return interventionRepository.findInterventionsByPeriod(startDate, endDate)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca intervenções executadas por período
     */
    @Transactional(readOnly = true)
    public List<InterventionResponseDto> findExecutedInterventionsByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return interventionRepository.findExecutedInterventionsByPeriod(startDate, endDate)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca intervenções que precisam de acompanhamento
     */
    @Transactional(readOnly = true)
    public List<InterventionResponseDto> findInterventionsNeedingFollowUp(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return interventionRepository.findInterventionsNeedingFollowUp(cutoffDate)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca intervenções com baixa efetividade
     */
    @Transactional(readOnly = true)
    public List<InterventionResponseDto> findInterventionsWithLowEffectiveness(Integer maxScore) {
        return interventionRepository.findInterventionsWithLowEffectiveness(maxScore)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca intervenções com filtros
     */
    @Transactional(readOnly = true)
    public Page<InterventionResponseDto> findInterventionsWithFilters(Long userId, InterventionType interventionType,
                                                                     Intervention.InterventionStatus status,
                                                                     Boolean isAutomatic, String createdBy,
                                                                     Pageable pageable) {
        return interventionRepository.findInterventionsWithFilters(userId, interventionType, status, isAutomatic, createdBy, pageable)
                .map(this::mapToResponseDto);
    }

    /**
     * Calcula estatísticas de intervenções por período
     */
    @Transactional(readOnly = true)
    public Object[] calculateInterventionStatisticsByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return interventionRepository.calculateInterventionStatisticsByPeriod(startDate, endDate);
    }

    /**
     * Conta intervenções por tipo
     */
    @Transactional(readOnly = true)
    public List<Object[]> countInterventionsByType() {
        return interventionRepository.countInterventionsByType();
    }

    /**
     * Conta intervenções por status
     */
    @Transactional(readOnly = true)
    public List<Object[]> countInterventionsByStatus() {
        return interventionRepository.countInterventionsByStatus();
    }

    /**
     * Calcula efetividade por tipo
     */
    @Transactional(readOnly = true)
    public List<Object[]> calculateEffectivenessByType() {
        return interventionRepository.calculateEffectivenessByType();
    }

    /**
     * Verifica se usuário tem intervenções pendentes
     */
    @Transactional(readOnly = true)
    public boolean hasPendingInterventions(Long userId) {
        return userRepository.findById(userId)
                .map(user -> interventionRepository.hasPendingInterventions(user))
                .orElse(false);
    }

    /**
     * Executa intervenções automáticas agendadas
     */
    public List<InterventionResponseDto> executeScheduledAutomaticInterventions() {
        List<Intervention> scheduledInterventions = interventionRepository.findScheduledInterventionsToExecute(LocalDateTime.now());
        
        return scheduledInterventions.stream()
                .filter(intervention -> intervention.getIsAutomatic())
                .map(intervention -> {
                    intervention.execute("SISTEMA_AUTOMATICO");
                    interventionRepository.save(intervention);
                    return mapToResponseDto(intervention);
                })
                .collect(Collectors.toList());
    }

    /**
     * Cria intervenção automática baseada no nível de risco
     */
    public void createAutomaticIntervention(User user, RiskLevel riskLevel) {
        InterventionType interventionType = determineInterventionType(riskLevel);
        String title = generateInterventionTitle(riskLevel);
        String description = generateInterventionDescription(riskLevel);
        String message = generateInterventionMessage(riskLevel);

        Intervention intervention = new Intervention();
        intervention.setUser(user);
        intervention.setInterventionType(interventionType);
        intervention.setTitle(title);
        intervention.setDescription(description);
        intervention.setMessage(message);
        intervention.setIsAutomatic(true);
        intervention.setCreatedBy("SISTEMA");
        intervention.setStatus(Intervention.InterventionStatus.PENDING);
        intervention.setPriority(determinePriority(riskLevel));
        intervention.setExpirationPeriod(24);

        interventionRepository.save(intervention);
    }

    /**
     * Determina o tipo de intervenção baseado no nível de risco
     */
    private InterventionType determineInterventionType(RiskLevel riskLevel) {
        return switch (riskLevel) {
            case CRITICAL -> InterventionType.TEMPORARY_BLOCK;
            case HIGH -> InterventionType.WARNING;
            case MEDIUM -> InterventionType.ALTERNATIVE_SUGGESTION;
            case LOW -> InterventionType.ALTERNATIVE_SUGGESTION;
        };
    }

    /**
     * Gera título da intervenção
     */
    private String generateInterventionTitle(RiskLevel riskLevel) {
        return switch (riskLevel) {
            case CRITICAL -> "Bloqueio Temporário - Risco Crítico";
            case HIGH -> "Aviso de Risco Alto";
            case MEDIUM -> "Sugestão de Alternativas";
            case LOW -> "Orientação Preventiva";
        };
    }

    /**
     * Gera descrição da intervenção
     */
    private String generateInterventionDescription(RiskLevel riskLevel) {
        return switch (riskLevel) {
            case CRITICAL -> "Intervenção automática devido a indicadores de risco crítico detectados.";
            case HIGH -> "Aviso automático sobre padrões de comportamento de alto risco.";
            case MEDIUM -> "Sugestão automática de alternativas para reduzir riscos.";
            case LOW -> "Orientação preventiva para manter práticas saudáveis.";
        };
    }

    /**
     * Gera mensagem da intervenção
     */
    private String generateInterventionMessage(RiskLevel riskLevel) {
        return switch (riskLevel) {
            case CRITICAL -> "Detectamos padrões preocupantes em sua atividade. Recomendamos uma pausa e contato com profissionais especializados.";
            case HIGH -> "Identificamos alguns padrões que merecem atenção. Considere reduzir a frequência e valor das apostas.";
            case MEDIUM -> "Sugerimos algumas alternativas para diversificar suas atividades e reduzir riscos.";
            case LOW -> "Continue mantendo práticas responsáveis. Aqui estão algumas dicas para continuar assim.";
        };
    }

    /**
     * Determina prioridade baseada no nível de risco
     */
    private Integer determinePriority(RiskLevel riskLevel) {
        return switch (riskLevel) {
            case CRITICAL -> 4;
            case HIGH -> 3;
            case MEDIUM -> 2;
            case LOW -> 1;
        };
    }

    /**
     * Mapeia entidade Intervention para DTO de resposta
     */
    private InterventionResponseDto mapToResponseDto(Intervention intervention) {
        InterventionResponseDto dto = new InterventionResponseDto();
        dto.setId(intervention.getId());
        dto.setUserId(intervention.getUser().getId());
        dto.setUserName(intervention.getUser().getName());
        dto.setInterventionType(intervention.getInterventionType());
        dto.setTitle(intervention.getTitle());
        dto.setDescription(intervention.getDescription());
        dto.setMessage(intervention.getMessage());
        dto.setActionRequired(intervention.getActionRequired());
        dto.setContactInfo(intervention.getContactInfo());
        dto.setScheduledFor(intervention.getScheduledFor());
        dto.setExecutedAt(intervention.getExecutedAt());
        dto.setExpiresAt(intervention.getExpiresAt());
        dto.setStatus(intervention.getStatus());
        dto.setPriority(intervention.getPriority());
        dto.setIsAutomatic(intervention.getIsAutomatic());
        dto.setCreatedBy(intervention.getCreatedBy());
        dto.setExecutedBy(intervention.getExecutedBy());
        dto.setExecutionNotes(intervention.getExecutionNotes());
        dto.setUserResponse(intervention.getUserResponse());
        dto.setEffectivenessScore(intervention.getEffectivenessScore());
        dto.setCreatedAt(intervention.getCreatedAt());
        return dto;
    }
}