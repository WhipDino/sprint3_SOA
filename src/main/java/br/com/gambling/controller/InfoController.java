package br.com.gambling.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para informações da aplicação
 */
@RestController
@RequestMapping("/api/info")
@Tag(name = "Informações", description = "Informações sobre a aplicação")
public class InfoController {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.application.version:1.0.0}")
    private String applicationVersion;

    @GetMapping
    @Operation(summary = "Obter informações da aplicação", description = "Retorna informações básicas sobre a aplicação")
    @ApiResponse(responseCode = "200", description = "Informações retornadas com sucesso")
    public ResponseEntity<Map<String, Object>> getApplicationInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", applicationName);
        info.put("version", applicationVersion);
        info.put("description", "API para detecção e prevenção de apostas compulsivas");
        info.put("timestamp", LocalDateTime.now());
        info.put("status", "UP");
        info.put("environment", "development");
        
        Map<String, Object> features = new HashMap<>();
        features.put("userManagement", true);
        features.put("sessionTracking", true);
        features.put("riskAssessment", true);
        features.put("interventions", true);
        features.put("reporting", true);
        info.put("features", features);
        
        Map<String, Object> endpoints = new HashMap<>();
        endpoints.put("users", "/api/users");
        endpoints.put("sessions", "/api/sessions");
        endpoints.put("riskAssessments", "/api/risk-assessments");
        endpoints.put("interventions", "/api/interventions");
        endpoints.put("documentation", "/swagger-ui.html");
        info.put("endpoints", endpoints);
        
        return ResponseEntity.ok(info);
    }

    @GetMapping("/health")
    @Operation(summary = "Verificar saúde da aplicação", description = "Endpoint simples para verificar se a aplicação está funcionando")
    @ApiResponse(responseCode = "200", description = "Aplicação funcionando normalmente")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().toString());
        health.put("service", applicationName);
        return ResponseEntity.ok(health);
    }
}