package com.ancora.customerbookshelf.security;

import lombok.Data;

@Data
public class AuthRequestDTO {
    private String clientId;
    private String clientSecret;
}
