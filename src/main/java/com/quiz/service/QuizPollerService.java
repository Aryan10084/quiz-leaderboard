package com.quiz.service;

import com.quiz.model.Event;
import com.quiz.model.PollResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Responsible for polling the quiz API 10 times (poll=0 through poll=9)
 * with a mandatory 5-second delay between each call.
 */
@Slf4j
@Service
public class QuizPollerService {

    private final RestTemplate restTemplate;

    @Value("${quiz.reg-no}")
    private String regNo;

    @Value("${quiz.base-url}")
    private String baseUrl;

    @Value("${quiz.total-polls}")
    private int totalPolls;

    @Value("${quiz.poll-delay-ms}")
    private long pollDelayMs;

    @Value("${quiz.max-retries-per-poll:5}")
    private int maxRetriesPerPoll;

    public QuizPollerService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Executes polls 0..9 with a mandatory delay and returns raw events (including duplicates).
     */
    public List<Event> pollAllRounds() {
        String normalizedRegNo = StringUtils.trimWhitespace(regNo);

        if (!StringUtils.hasText(normalizedRegNo)) {
            throw new IllegalStateException("quiz.reg-no is not set. Define QUIZ_REG_NO before running the app.");
        }

        List<Event> allEvents = new ArrayList<>();

        log.info("Polling started: regNo={}, polls={}, delayMs={}, retriesPerPoll={}",
                normalizedRegNo, totalPolls, pollDelayMs, maxRetriesPerPoll);

        for (int poll = 0; poll < totalPolls; poll++) {
            List<Event> events = fetchEventsWithRetry(poll, normalizedRegNo);
            allEvents.addAll(events);
            log.info("Poll {}: {} events (total={})", poll, events.size(), allEvents.size());

            if (poll < totalPolls - 1) {
                sleep(pollDelayMs);
            }
        }

        log.info("Polling complete: rawEvents={}", allEvents.size());

        return allEvents;
    }

    private List<Event> fetchEventsWithRetry(int pollIndex, String normalizedRegNo) {
        int attempts = 0;

        while (attempts < maxRetriesPerPoll) {
            attempts++;
            PollResponse response = fetchPollOnce(pollIndex, normalizedRegNo);
            if (response != null) {
                if (attempts > 1) log.info("Poll {} recovered on attempt {}", pollIndex, attempts);
                return response.getEvents() == null ? Collections.emptyList() : response.getEvents();
            }

            if (attempts < maxRetriesPerPoll) {
                log.warn("Poll {} retry {}/{}", pollIndex, attempts, maxRetriesPerPoll);
                sleep(pollDelayMs);
            }
        }

        throw new IllegalStateException(
                String.format("Poll %d failed after %d attempts. Aborting to avoid partial leaderboard.", pollIndex, maxRetriesPerPoll)
        );
    }

    /**
     * Makes a single GET request for the given poll index.
     */
    private PollResponse fetchPollOnce(int pollIndex, String normalizedRegNo) {
        String url = String.format("%s/quiz/messages?regNo=%s&poll=%d", baseUrl, normalizedRegNo, pollIndex);
        log.debug("  GET {}", url);

        try {
            PollResponse response = restTemplate.getForObject(url, PollResponse.class);
            log.debug("  Response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Poll {} failed: {}", pollIndex, e.getMessage());
            return null;
        }
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Sleep interrupted during poll delay");
        }
    }
}
