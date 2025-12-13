package com.amirkenesbay.asanali_jwt_security.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
