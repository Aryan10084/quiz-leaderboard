package com.quiz.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response from POST /quiz/submit
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubmitResponse {

    private boolean isCorrect;
    private boolean isIdempotent;
    private int submittedTotal;
    private int expectedTotal;
    private String message;

    // Alternate response shape observed from validator for some registrations.
    private String regNo;
    private Integer totalPollsMade;
    private Integer attemptCount;

    @Override
    public String toString() {
        return String.format(
            "SubmitResponse{isCorrect=%b, isIdempotent=%b, submittedTotal=%d, expectedTotal=%d, message='%s', regNo='%s', totalPollsMade=%s, attemptCount=%s}",
            isCorrect, isIdempotent, submittedTotal, expectedTotal, message, regNo, totalPollsMade, attemptCount);
    }
}
