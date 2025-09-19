package br.com.gambling.controller;

import br.com.gambling.dto.UserRequestDto;
import br.com.gambling.dto.UserResponseDto;
import br.com.gambling.enums.RiskLevel;
import br.com.gambling.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para operações relacionadas aos usuários
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuários", description = "Operações relacionadas aos usuários do sistema")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    @Operation(summary = "Criar novo usuário", description = "Cria um novo usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Email já existe")
    })
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequest) {
        if (userService.emailExists(userRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        UserResponseDto createdUser = userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna os dados de um usuário específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        Optional<UserResponseDto> user = userService.findById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar usuário por email", description = "Retorna os dados de um usuário pelo email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email) {
        Optional<UserResponseDto> user = userService.findByEmail(email);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, 
                                                     @Valid @RequestBody UserRequestDto userRequest) {
        Optional<UserResponseDto> updatedUser = userService.updateUser(id, userRequest);
        return updatedUser.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Desativar usuário", description = "Desativa um usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário desativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        boolean deactivated = userService.deactivateUser(id);
        return deactivated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Ativar usuário", description = "Ativa um usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário ativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        boolean activated = userService.activateUser(id);
        return activated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/risk-level")
    @Operation(summary = "Atualizar nível de risco", description = "Atualiza o nível de risco de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nível de risco atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> updateRiskLevel(@PathVariable Long id, 
                                               @RequestParam RiskLevel riskLevel) {
        boolean updated = userService.updateRiskLevel(id, riskLevel);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/deposits")
    @Operation(summary = "Adicionar depósito", description = "Adiciona um depósito ao histórico do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Depósito adicionado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> addDeposit(@PathVariable Long id, 
                                          @RequestParam Double amount) {
        boolean added = userService.addDeposit(id, amount);
        return added ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/withdrawals")
    @Operation(summary = "Adicionar saque", description = "Adiciona um saque ao histórico do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saque adicionado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> addWithdrawal(@PathVariable Long id, 
                                             @RequestParam Double amount) {
        boolean added = userService.addWithdrawal(id, amount);
        return added ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/increment-sessions")
    @Operation(summary = "Incrementar contador de sessões", description = "Incrementa o contador de sessões do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contador incrementado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> incrementSessionCount(@PathVariable Long id) {
        boolean incremented = userService.incrementSessionCount(id);
        return incremented ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @GetMapping
    @Operation(summary = "Listar usuários com filtros", description = "Retorna uma lista paginada de usuários com filtros opcionais")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    })
    public ResponseEntity<Page<UserResponseDto>> getUsers(
            @Parameter(description = "Nome do usuário para filtrar") @RequestParam(required = false) String name,
            @Parameter(description = "Nível de risco para filtrar") @RequestParam(required = false) RiskLevel riskLevel,
            @Parameter(description = "Status ativo/inativo") @RequestParam(required = false) Boolean isActive,
            Pageable pageable) {
        
        Page<UserResponseDto> users = userService.findUsersWithFilters(name, riskLevel, isActive, pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/risk-level/{riskLevel}")
    @Operation(summary = "Listar usuários por nível de risco", description = "Retorna usuários com um nível de risco específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    })
    public ResponseEntity<List<UserResponseDto>> getUsersByRiskLevel(@PathVariable RiskLevel riskLevel) {
        List<UserResponseDto> users = userService.findByRiskLevel(riskLevel);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/recent-activity")
    @Operation(summary = "Listar usuários com atividade recente", description = "Retorna usuários com atividade nas últimas horas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    })
    public ResponseEntity<List<UserResponseDto>> getUsersWithRecentActivity(
            @Parameter(description = "Número de horas para considerar atividade recente") @RequestParam(defaultValue = "24") int hours) {
        
        List<UserResponseDto> users = userService.findActiveUsersWithRecentActivity(hours);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/high-deposits")
    @Operation(summary = "Listar usuários com alto volume de depósitos", description = "Retorna usuários com volume de depósitos acima do valor mínimo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    })
    public ResponseEntity<List<UserResponseDto>> getUsersWithHighDeposits(
            @Parameter(description = "Valor mínimo de depósitos") @RequestParam(defaultValue = "1000.0") Double minAmount) {
        
        List<UserResponseDto> users = userService.findUsersWithHighDeposits(minAmount);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/needing-risk-reassessment")
    @Operation(summary = "Listar usuários que precisam de reavaliação", description = "Retorna usuários que precisam de nova avaliação de risco")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    })
    public ResponseEntity<List<UserResponseDto>> getUsersNeedingRiskReassessment(
            @Parameter(description = "Número de horas para considerar necessidade de reavaliação") @RequestParam(defaultValue = "24") int hours) {
        
        List<UserResponseDto> users = userService.findUsersNeedingRiskReassessment(hours);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Obter estatísticas de usuários", description = "Retorna estatísticas gerais dos usuários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso")
    })
    public ResponseEntity<Object[]> getUserStatistics() {
        Object[] statistics = userService.getUserStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/risk-level-counts")
    @Operation(summary = "Contar usuários por nível de risco", description = "Retorna contagem de usuários por nível de risco")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contagens retornadas com sucesso")
    })
    public ResponseEntity<List<Object[]>> countUsersByRiskLevel() {
        List<Object[]> counts = userService.countUsersByRiskLevel();
        return ResponseEntity.ok(counts);
    }
}