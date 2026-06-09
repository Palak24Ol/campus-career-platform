package com.campus.auth.dto.response;

import com.campus.auth.entity.User;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private UUID userId;
    private String email;
    private User.Role role;
}
