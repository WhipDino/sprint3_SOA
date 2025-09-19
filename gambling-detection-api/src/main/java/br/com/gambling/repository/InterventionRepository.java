package br.com.gambling.repository;

import br.com.gambling.entity.Intervention;
import br.com.gambling.entity.User;
import br.com.gambling.enums.InterventionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório para operações de banco de dados relacionadas às intervenções
 */
@Repository
public interface InterventionRepository extends JpaRepository<Intervention, Long> {

    /**
     * Busca intervenções de um usuário específico
     */
    List<Intervention> findByUserOrderByCreatedAtDesc(User user);

    /**
     * Busca intervenções por tipo
     */
    List<Intervention> findByInterventionTypeOrderByCreatedAtDesc(InterventionType interventionType);

    /**
     * Busca intervenções por status
     */
    List<Intervention> findByStatusOrderByCreatedAtDesc(Intervention.InterventionStatus status);

    /**
     * Busca intervenções pendentes
     */
    @Query("SELECT i FROM Intervention i WHERE i.status = 'PENDING' ORDER BY i.priority DESC, i.createdAt ASC")
    List<Intervention> findPendingInterventions();

    /**
     * Busca intervenções agendadas
     */
    @Query("SELECT i FROM Intervention i WHERE i.status = 'SCHEDULED' AND i.scheduledFor <= :now ORDER BY i.scheduledFor ASC")
    List<Intervention> findScheduledInterventionsToExecute(@Param("now") LocalDateTime now);

    /**
     * Busca intervenções expiradas
     */
    @Query("SELECT i FROM Intervention i WHERE i.expiresAt IS NOT NULL AND i.expiresAt < :now AND i.status IN ('PENDING', 'SCHEDULED')")
    List<Intervention> findExpiredInterventions(@Param("now") LocalDateTime now);

    /**
     * Busca intervenções de alta prioridade
     */
    @Query("SELECT i FROM Intervention i WHERE i.priority >= :minPriority AND i.status IN ('PENDING', 'SCHEDULED') ORDER BY i.priority DESC, i.createdAt ASC")
    List<Intervention> findHighPriorityInterventions(@Param("minPriority") Integer minPriority);

    /**
     * Busca intervenções por período
     */
    @Query("SELECT i FROM Intervention i WHERE i.createdAt >= :startDate AND i.createdAt <= :endDate ORDER BY i.createdAt DESC")
    List<Intervention> findInterventionsByPeriod(@Param("startDate") LocalDateTime startDate, 
                                                @Param("endDate") LocalDateTime endDate);

    /**
     * Busca intervenções executadas por período
     */
    @Query("SELECT i FROM Intervention i WHERE i.status = 'EXECUTED' AND i.executedAt >= :startDate AND i.executedAt <= :endDate ORDER BY i.executedAt DESC")
    List<Intervention> findExecutedInterventionsByPeriod(@Param("startDate") LocalDateTime startDate, 
                                                        @Param("endDate") LocalDateTime endDate);

    /**
     * Busca intervenções que precisam de acompanhamento
     */
    @Query("SELECT i FROM Intervention i WHERE i.status = 'EXECUTED' AND i.executedAt <= :cutoffDate AND i.effectivenessScore IS NULL ORDER BY i.executedAt ASC")
    List<Intervention> findInterventionsNeedingFollowUp(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Busca intervenções com baixa efetividade
     */
    @Query("SELECT i FROM Intervention i WHERE i.effectivenessScore IS NOT NULL AND i.effectivenessScore <= :maxScore ORDER BY i.effectivenessScore ASC")
    List<Intervention> findInterventionsWithLowEffectiveness(@Param("maxScore") Integer maxScore);

    /**
     * Busca intervenções com filtros
     */
    @Query("SELECT i FROM Intervention i WHERE " +
           "(:userId IS NULL OR i.user.id = :userId) AND " +
           "(:interventionType IS NULL OR i.interventionType = :interventionType) AND " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:isAutomatic IS NULL OR i.isAutomatic = :isAutomatic) AND " +
           "(:createdBy IS NULL OR LOWER(i.createdBy) LIKE LOWER(CONCAT('%', :createdBy, '%')))")
    Page<Intervention> findInterventionsWithFilters(@Param("userId") Long userId,
                                                   @Param("interventionType") InterventionType interventionType,
                                                   @Param("status") Intervention.InterventionStatus status,
                                                   @Param("isAutomatic") Boolean isAutomatic,
                                                   @Param("createdBy") String createdBy,
                                                   Pageable pageable);

    /**
     * Calcula estatísticas de intervenções por período
     */
    @Query("SELECT COUNT(i), " +
           "COUNT(CASE WHEN i.status = 'EXECUTED' THEN 1 END), " +
           "COUNT(CASE WHEN i.status = 'PENDING' THEN 1 END), " +
           "COUNT(CASE WHEN i.status = 'CANCELLED' THEN 1 END), " +
           "AVG(i.effectivenessScore) " +
           "FROM Intervention i WHERE i.createdAt >= :startDate AND i.createdAt <= :endDate")
    Object[] calculateInterventionStatisticsByPeriod(@Param("startDate") LocalDateTime startDate, 
                                                    @Param("endDate") LocalDateTime endDate);

    /**
     * Conta intervenções por tipo
     */
    @Query("SELECT i.interventionType, COUNT(i) FROM Intervention i GROUP BY i.interventionType")
    List<Object[]> countInterventionsByType();

    /**
     * Conta intervenções por status
     */
    @Query("SELECT i.status, COUNT(i) FROM Intervention i GROUP BY i.status")
    List<Object[]> countInterventionsByStatus();

    /**
     * Calcula efetividade por tipo
     */
    @Query("SELECT i.interventionType, AVG(i.effectivenessScore) FROM Intervention i WHERE i.effectivenessScore IS NOT NULL GROUP BY i.interventionType")
    List<Object[]> calculateEffectivenessByType();

    /**
     * Verifica se usuário tem intervenções pendentes
     */
    @Query("SELECT COUNT(i) > 0 FROM Intervention i WHERE i.user = :user AND i.status IN ('PENDING', 'SCHEDULED')")
    boolean hasPendingInterventions(@Param("user") User user);

    /**
     * Busca intervenções expiradas (método padrão)
     */
    default List<Intervention> findExpiredInterventions() {
        return findExpiredInterventions(LocalDateTime.now());
    }

    /**
     * Busca intervenções agendadas para execução (método padrão)
     */
    default List<Intervention> findScheduledInterventionsToExecute() {
        return findScheduledInterventionsToExecute(LocalDateTime.now());
    }
}