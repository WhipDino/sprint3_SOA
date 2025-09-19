package br.com.gambling.repository;

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
 * Repositório para operações de banco de dados relacionadas aos usuários
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca usuário por email
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica se existe usuário com o email informado
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuários por nível de risco
     */
    List<User> findByCurrentRiskLevel(RiskLevel riskLevel);

    /**
     * Busca usuários ativos por nível de risco
     */
    List<User> findByCurrentRiskLevelAndIsActive(RiskLevel riskLevel, Boolean isActive);

    /**
     * Busca usuários com atividade recente
     */
    @Query("SELECT u FROM User u WHERE u.lastActivity >= :since AND u.isActive = true")
    List<User> findActiveUsersWithRecentActivity(@Param("since") LocalDateTime since);

    /**
     * Busca usuários com alto volume de depósitos
     */
    @Query("SELECT u FROM User u WHERE u.totalDeposits >= :minAmount AND u.isActive = true")
    List<User> findUsersWithHighDeposits(@Param("minAmount") Double minAmount);

    /**
     * Busca usuários que precisam de reavaliação de risco
     */
    @Query("SELECT u FROM User u WHERE u.lastActivity >= :since AND u.isActive = true")
    List<User> findUsersNeedingRiskReassessment(@Param("since") LocalDateTime since);

    /**
     * Busca usuários com filtros
     */
    @Query("SELECT u FROM User u WHERE " +
           "(:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:riskLevel IS NULL OR u.currentRiskLevel = :riskLevel) AND " +
           "(:isActive IS NULL OR u.isActive = :isActive)")
    Page<User> findUsersWithFilters(@Param("name") String name, 
                                   @Param("riskLevel") RiskLevel riskLevel, 
                                   @Param("isActive") Boolean isActive, 
                                   Pageable pageable);

    /**
     * Obtém estatísticas dos usuários
     */
    @Query("SELECT COUNT(u), " +
           "COUNT(CASE WHEN u.isActive = true THEN 1 END), " +
           "COUNT(CASE WHEN u.currentRiskLevel = 'HIGH' OR u.currentRiskLevel = 'CRITICAL' THEN 1 END), " +
           "AVG(u.totalDeposits), " +
           "SUM(u.totalDeposits) " +
           "FROM User u")
    Object[] getUserStatistics();

    /**
     * Conta usuários por nível de risco
     */
    @Query("SELECT u.currentRiskLevel, COUNT(u) FROM User u WHERE u.isActive = true GROUP BY u.currentRiskLevel")
    List<Object[]> countUsersByRiskLevel();
}