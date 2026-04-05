package com.airesume;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class AIResumeAnalyserApplication {

    public static void main(String[] args) {
        SpringApplication.run(AIResumeAnalyserApplication.class, args);
    }
}
