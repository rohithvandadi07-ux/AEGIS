package com.aegis.scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class OutboundScannerApplication {
    public static void main(String[] args) {
        SpringApplication.run(OutboundScannerApplication.class, args);
    }
}
