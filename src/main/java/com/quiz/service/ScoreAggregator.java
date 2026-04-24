package com.quiz.service;

import com.quiz.model.Event;
import com.quiz.model.LeaderboardEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Aggregates deduplicated events into a sorted leaderboard.
 *
 * Steps:
 * 1. Sum scores per participant using a Map
 * 2. Convert to LeaderboardEntry list
 * 3. Sort descending by totalScore
 */
@Slf4j
@Service
public class ScoreAggregator {

    public List<LeaderboardEntry> buildLeaderboard(List<Event> uniqueEvents) {
        Map<String, Integer> scoreMap = new HashMap<>();

        for (Event event : uniqueEvents) {
            scoreMap.merge(event.getParticipant(), event.getScore(), Integer::sum);
        }

        List<LeaderboardEntry> leaderboard = scoreMap.entrySet()
                .stream()
                .map(entry -> new LeaderboardEntry(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingInt(LeaderboardEntry::getTotalScore).reversed())
                .collect(Collectors.toList());

        log.info("Aggregation complete: participants={}, totalScore={}", leaderboard.size(), computeGrandTotal(leaderboard));

        return leaderboard;
    }

    /**
     * Computes combined total score across all participants (for verification).
     */
    public int computeGrandTotal(List<LeaderboardEntry> leaderboard) {
        return leaderboard.stream().mapToInt(LeaderboardEntry::getTotalScore).sum();
    }
}
