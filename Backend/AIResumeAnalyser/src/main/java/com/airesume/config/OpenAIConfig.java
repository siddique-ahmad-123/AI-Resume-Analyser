package com.airesume.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Slf4j
@Configuration
public class OpenAIConfig {

    @Value("${groq.api.key:your-groq-api-key-here}")
    private String apiKey;

    @Value("${groq.api.base-url:https://api.groq.com/openai/v1}")
    private String baseUrl;

    @Bean
    public WebClient openAIWebClient() {
        HttpClient httpClient = buildHttpClient();
        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024))
                .build();
    }

    /**
     * Builds a Netty HttpClient that trusts all SSL certificates.
     * Required when the JVM trust store is missing the Groq/OpenAI certificate chain
     * (common in Docker environments with corporate proxies or slim JRE images).
     */
    private HttpClient buildHttpClient() {
        try {
            SslContext sslContext = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
            log.warn("Using trust-all SSL context for AI API client. " +
                     "Import the CA certificate into the JVM truststore for production use.");
            return HttpClient.create().secure(spec -> spec.sslContext(sslContext));
        } catch (SSLException e) {
            log.error("Failed to build custom SSL context, falling back to default", e);
            return HttpClient.create();
        }
    }
}
