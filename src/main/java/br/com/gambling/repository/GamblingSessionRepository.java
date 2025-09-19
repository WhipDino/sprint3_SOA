package br.com.gambling.repository;

import br.com.gambling.entity.GamblingSession;
import br.com.gambling.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório para operações de banco de dados relacionadas às sessões de apostas
 */
@Repository
public interface GamblingSessionRepository extends JpaRepository<GamblingSession, Long> {

    /**
     * Busca sessões de um usuário
     */
    List<GamblingSession> findByUserOrderBySessionStartDesc(User user);

    /**
     * Busca sessões ativas
     */
    @Query("SELECT gs FROM GamblingSession gs WHERE gs.sessionEnd IS NULL ORDER BY gs.sessionStart DESC")
    List<GamblingSession> findActiveSessions();

    /**
     * Busca sessões por período
     */
    @Query("SELECT gs FROM GamblingSession gs WHERE gs.sessionStart >= :startDate AND gs.sessionStart <= :endDate ORDER BY gs.sessionStart DESC")
    List<GamblingSession> findSessionsByPeriod(@Param("startDate") LocalDateTime startDate, 
                                              @Param("endDate") LocalDateTime endDate);

    /**
     * Busca sessões de um usuário por período
     */
    @Query("SELECT gs FROM GamblingSession gs WHERE gs.user = :user AND gs.sessionStart >= :startDate AND gs.sessionStart <= :endDate ORDER BY gs.sessionStart DESC")
    List<GamblingSession> findUserSessionsByPeriod(@Param("user") User user, 
                                                  @Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate);

    /**
     * Busca sessões por ID do usuário
     */
    @Query("SELECT gs FROM GamblingSession gs WHERE gs.user.id = :userId ORDER BY gs.sessionStart DESC")
    List<GamblingSession> findByUserIdOrderBySessionStartDesc(@Param("userId") Long userId);

    /**
     * Busca sessões de alta frequência
     */
    @Query("SELECT gs FROM GamblingSession gs WHERE gs.isHighFrequency = true ORDER BY gs.sessionStart DESC")
    List<GamblingSession> findHighFrequencySessions();

    /**
     * Busca sessões com perseguição de perdas
     */
    @Query("SELECT gs FROM GamblingSession gs WHERE gs.hasLossChasing = true ORDER BY gs.sessionStart DESC")
    List<GamblingSession> findLossChasingSessions();

    /**
     * Busca sessões por tipo de jogo
     */
    List<GamblingSession> findByGameTypeOrderBySessionStartDesc(String gameType);

    /**
     * Busca sessões por plataforma
     */
    List<GamblingSession> findByPlatformOrderBySessionStartDesc(String platform);

    /**
     * Busca sessões com filtros
     */
    @Query("SELECT gs FROM GamblingSession gs WHERE " +
           "(:userId IS NULL OR gs.user.id = :userId) AND " +
           "(:gameType IS NULL OR gs.gameType = :gameType) AND " +
           "(:platform IS NULL OR gs.platform = :platform) AND " +
           "(:isHighFrequency IS NULL OR gs.isHighFrequency = :isHighFrequency) AND " +
           "(:hasLossChasing IS NULL OR gs.hasLossChasing = :hasLossChasing)")
    Page<GamblingSession> findSessionsWithFilters(@Param("userId") Long userId,
                                                  @Param("gameType") String gameType,
                                                  @Param("platform") String platform,
                                                  @Param("isHighFrequency") Boolean isHighFrequency,
                                                  @Param("hasLossChasing") Boolean hasLossChasing,
                                                  Pageable pageable);

    /**
     * Calcula estatísticas de sessões por período
     */
    @Query("SELECT COUNT(gs), " +
           "AVG(gs.durationMinutes), " +
           "SUM(gs.totalBetAmount), " +
           "SUM(gs.totalWinAmount), " +
           "AVG(gs.netResult) " +
           "FROM GamblingSession gs WHERE gs.sessionStart >= :startDate AND gs.sessionStart <= :endDate")
    Object[] calculateSessionStatisticsByPeriod(@Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);

    /**
     * Calcula estatísticas de sessões de um usuário
     */
    @Query("SELECT COUNT(gs), " +
           "AVG(gs.durationMinutes), " +
           "SUM(gs.totalBetAmount) " +
           "FROM GamblingSession gs WHERE gs.user.id = :userId")
    Object[] calculateUserSessionStatistics(@Param("userId") Long userId);

    /**
     * Conta sessões por tipo de jogo
     */
    @Query("SELECT gs.gameType, COUNT(gs) FROM GamblingSession gs GROUP BY gs.gameType")
    List<Object[]> countSessionsByGameType();

    /**
     * Conta sessões por plataforma
     */
    @Query("SELECT gs.platform, COUNT(gs) FROM GamblingSession gs GROUP BY gs.platform")
    List<Object[]> countSessionsByPlatform();

    /**
     * Busca sessões que precisam de análise de risco
     */
    @Query("SELECT gs FROM GamblingSession gs WHERE gs.sessionEnd IS NOT NULL AND gs.riskIndicators IS NULL ORDER BY gs.sessionEnd DESC")
    List<GamblingSession> findSessionsNeedingRiskAnalysis();

    /**
     * Busca sessões com alto valor de apostas
     */
    @Query("SELECT gs FROM GamblingSession gs WHERE gs.totalBetAmount >= :minAmount ORDER BY gs.totalBetAmount DESC")
    List<GamblingSession> findHighValueSessions(@Param("minAmount") Double minAmount);

    /**
     * Busca sessões com perdas significativas
     */
    @Query("SELECT gs FROM GamblingSession gs WHERE gs.netResult < :maxLoss ORDER BY gs.netResult ASC")
    List<GamblingSession> findSignificantLossSessions(@Param("maxLoss") Double maxLoss);

    /**
     * Busca sessões por ID do usuário (método alternativo)
     */
    default List<GamblingSession> findByUserId(Long userId) {
        return findByUserIdOrderBySessionStartDesc(userId);
    }

    /**
     * Busca sessões ativas de um usuário
     */
    @Query("SELECT gs FROM GamblingSession gs WHERE gs.user.id = :userId AND gs.sessionEnd IS NULL ORDER BY gs.sessionStart DESC")
    List<GamblingSession> findActiveSessionsByUser(@Param("userId") Long userId);

    /**
     * Busca sessões com perdas significativas (método alternativo)
     */
    default List<GamblingSession> findSessionsWithSignificantLosses(Double maxLoss) {
        return findSignificantLossSessions(maxLoss);
    }

    /**
     * Busca sessões com apostas altas (método alternativo)
     */
    default List<GamblingSession> findSessionsWithHighBets(Double minAmount) {
        return findHighValueSessions(minAmount);
    }

    /**
     * Busca sessões longas
     */
    @Query("SELECT gs FROM GamblingSession gs WHERE gs.durationMinutes >= :minDuration ORDER BY gs.durationMinutes DESC")
    List<GamblingSession> findLongSessions(@Param("minDuration") Long minDuration);

    /**
     * Conta sessões de um usuário por período
     */
    @Query("SELECT COUNT(gs) FROM GamblingSession gs WHERE gs.user.id = :userId AND gs.sessionStart >= :startDate AND gs.sessionStart <= :endDate")
    Long countSessionsByUserAndPeriod(@Param("userId") Long userId, 
                                     @Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);

    /**
     * Verifica se usuário teve perda significativa recente
     */
    @Query("SELECT COUNT(gs) > 0 FROM GamblingSession gs WHERE gs.user.id = :userId AND gs.sessionEnd >= :since AND gs.netResult < -100")
    boolean hasRecentSignificantLoss(@Param("userId") Long userId, @Param("since") LocalDateTime since);
}