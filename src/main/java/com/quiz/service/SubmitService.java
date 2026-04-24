package com.quiz.service;

import com.quiz.model.LeaderboardEntry;
import com.quiz.model.SubmitRequest;
import com.quiz.model.SubmitResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handles submitting the final leaderboard to POST /quiz/submit.
 * Ensures submission happens exactly ONCE using an AtomicBoolean guard.
 */
@Slf4j
@Service
public class SubmitService {

    private final RestTemplate restTemplate;
    private final AtomicBoolean submitted = new AtomicBoolean(false);

    @Value("${quiz.reg-no}")
    private String regNo;

    @Value("${quiz.base-url}")
    private String baseUrl;

    public SubmitService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /** Submits the final leaderboard exactly once. */
    public SubmitResponse submit(List<LeaderboardEntry> leaderboard) {
        String normalizedRegNo = StringUtils.trimWhitespace(regNo);
        if (!StringUtils.hasText(normalizedRegNo)) {
            throw new IllegalStateException("quiz.reg-no is not set. Define QUIZ_REG_NO before submitting the leaderboard.");
        }

        if (!submitted.compareAndSet(false, true)) {
            log.warn("Submit skipped: already submitted once");
            return null;
        }

        String url = baseUrl + "/quiz/submit";
        SubmitRequest request = new SubmitRequest(normalizedRegNo, leaderboard);
        log.info("Submitting leaderboard: participants={}", leaderboard.size());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<SubmitRequest> entity = new HttpEntity<>(request, headers);
            ResponseEntity<SubmitResponse> responseEntity =
                    restTemplate.exchange(url, HttpMethod.POST, entity, SubmitResponse.class);

            SubmitResponse response = responseEntity.getBody();

            logSubmissionOutcome(response);
            return response;

        } catch (Exception e) {
            log.error("Submission failed: {}", e.getMessage());
            submitted.set(false);
            throw new RuntimeException("Submission failed: " + e.getMessage(), e);
        }
    }

    private void logSubmissionOutcome(SubmitResponse response) {
        if (response == null) {
            log.warn("Submission response body was empty");
            return;
        }

        if (hasCorrectnessPayload(response)) {
            log.info("Submission response: isCorrect={}, expectedTotal={}, submittedTotal={}, message={}",
                    response.isCorrect(), response.getExpectedTotal(), response.getSubmittedTotal(), response.getMessage());
        } else {
            log.info("Submission response summary: regNo={}, submittedTotal={}, totalPollsMade={}, attemptCount={}",
                    response.getRegNo(), response.getSubmittedTotal(), response.getTotalPollsMade(), response.getAttemptCount());
        }
    }

    private boolean hasCorrectnessPayload(SubmitResponse response) {
        return response.getExpectedTotal() > 0
                || StringUtils.hasText(response.getMessage())
                || response.isCorrect()
                || response.isIdempotent();
    }
}
