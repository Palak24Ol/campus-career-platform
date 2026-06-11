package com.campus.notification.controller;

import com.campus.notification.dto.response.NotificationResponse;
import com.campus.notification.dto.response.UnreadCountResponse;
import com.campus.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAll(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(notificationService.getForUser(UUID.fromString(userId)));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnread(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(notificationService.getUnreadForUser(UUID.fromString(userId)));
    }

    @GetMapping("/unread/count")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(notificationService.getUnreadCount(UUID.fromString(userId)));
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllRead(
            @RequestHeader("X-User-Id") String userId) {
        notificationService.markAllRead(UUID.fromString(userId));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable UUID id) {
        notificationService.markRead(id);
        return ResponseEntity.ok().build();
    }
}
