package br.com.gambling.service;

import br.com.gambling.dto.GamblingSessionRequestDto;
import br.com.gambling.dto.GamblingSessionResponseDto;
import br.com.gambling.entity.GamblingSession;
import br.com.gambling.entity.User;
import br.com.gambling.repository.GamblingSessionRepository;
import br.com.gambling.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Serviço para operações relacionadas às sessões de apostas
 */
@Service
@Transactional
public class GamblingSessionService {

    @Autowired
    private GamblingSessionRepository gamblingSessionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Cria uma nova sessão de apostas
     */
    public Optional<GamblingSessionResponseDto> createSession(GamblingSessionRequestDto sessionRequestDto) {
        return userRepository.findById(sessionRequestDto.getUserId())
                .map(user -> {
                    GamblingSession session = new GamblingSession();
                    session.setUser(user);
                    session.setSessionStart(sessionRequestDto.getSessionStart());
                    session.setGameType(sessionRequestDto.getGameType());
                    session.setPlatform(sessionRequestDto.getPlatform());

                    GamblingSession savedSession = gamblingSessionRepository.save(session);
                    
                    // Incrementa contador de sessões do usuário
                    user.incrementSessionCount();
                    userRepository.save(user);

                    return mapToResponseDto(savedSession);
                });
    }

    /**
     * Busca uma sessão por ID
     */
    @Transactional(readOnly = true)
    public Optional<GamblingSessionResponseDto> findById(Long id) {
        return gamblingSessionRepository.findById(id)
                .map(this::mapToResponseDto);
    }

    /**
     * Finaliza uma sessão de apostas
     */
    public Optional<GamblingSessionResponseDto> endSession(Long id) {
        return gamblingSessionRepository.findById(id)
                .map(session -> {
                    session.endSession();
                    session.calculateNetResult();
                    
                    // Analisa indicadores de risco
                    analyzeRiskIndicators(session);
                    
                    GamblingSession savedSession = gamblingSessionRepository.save(session);
                    return mapToResponseDto(savedSession);
                });
    }

    /**
     * Adiciona uma aposta à sessão
     */
    public boolean addBet(Long id, Double amount) {
        return gamblingSessionRepository.findById(id)
                .map(session -> {
                    session.addBet(amount);
                    gamblingSessionRepository.save(session);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Adiciona um ganho à sessão
     */
    public boolean addWin(Long id, Double amount) {
        return gamblingSessionRepository.findById(id)
                .map(session -> {
                    session.addWin(amount);
                    gamblingSessionRepository.save(session);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Busca sessões de um usuário
     */
    @Transactional(readOnly = true)
    public List<GamblingSessionResponseDto> findByUser(Long userId) {
        return gamblingSessionRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Busca sessões ativas de um usuário
     */
    @Transactional(readOnly = true)
    public List<GamblingSessionResponseDto> findActiveSessionsByUser(Long userId) {
        return gamblingSessionRepository.findActiveSessionsByUser(userId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Busca sessões por período
     */
    @Transactional(readOnly = true)
    public List<GamblingSessionResponseDto> findSessionsByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return gamblingSessionRepository.findSessionsByPeriod(startDate, endDate)
                .stream()
                .map(this::mapToResponseDto)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Busca sessões de um usuário por período
     */
    @Transactional(readOnly = true)
    public List<GamblingSessionResponseDto> findUserSessionsByPeriod(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return userRepository.findById(userId)
                .map(user -> gamblingSessionRepository.findUserSessionsByPeriod(user, startDate, endDate)
                        .stream()
                        .map(this::mapToResponseDto)
                        .collect(java.util.stream.Collectors.toList()))
                .orElse(List.of());
    }

    /**
     * Busca sessões de alta frequência
     */
    @Transactional(readOnly = true)
    public List<GamblingSessionResponseDto> findHighFrequencySessions() {
        return gamblingSessionRepository.findHighFrequencySessions()
                .stream()
                .map(this::mapToResponseDto)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Busca sessões com perda chasing
     */
    @Transactional(readOnly = true)
    public List<GamblingSessionResponseDto> findLossChasingSessions() {
        return gamblingSessionRepository.findLossChasingSessions()
                .stream()
                .map(this::mapToResponseDto)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Busca sessões com perdas significativas
     */
    @Transactional(readOnly = true)
    public List<GamblingSessionResponseDto> findSessionsWithSignificantLosses(Double maxLoss) {
        return gamblingSessionRepository.findSessionsWithSignificantLosses(maxLoss)
                .stream()
                .map(this::mapToResponseDto)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Busca sessões com apostas altas
     */
    @Transactional(readOnly = true)
    public List<GamblingSessionResponseDto> findSessionsWithHighBets(Double minBetAmount) {
        return gamblingSessionRepository.findSessionsWithHighBets(minBetAmount)
                .stream()
                .map(this::mapToResponseDto)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Busca sessões longas
     */
    @Transactional(readOnly = true)
    public List<GamblingSessionResponseDto> findLongSessions(Long minDuration) {
        return gamblingSessionRepository.findLongSessions(minDuration)
                .stream()
                .map(this::mapToResponseDto)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Busca sessões com filtros
     */
    @Transactional(readOnly = true)
    public Page<GamblingSessionResponseDto> findSessionsWithFilters(Long userId, String gameType, String platform, 
                                                                   Boolean isHighFrequency, Boolean hasLossChasing, 
                                                                   Pageable pageable) {
        return gamblingSessionRepository.findSessionsWithFilters(userId, gameType, platform, isHighFrequency, hasLossChasing, pageable)
                .map(this::mapToResponseDto);
    }

    /**
     * Calcula estatísticas de sessões de um usuário
     */
    @Transactional(readOnly = true)
    public Object[] calculateUserSessionStatistics(Long userId) {
        return gamblingSessionRepository.calculateUserSessionStatistics(userId);
    }

    /**
     * Calcula estatísticas de sessões por período
     */
    @Transactional(readOnly = true)
    public Object[] calculateSessionStatisticsByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return gamblingSessionRepository.calculateSessionStatisticsByPeriod(startDate, endDate);
    }

    /**
     * Busca sessões que precisam de análise de risco
     */
    @Transactional(readOnly = true)
    public List<GamblingSessionResponseDto> findSessionsNeedingRiskAnalysis() {
        return gamblingSessionRepository.findSessionsNeedingRiskAnalysis()
                .stream()
                .map(this::mapToResponseDto)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Analisa indicadores de risco de uma sessão
     */
    private void analyzeRiskIndicators(GamblingSession session) {
        // Alta frequência: mais de 3 sessões por dia
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        long sessionCount = gamblingSessionRepository.countSessionsByUserAndPeriod(session.getUser().getId(), oneDayAgo, LocalDateTime.now());
        session.setIsHighFrequency(sessionCount > 3);

        // Perda chasing: sessão após perda significativa
        boolean hasRecentLoss = gamblingSessionRepository.hasRecentSignificantLoss(session.getUser().getId(), LocalDateTime.now().minusHours(24));
        session.setHasLossChasing(hasRecentLoss && session.getNetResult() < 0);

        // Gera indicadores de risco em JSON
        String riskIndicators = generateRiskIndicatorsJson(session);
        session.setRiskIndicators(riskIndicators);
    }

    /**
     * Gera JSON com indicadores de risco
     */
    private String generateRiskIndicatorsJson(GamblingSession session) {
        return String.format("{\"highFrequency\":%s,\"lossChasing\":%s,\"highBets\":%s,\"longSession\":%s}",
                session.getIsHighFrequency(),
                session.getHasLossChasing(),
                session.getMaxBetAmount() > 1000,
                session.getDurationMinutes() != null && session.getDurationMinutes() > 240);
    }

    /**
     * Mapeia entidade GamblingSession para DTO de resposta
     */
    private GamblingSessionResponseDto mapToResponseDto(GamblingSession session) {
        GamblingSessionResponseDto dto = new GamblingSessionResponseDto();
        dto.setId(session.getId());
        dto.setUserId(session.getUser().getId());
        dto.setUserName(session.getUser().getName());
        dto.setSessionStart(session.getSessionStart());
        dto.setSessionEnd(session.getSessionEnd());
        dto.setDurationMinutes(session.getDurationMinutes());
        dto.setTotalBetAmount(session.getTotalBetAmount());
        dto.setTotalWinAmount(session.getTotalWinAmount());
        dto.setNetResult(session.getNetResult());
        dto.setBetCount(session.getBetCount());
        dto.setMaxBetAmount(session.getMaxBetAmount());
        dto.setAverageBetAmount(session.getAverageBetAmount());
        dto.setGameType(session.getGameType());
        dto.setPlatform(session.getPlatform());
        dto.setIsHighFrequency(session.getIsHighFrequency());
        dto.setHasLossChasing(session.getHasLossChasing());
        dto.setRiskIndicators(session.getRiskIndicators());
        dto.setCreatedAt(session.getCreatedAt());
        return dto;
    }
}