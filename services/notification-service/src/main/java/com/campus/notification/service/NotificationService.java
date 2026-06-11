package com.campus.notification.service;

import com.campus.notification.dto.response.NotificationResponse;
import com.campus.notification.dto.response.UnreadCountResponse;
import com.campus.notification.dto.response.WebSocketMessage;
import com.campus.notification.entity.Notification;
import com.campus.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final WebSocketNotificationService webSocketService;

    @Transactional
    public Notification create(UUID userId, String type, String title, String message) {
        Notification notification = Notification.builder()
                .userId(userId)
                .type(type)
                .title(title)
                .message(message)
                .build();

        notification = notificationRepository.save(notification);

        webSocketService.pushToUser(userId, WebSocketMessage.builder()
                .notificationId(notification.getId())
                .type(type)
                .title(title)
                .message(message)
                .createdAt(notification.getCreatedAt())
                .build());

        return notification;
    }

    public List<NotificationResponse> getForUser(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    public List<NotificationResponse> getUnreadForUser(UUID userId) {
        return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    public UnreadCountResponse getUnreadCount(UUID userId) {
        return UnreadCountResponse.builder()
                .count(notificationRepository.countByUserIdAndReadFalse(userId))
                .build();
    }

    @Transactional
    public void markAllRead(UUID userId) {
        notificationRepository.markAllReadByUserId(userId);
    }

    @Transactional
    public void markRead(UUID notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .userId(n.getUserId())
                .type(n.getType())
                .title(n.getTitle())
                .message(n.getMessage())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
