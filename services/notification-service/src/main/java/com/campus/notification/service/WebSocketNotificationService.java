package com.campus.notification.service;

import com.campus.notification.dto.response.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void pushToUser(UUID userId, WebSocketMessage message) {
        String destination = "/topic/notifications/" + userId;
        try {
            messagingTemplate.convertAndSend(destination, message);
            log.debug("WebSocket push sent to userId={}, type={}", userId, message.getType());
        } catch (Exception e) {
            log.error("Failed to push WebSocket message to userId={}: {}", userId, e.getMessage());
        }
    }
}
