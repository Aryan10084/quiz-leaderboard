package com.quiz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A single entry in the final leaderboard submitted to POST /quiz/submit
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntry {

    private String participant;
    private int totalScore;

    @Override
    public String toString() {
        return String.format("%-20s %d pts", participant, totalScore);
    }
}
