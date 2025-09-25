package com.ancora.customerbookshelf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // Habilita o suporte do Spring para execução de métodos assíncronos em background.
@EnableRetry // Habilita o suporte do Spring para a funcionalidade de retentativas automáticas (@Retryable).
@EnableCaching // Habilita o suporte a cache do Spring.
public class CustomerBookshelfApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerBookshelfApplication.class, args);
    }

}
