package br.com.gambling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicação principal para detecção e prevenção de apostas compulsivas.
 * 
 * Esta API oferece serviços para:
 * - Detecção de padrões comportamentais de risco
 * - Classificação de usuários por níveis de risco
 * - Intervenções em tempo real
 * - Conexão com rede de suporte
 * 
 * @author Sistema de Detecção de Apostas Compulsivas
 * @version 1.0.0
 */
@SpringBootApplication
public class GamblingDetectionApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(GamblingDetectionApiApplication.class, args);
    }
}
