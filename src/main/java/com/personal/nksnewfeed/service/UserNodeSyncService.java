package com.personal.nksnewfeed.service;

import com.personal.nksnewfeed.entity.UserNode;
import com.personal.nksnewfeed.event.UserCreatedEvent;
import com.personal.nksnewfeed.repository.UserNodeRepository;
import com.personal.nksnewfeed.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserNodeSyncService {
    
    private final UserNodeRepository userNodeRepository;
    private final UserRepository userRepository;
    
    @Retryable(
            retryFor = {Exception.class},
            recover = "recoverUser",
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    public void syncUserToUserNode(final UserCreatedEvent event) {
        log.info("Syncing user to user node - userId: {}, attempt: {}/3",
                 event.userId(), getCurrentAttempt());

        userNodeRepository.save(new UserNode(event.userId(), event.username()));
    }
    
    @Recover
    public void recoverUser(final Exception ex, final UserCreatedEvent event) {
        log.error("All retry attempts failed for userId: {}. Executing compensation logic.",
                  event.userId(), ex);
        
        try {
            userRepository.deleteById(event.userId());
            log.warn("Compensation executed: Deleted user from userRepository for userId: {}",
                    event.userId());
            
        } catch (final Exception compensationEx) {
            log.error("CRITICAL: Compensation logic failed for userId: {}. " +
                     "Data inconsistency detected! Manual intervention required!",
                      event.userId(), compensationEx);
        }
    }
    
    private static int getCurrentAttempt() {
        return Optional.ofNullable(RetrySynchronizationManager.getContext())
                .map(retryContext -> retryContext.getRetryCount() + 1)
                .orElse(0);
    }
}
