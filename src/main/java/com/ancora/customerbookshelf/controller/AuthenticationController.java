package com.ancora.customerbookshelf.controller;

import com.ancora.customerbookshelf.security.AuthRequestDTO;
import com.ancora.customerbookshelf.security.AuthResponseDTO;
import com.ancora.customerbookshelf.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final JwtTokenProvider tokenProvider;
    private final String validClientId;
    private final String validClientSecret;

    public AuthenticationController(JwtTokenProvider tokenProvider,
                                @Value("${api.security.client-id}") String validClientId,
                                @Value("${api.security.client-secret}") String validClientSecret) {
        this.tokenProvider = tokenProvider;
        this.validClientId = validClientId;
        this.validClientSecret = validClientSecret;
    }

    @PostMapping("/token")
    public ResponseEntity<?> authenticateClient(@RequestBody AuthRequestDTO authRequest) {
        if (validClientId.equals(authRequest.getClientId()) && validClientSecret.equals(authRequest.getClientSecret())) {
            String token = tokenProvider.generateToken(authRequest.getClientId());
            return ResponseEntity.ok(new AuthResponseDTO(token));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid client credentials");
    }
}
