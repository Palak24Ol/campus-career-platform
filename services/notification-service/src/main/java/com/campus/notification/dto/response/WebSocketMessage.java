package com.campus.notification.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class WebSocketMessage {

    private UUID notificationId;
    private String type;
    private String title;
    private String message;
    private LocalDateTime createdAt;
}
