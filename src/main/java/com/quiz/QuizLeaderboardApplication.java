package com.quiz;

import com.quiz.service.QuizOrchestrator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Entry point for the Quiz Leaderboard System.
 *
 * Runs as a CommandLineRunner so it executes the pipeline
 * automatically on startup and exits when done.
 */
@Slf4j
@SpringBootApplication
public class QuizLeaderboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizLeaderboardApplication.class, args);
    }

    /**
     * Kicks off the orchestrator immediately after Spring context loads.
     */
    @Bean
    public CommandLineRunner runner(QuizOrchestrator orchestrator) {
        return args -> {
            try {
                orchestrator.run();
            } catch (Exception e) {
                log.error("Fatal error during quiz pipeline: {}", e.getMessage(), e);
                System.exit(1);
            }
            System.exit(0);
        };
    }
}
