package br.com.gambling.repository;

import br.com.gambling.entity.RiskAssessment;
import br.com.gambling.entity.User;
import br.com.gambling.enums.RiskLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de banco de dados relacionadas às avaliações de risco
 */
@Repository
public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, Long> {

    /**
     * Busca a avaliação de risco mais recente de um usuário
     */
    @Query("SELECT ra FROM RiskAssessment ra WHERE ra.user = :user AND ra.isActive = true ORDER BY ra.assessmentDate DESC")
    Optional<RiskAssessment> findLatestByUser(@Param("user") User user);

    /**
     * Busca todas as avaliações de um usuário ordenadas por data
     */
    List<RiskAssessment> findByUserOrderByAssessmentDateDesc(User user);

    /**
     * Busca avaliações por nível de risco
     */
    List<RiskAssessment> findByRiskLevelAndIsActive(RiskLevel riskLevel, Boolean isActive);

    /**
     * Busca avaliações automáticas
     */
    List<RiskAssessment> findByIsAutomaticAndIsActive(Boolean isAutomatic, Boolean isActive);

    /**
     * Busca avaliações por período
     */
    @Query("SELECT ra FROM RiskAssessment ra WHERE ra.assessmentDate >= :startDate AND ra.assessmentDate <= :endDate")
    List<RiskAssessment> findAssessmentsByPeriod(@Param("startDate") LocalDateTime startDate, 
                                                @Param("endDate") LocalDateTime endDate);

    /**
     * Busca avaliações de alto risco
     */
    @Query("SELECT ra FROM RiskAssessment ra WHERE (ra.riskLevel = 'HIGH' OR ra.riskLevel = 'CRITICAL') AND ra.isActive = true ORDER BY ra.assessmentDate DESC")
    List<RiskAssessment> findHighRiskAssessments();

    /**
     * Busca avaliações expiradas
     */
    @Query("SELECT ra FROM RiskAssessment ra WHERE ra.validUntil IS NOT NULL AND ra.validUntil < :now AND ra.isActive = true")
    List<RiskAssessment> findExpiredAssessments(@Param("now") LocalDateTime now);

    /**
     * Busca avaliações que precisam de renovação
     */
    @Query("SELECT ra FROM RiskAssessment ra WHERE ra.validUntil IS NOT NULL AND ra.validUntil <= :cutoffDate AND ra.isActive = true")
    List<RiskAssessment> findAssessmentsNeedingRenewal(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Busca avaliações com filtros
     */
    @Query("SELECT ra FROM RiskAssessment ra WHERE " +
           "(:userId IS NULL OR ra.user.id = :userId) AND " +
           "(:riskLevel IS NULL OR ra.riskLevel = :riskLevel) AND " +
           "(:isAutomatic IS NULL OR ra.isAutomatic = :isAutomatic) AND " +
           "(:assessedBy IS NULL OR LOWER(ra.assessedBy) LIKE LOWER(CONCAT('%', :assessedBy, '%'))) AND " +
           "(:isActive IS NULL OR ra.isActive = :isActive)")
    Page<RiskAssessment> findAssessmentsWithFilters(@Param("userId") Long userId,
                                                   @Param("riskLevel") RiskLevel riskLevel,
                                                   @Param("isAutomatic") Boolean isAutomatic,
                                                   @Param("assessedBy") String assessedBy,
                                                   @Param("isActive") Boolean isActive,
                                                   Pageable pageable);

    /**
     * Calcula estatísticas de avaliações por período
     */
    @Query("SELECT COUNT(ra), " +
           "COUNT(CASE WHEN ra.riskLevel = 'LOW' THEN 1 END), " +
           "COUNT(CASE WHEN ra.riskLevel = 'MEDIUM' THEN 1 END), " +
           "COUNT(CASE WHEN ra.riskLevel = 'HIGH' THEN 1 END), " +
           "COUNT(CASE WHEN ra.riskLevel = 'CRITICAL' THEN 1 END), " +
           "AVG(ra.riskScore) " +
           "FROM RiskAssessment ra WHERE ra.assessmentDate >= :startDate AND ra.assessmentDate <= :endDate")
    Object[] calculateAssessmentStatisticsByPeriod(@Param("startDate") LocalDateTime startDate, 
                                                 @Param("endDate") LocalDateTime endDate);

    /**
     * Conta avaliações por nível de risco
     */
    @Query("SELECT ra.riskLevel, COUNT(ra) FROM RiskAssessment ra WHERE ra.isActive = true GROUP BY ra.riskLevel")
    List<Object[]> countAssessmentsByRiskLevel();

    /**
     * Busca usuários que precisam de nova avaliação
     */
    @Query("SELECT DISTINCT u FROM User u WHERE u.isActive = true AND " +
           "NOT EXISTS (SELECT ra FROM RiskAssessment ra WHERE ra.user = u AND ra.assessmentDate >= :cutoffDate AND ra.isActive = true)")
    List<User> findUsersNeedingNewAssessment(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Busca avaliações expiradas (método padrão)
     */
    default List<RiskAssessment> findExpiredAssessments() {
        return findExpiredAssessments(LocalDateTime.now());
    }
}