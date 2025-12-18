package com.amirkenesbay.asanali_jwt_security.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
public class AuthenticationResponse {
    private String accessToken;
    private String refreshToken;
}

