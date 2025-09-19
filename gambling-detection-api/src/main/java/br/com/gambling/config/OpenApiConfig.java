package br.com.gambling.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração do OpenAPI/Swagger para documentação da API.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(servers())
                .components(components())
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"));
    }

    private Info apiInfo() {
        return new Info()
                .title("Gambling Detection API")
                .description("""
                        API para detecção e prevenção de apostas compulsivas.
                        
                        Esta API oferece funcionalidades para:
                        - Gerenciamento de usuários
                        - Monitoramento de sessões de apostas
                        - Avaliação de risco comportamental
                        - Intervenções preventivas e corretivas
                        - Análise de padrões de jogo
                        - Relatórios e estatísticas
                        
                        ## Funcionalidades Principais
                        
                        ### Usuários
                        - Cadastro e gerenciamento de usuários
                        - Controle de níveis de risco
                        - Histórico de atividades
                        
                        ### Sessões de Apostas
                        - Registro de sessões de jogo
                        - Análise de padrões comportamentais
                        - Detecção de sinais de risco
                        
                        ### Avaliação de Risco
                        - Análise automática de risco
                        - Classificação por níveis (LOW, MEDIUM, HIGH, CRITICAL)
                        - Recomendações personalizadas
                        
                        ### Intervenções
                        - Alertas preventivos
                        - Sugestões de alternativas
                        - Bloqueios temporários
                        - Encaminhamentos profissionais
                        - Grupos de apoio
                        
                        ## Segurança
                        
                        A API utiliza autenticação HTTP Basic para proteção dos endpoints.
                        Em ambiente de produção, recomenda-se implementar autenticação mais robusta.
                        
                        ## Limitações
                        
                        - Esta é uma versão de desenvolvimento
                        - Alguns endpoints podem ter limitações de performance
                        - Dados de exemplo são fornecidos para demonstração
                        """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("Equipe de Desenvolvimento")
                        .email("dev@gambling-detection.com")
                        .url("https://gambling-detection.com"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    private List<Server> servers() {
        return List.of(
                new Server()
                        .url("http://localhost:8080")
                        .description("Servidor de Desenvolvimento"),
                new Server()
                        .url("https://api.gambling-detection.com")
                        .description("Servidor de Produção")
        );
    }

    private Components components() {
        return new Components()
                .addSecuritySchemes("basicAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("basic")
                        .description("Autenticação HTTP Basic")
                        .name("basicAuth"));
    }
}
