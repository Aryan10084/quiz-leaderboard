package com.quiz.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single scored event from the quiz API.
 * Key fields used for deduplication: roundId + participant
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {

    private String roundId;
    private String participant;
    private int score;

    /**
     * Composite deduplication key.
     * Two events are considered duplicates if they share the same roundId AND participant.
     */
    public String getDeduplicationKey() {
        return roundId + "|" + participant;
    }

    @Override
    public String toString() {
        return String.format("Event{roundId='%s', participant='%s', score=%d}", roundId, participant, score);
    }
}
