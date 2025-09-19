package br.com.gambling.service;

import br.com.gambling.dto.RiskAnalysisRequestDto;
import br.com.gambling.dto.RiskAssessmentResponseDto;
import br.com.gambling.entity.RiskAssessment;
import br.com.gambling.entity.User;
import br.com.gambling.enums.RiskLevel;
import br.com.gambling.repository.RiskAssessmentRepository;
import br.com.gambling.repository.UserRepository;
import br.com.gambling.repository.GamblingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Serviço para operações relacionadas às avaliações de risco
 */
@Service
@Transactional
public class RiskAssessmentService {

    @Autowired
    private RiskAssessmentRepository riskAssessmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GamblingSessionRepository gamblingSessionRepository;

    /**
     * Realiza análise de risco de um usuário
     */
    public RiskAssessmentResponseDto performRiskAnalysis(RiskAnalysisRequestDto analysisRequest) {
        return userRepository.findById(analysisRequest.getUserId())
                .map(user -> {
                    // Verifica se já existe uma avaliação recente válida
                    if (!analysisRequest.getForceReanalysis()) {
                        Optional<RiskAssessment> existingAssessment = riskAssessmentRepository.findLatestByUser(user);
                        if (existingAssessment.isPresent() && !existingAssessment.get().isExpired()) {
                            return mapToResponseDto(existingAssessment.get());
                        }
                    }

                    // Realiza nova análise
                    RiskAssessment assessment = createRiskAssessment(user, analysisRequest);
                    RiskAssessment savedAssessment = riskAssessmentRepository.save(assessment);
                    
                    // Atualiza nível de risco do usuário
                    user.setCurrentRiskLevel(assessment.getRiskLevel());
                    userRepository.save(user);

                    return mapToResponseDto(savedAssessment);
                })
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    /**
     * Busca a avaliação mais recente de um usuário
     */
    @Transactional(readOnly = true)
    public Optional<RiskAssessmentResponseDto> findLatestByUser(Long userId) {
        return userRepository.findById(userId)
                .flatMap(user -> riskAssessmentRepository.findLatestByUser(user)
                        .map(this::mapToResponseDto));
    }

    /**
     * Busca avaliações de um usuário
     */
    @Transactional(readOnly = true)
    public List<RiskAssessmentResponseDto> findByUser(Long userId) {
        return userRepository.findById(userId)
                .map(user -> riskAssessmentRepository.findByUserOrderByAssessmentDateDesc(user)
                        .stream()
                        .map(this::mapToResponseDto)
                        .collect(java.util.stream.Collectors.toList()))
                .orElse(List.of());
    }

    /**
     * Busca avaliações por nível de risco
     */
    @Transactional(readOnly = true)
    public List<RiskAssessmentResponseDto> findByRiskLevel(RiskLevel riskLevel) {
        return riskAssessmentRepository.findByRiskLevelAndIsActive(riskLevel, true)
                .stream()
                .map(this::mapToResponseDto)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Busca avaliações de alto risco
     */
    @Transactional(readOnly = true)
    public List<RiskAssessmentResponseDto> findHighRiskAssessments() {
        return riskAssessmentRepository.findHighRiskAssessments()
                .stream()
                .map(this::mapToResponseDto)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Busca avaliações expiradas
     */
    @Transactional(readOnly = true)
    public List<RiskAssessmentResponseDto> findExpiredAssessments() {
        return riskAssessmentRepository.findExpiredAssessments()
                .stream()
                .map(this::mapToResponseDto)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Busca avaliações que precisam de renovação
     */
    @Transactional(readOnly = true)
    public List<RiskAssessmentResponseDto> findAssessmentsNeedingRenewal(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return riskAssessmentRepository.findAssessmentsNeedingRenewal(cutoffDate)
                .stream()
                .map(this::mapToResponseDto)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Busca avaliações com filtros
     */
    @Transactional(readOnly = true)
    public Page<RiskAssessmentResponseDto> findAssessmentsWithFilters(Long userId, RiskLevel riskLevel, 
                                                                     Boolean isAutomatic, String assessedBy, 
                                                                     Boolean isActive, Pageable pageable) {
        return riskAssessmentRepository.findAssessmentsWithFilters(userId, riskLevel, isAutomatic, assessedBy, isActive, pageable)
                .map(this::mapToResponseDto);
    }

    /**
     * Calcula estatísticas de avaliações por período
     */
    @Transactional(readOnly = true)
    public Object[] calculateAssessmentStatisticsByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return riskAssessmentRepository.calculateAssessmentStatisticsByPeriod(startDate, endDate);
    }

    /**
     * Conta avaliações por nível de risco
     */
    @Transactional(readOnly = true)
    public List<Object[]> countAssessmentsByRiskLevel() {
        return riskAssessmentRepository.countAssessmentsByRiskLevel();
    }

    /**
     * Busca usuários que precisam de nova avaliação
     */
    @Transactional(readOnly = true)
    public List<User> findUsersNeedingNewAssessment(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return riskAssessmentRepository.findUsersNeedingNewAssessment(cutoffDate);
    }

    /**
     * Desativa avaliações antigas de um usuário
     */
    public void deactivateOldAssessments(Long userId, Long currentAssessmentId) {
        userRepository.findById(userId)
                .ifPresent(user -> {
                    List<RiskAssessment> oldAssessments = riskAssessmentRepository.findByUserOrderByAssessmentDateDesc(user)
                            .stream()
                            .filter(assessment -> !assessment.getId().equals(currentAssessmentId))
                            .collect(java.util.stream.Collectors.toList());
                    
                    oldAssessments.forEach(RiskAssessment::deactivate);
                    riskAssessmentRepository.saveAll(oldAssessments);
                });
    }

    /**
     * Cria uma nova avaliação de risco
     */
    private RiskAssessment createRiskAssessment(User user, RiskAnalysisRequestDto analysisRequest) {
        RiskAssessment assessment = new RiskAssessment();
        assessment.setUser(user);
        assessment.setAssessmentDate(analysisRequest.getAnalysisDate() != null ? 
                analysisRequest.getAnalysisDate() : LocalDateTime.now());
        assessment.setIsAutomatic(true);
        assessment.setAssessedBy("SISTEMA");
        assessment.setIsActive(true);

        // Calcula indicadores e nível de risco
        calculateRiskIndicators(assessment, user);
        calculateRiskLevel(assessment);

        // Define período de validade (30 dias)
        assessment.setValidityPeriod(30);

        return assessment;
    }

    /**
     * Calcula indicadores de risco
     */
    private void calculateRiskIndicators(RiskAssessment assessment, User user) {
        // Indicadores comportamentais
        String behavioralIndicators = calculateBehavioralIndicators(user);
        assessment.setBehavioralIndicators(behavioralIndicators);

        // Indicadores financeiros
        String financialIndicators = calculateFinancialIndicators(user);
        assessment.setFinancialIndicators(financialIndicators);

        // Indicadores temporais
        String temporalIndicators = calculateTemporalIndicators(user);
        assessment.setTemporalIndicators(temporalIndicators);

        // Análise de sessões
        String sessionAnalysis = analyzeUserSessions(user);
        assessment.setSessionAnalysis(sessionAnalysis);
    }

    /**
     * Calcula indicadores comportamentais
     */
    private String calculateBehavioralIndicators(User user) {
        return String.format("{\"sessionCount\":%d,\"lastActivity\":\"%s\",\"isActive\":%s}",
                user.getSessionCount(),
                user.getLastActivity(),
                user.getIsActive());
    }

    /**
     * Calcula indicadores financeiros
     */
    private String calculateFinancialIndicators(User user) {
        return String.format("{\"totalDeposits\":%.2f,\"totalWithdrawals\":%.2f,\"netBalance\":%.2f}",
                user.getTotalDeposits(),
                user.getTotalWithdrawals(),
                user.getNetBalance());
    }

    /**
     * Calcula indicadores temporais
     */
    private String calculateTemporalIndicators(User user) {
        return String.format("{\"createdAt\":\"%s\",\"lastActivity\":\"%s\"}",
                user.getCreatedAt(),
                user.getLastActivity());
    }

    /**
     * Analisa sessões do usuário
     */
    private String analyzeUserSessions(User user) {
        Object[] sessionStats = gamblingSessionRepository.calculateUserSessionStatistics(user.getId());
        if (sessionStats != null && sessionStats.length > 0) {
            return String.format("{\"totalSessions\":%d,\"avgDuration\":%.2f,\"totalBets\":%.2f}",
                    sessionStats[0], sessionStats[1], sessionStats[2]);
        }
        return "{\"totalSessions\":0,\"avgDuration\":0,\"totalBets\":0}";
    }

    /**
     * Calcula o nível de risco baseado nos indicadores
     */
    private void calculateRiskLevel(RiskAssessment assessment) {
        double riskScore = 0.0;
        String reason = "";

        // Análise baseada nos indicadores
        if (assessment.getBehavioralIndicators() != null) {
            // Lógica simplificada para cálculo de risco
            riskScore += 20; // Base score
        }

        if (assessment.getFinancialIndicators() != null) {
            riskScore += 30; // Financial indicators
        }

        if (assessment.getSessionAnalysis() != null) {
            riskScore += 25; // Session analysis
        }

        assessment.setRiskScore(riskScore);

        // Define nível de risco baseado no score
        if (riskScore >= 80) {
            assessment.setRiskLevel(RiskLevel.CRITICAL);
            reason = "Múltiplos indicadores de risco crítico detectados";
        } else if (riskScore >= 60) {
            assessment.setRiskLevel(RiskLevel.HIGH);
            reason = "Indicadores de alto risco identificados";
        } else if (riskScore >= 40) {
            assessment.setRiskLevel(RiskLevel.MEDIUM);
            reason = "Alguns indicadores de risco moderado";
        } else {
            assessment.setRiskLevel(RiskLevel.LOW);
            reason = "Baixo risco identificado";
        }

        assessment.setAssessmentReason(reason);
        assessment.setRecommendations(generateRecommendations(assessment.getRiskLevel()));
    }

    /**
     * Gera recomendações baseadas no nível de risco
     */
    private String generateRecommendations(RiskLevel riskLevel) {
        return switch (riskLevel) {
            case CRITICAL -> "Intervenção imediata necessária. Contato com profissional especializado.";
            case HIGH -> "Monitoramento intensivo. Considerar intervenções preventivas.";
            case MEDIUM -> "Acompanhamento regular. Orientação sobre jogos responsáveis.";
            case LOW -> "Monitoramento padrão. Manter práticas saudáveis.";
        };
    }

    /**
     * Mapeia entidade RiskAssessment para DTO de resposta
     */
    private RiskAssessmentResponseDto mapToResponseDto(RiskAssessment assessment) {
        RiskAssessmentResponseDto dto = new RiskAssessmentResponseDto();
        dto.setId(assessment.getId());
        dto.setUserId(assessment.getUser().getId());
        dto.setUserName(assessment.getUser().getName());
        dto.setRiskLevel(assessment.getRiskLevel());
        dto.setRiskScore(assessment.getRiskScore());
        dto.setAssessmentReason(assessment.getAssessmentReason());
        dto.setBehavioralIndicators(assessment.getBehavioralIndicators());
        dto.setFinancialIndicators(assessment.getFinancialIndicators());
        dto.setTemporalIndicators(assessment.getTemporalIndicators());
        dto.setSessionAnalysis(assessment.getSessionAnalysis());
        dto.setRecommendations(assessment.getRecommendations());
        dto.setIsAutomatic(assessment.getIsAutomatic());
        dto.setAssessedBy(assessment.getAssessedBy());
        dto.setAssessmentDate(assessment.getAssessmentDate());
        dto.setValidUntil(assessment.getValidUntil());
        dto.setIsActive(assessment.getIsActive());
        dto.setCreatedAt(assessment.getCreatedAt());
        return dto;
    }
}