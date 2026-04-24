package com.quiz.service;

import com.quiz.model.Event;
import com.quiz.model.LeaderboardEntry;
import com.quiz.model.SubmitResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class QuizOrchestrator {

    private final QuizPollerService pollerService;
    private final EventDeduplicator deduplicator;
    private final ScoreAggregator aggregator;
    private final SubmitService submitService;

    public QuizOrchestrator(
            QuizPollerService pollerService,
            EventDeduplicator deduplicator,
            ScoreAggregator aggregator,
            SubmitService submitService) {
        this.pollerService = pollerService;
        this.deduplicator = deduplicator;
        this.aggregator = aggregator;
        this.submitService = submitService;
    }

    public void run() {
        log.info("Quiz pipeline started");

        List<Event> rawEvents = pollerService.pollAllRounds();
        if (rawEvents.isEmpty()) {
            log.error("No events received from any poll. Aborting.");
            return;
        }

        List<Event> uniqueEvents = deduplicator.deduplicate(rawEvents);
        List<LeaderboardEntry> leaderboard = aggregator.buildLeaderboard(uniqueEvents);
        SubmitResponse result = submitService.submit(leaderboard);

        log.info("Final leaderboard:");
        for (int i = 0; i < leaderboard.size(); i++) {
            LeaderboardEntry entry = leaderboard.get(i);
            log.info("{}. {} ({})", i + 1, entry.getParticipant(), entry.getTotalScore());
        }

        if (result == null) {
            log.warn("Submission result is empty");
        } else if (result.getExpectedTotal() > 0 || result.isCorrect() || result.isIdempotent()) {
            log.info("Result: {}", result.isCorrect() ? "CORRECT" : "INCORRECT");
            log.info("Message: {}", result.getMessage());
        } else {
            log.info("Result: SUBMITTED (validator summary mode)");
        }

        log.info("Quiz pipeline finished");
    }
}
