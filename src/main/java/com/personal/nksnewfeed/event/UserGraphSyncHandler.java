package com.personal.nksnewfeed.event;

import com.personal.nksnewfeed.service.UserNodeSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserGraphSyncHandler {
    
    private final UserNodeSyncService userNodeSyncService;
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserCreated(final UserCreatedEvent event) {
        log.info("Received user created event for userId: {}", event.userId());
        userNodeSyncService.syncUserToUserNode(event);
    }
}
