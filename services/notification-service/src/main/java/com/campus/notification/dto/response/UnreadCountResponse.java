package com.campus.notification.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UnreadCountResponse {

    private long count;
}
