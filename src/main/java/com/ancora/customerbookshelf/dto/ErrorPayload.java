package com.ancora.customerbookshelf.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorPayload {
    private Instant timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
    private List<Map<String, String>> details;
}