package com.quiz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request payload for POST /quiz/submit
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitRequest {

    private String regNo;
    private List<LeaderboardEntry> leaderboard;
}
