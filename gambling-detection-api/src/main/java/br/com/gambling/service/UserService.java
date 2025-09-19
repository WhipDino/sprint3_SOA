package br.com.gambling.service;

import br.com.gambling.dto.UserRequestDto;
import br.com.gambling.dto.UserResponseDto;
import br.com.gambling.entity.User;
import br.com.gambling.enums.RiskLevel;
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
 * Serviço para operações relacionadas aos usuários
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Cria um novo usuário
     */
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User user = new User();
        user.setName(userRequestDto.getName());
        user.setEmail(userRequestDto.getEmail());
        user.setPhoneNumber(userRequestDto.getPhoneNumber());
        user.setDateOfBirth(userRequestDto.getDateOfBirth());
        user.setCurrentRiskLevel(RiskLevel.LOW);
        user.setIsActive(true);
        user.setLastActivity(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return mapToResponseDto(savedUser);
    }

    /**
     * Busca um usuário por ID
     */
    @Transactional(readOnly = true)
    public Optional<UserResponseDto> findById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToResponseDto);
    }

    /**
     * Busca um usuário por email
     */
    @Transactional(readOnly = true)
    public Optional<UserResponseDto> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::mapToResponseDto);
    }

    /**
     * Atualiza um usuário existente
     */
    public Optional<UserResponseDto> updateUser(Long id, UserRequestDto userRequestDto) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(userRequestDto.getName());
                    user.setEmail(userRequestDto.getEmail());
                    user.setPhoneNumber(userRequestDto.getPhoneNumber());
                    user.setDateOfBirth(userRequestDto.getDateOfBirth());
                    user.setLastActivity(LocalDateTime.now());

                    User savedUser = userRepository.save(user);
                    return mapToResponseDto(savedUser);
                });
    }

    /**
     * Desativa um usuário
     */
    public boolean deactivateUser(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setIsActive(false);
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Ativa um usuário
     */
    public boolean activateUser(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setIsActive(true);
                    user.setLastActivity(LocalDateTime.now());
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Atualiza o nível de risco de um usuário
     */
    public boolean updateRiskLevel(Long id, RiskLevel riskLevel) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setCurrentRiskLevel(riskLevel);
                    user.setLastActivity(LocalDateTime.now());
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Adiciona um depósito ao usuário
     */
    public boolean addDeposit(Long id, Double amount) {
        return userRepository.findById(id)
                .map(user -> {
                    user.addDeposit(amount);
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Adiciona um saque ao usuário
     */
    public boolean addWithdrawal(Long id, Double amount) {
        return userRepository.findById(id)
                .map(user -> {
                    user.addWithdrawal(amount);
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Incrementa o contador de sessões do usuário
     */
    public boolean incrementSessionCount(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.incrementSessionCount();
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Verifica se um email já existe
     */
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    /**
     * Busca usuários com filtros
     */
    @Transactional(readOnly = true)
    public Page<UserResponseDto> findUsersWithFilters(String name, RiskLevel riskLevel, Boolean isActive, Pageable pageable) {
        return userRepository.findUsersWithFilters(name, riskLevel, isActive, pageable)
                .map(this::mapToResponseDto);
    }

    /**
     * Busca usuários por nível de risco
     */
    @Transactional(readOnly = true)
    public List<UserResponseDto> findByRiskLevel(RiskLevel riskLevel) {
        return userRepository.findByCurrentRiskLevel(riskLevel)
                .stream()
                .map(this::mapToResponseDto)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Busca usuários ativos com atividade recente
     */
    @Transactional(readOnly = true)
    public List<UserResponseDto> findActiveUsersWithRecentActivity(int hours) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(hours);
        return userRepository.findActiveUsersWithRecentActivity(cutoffTime)
                .stream()
                .map(this::mapToResponseDto)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Busca usuários com alto volume de depósitos
     */
    @Transactional(readOnly = true)
    public List<UserResponseDto> findUsersWithHighDeposits(Double minAmount) {
        return userRepository.findUsersWithHighDeposits(minAmount)
                .stream()
                .map(this::mapToResponseDto)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Busca usuários que precisam de reavaliação de risco
     */
    @Transactional(readOnly = true)
    public List<UserResponseDto> findUsersNeedingRiskReassessment(int hours) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(hours);
        return userRepository.findUsersNeedingRiskReassessment(cutoffTime)
                .stream()
                .map(this::mapToResponseDto)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Obtém estatísticas dos usuários
     */
    @Transactional(readOnly = true)
    public Object[] getUserStatistics() {
        return userRepository.getUserStatistics();
    }

    /**
     * Conta usuários por nível de risco
     */
    @Transactional(readOnly = true)
    public List<Object[]> countUsersByRiskLevel() {
        return userRepository.countUsersByRiskLevel();
    }

    /**
     * Mapeia entidade User para DTO de resposta
     */
    private UserResponseDto mapToResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setCurrentRiskLevel(user.getCurrentRiskLevel());
        dto.setTotalDeposits(user.getTotalDeposits());
        dto.setTotalWithdrawals(user.getTotalWithdrawals());
        dto.setNetBalance(user.getNetBalance());
        dto.setSessionCount(user.getSessionCount());
        dto.setLastActivity(user.getLastActivity());
        dto.setIsActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}