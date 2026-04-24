package com.quiz.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents the full response from GET /quiz/messages
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PollResponse {

    private String regNo;
    private String setId;
    private int pollIndex;
    private List<Event> events;

    @Override
    public String toString() {
        return String.format("PollResponse{regNo='%s', setId='%s', pollIndex=%d, eventCount=%d}",
                regNo, setId, pollIndex, events != null ? events.size() : 0);
    }
}
