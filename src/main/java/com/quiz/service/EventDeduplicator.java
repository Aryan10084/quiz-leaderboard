package com.quiz.service;

import com.quiz.model.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Handles deduplication of raw events using the composite key: roundId + participant.
 *
 * The quiz API may deliver the same event data across multiple polls.
 * Only the FIRST occurrence of each (roundId, participant) pair is kept.
 */
@Slf4j
@Service
public class EventDeduplicator {

    public List<Event> deduplicate(List<Event> rawEvents) {
        Set<String> seen = new HashSet<>();
        List<Event> unique = new ArrayList<>();

        for (Event event : rawEvents) {
            if (seen.add(event.getDeduplicationKey())) {
                unique.add(event);
            }
        }

        log.info("Deduplication complete: raw={}, unique={}", rawEvents.size(), unique.size());

        return unique;
    }
}
