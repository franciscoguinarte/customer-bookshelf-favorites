package com.ancora.customerbookshelf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorPayload {
    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}