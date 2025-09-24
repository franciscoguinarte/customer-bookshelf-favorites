package com.ancora.customerbookshelf.exception;


import com.ancora.customerbookshelf.dto.ErrorPayload;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorPayload> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<Map<String, String>> errorDetails = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    Map<String, String> detail = new HashMap<>();
                    detail.put("field", error.getField());
                    detail.put("message", error.getDefaultMessage());
                    return detail;
                })
                .collect(Collectors.toList());

        ErrorPayload payload = ErrorPayload.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed for one or more fields.")
                .path(request.getRequestURI())
                .details(errorDetails)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payload);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorPayload> handleConflict(
            ConflictException ex,
            HttpServletRequest request
    ) {
        ErrorPayload payload = ErrorPayload.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(payload);
    }

    @ExceptionHandler(NoContentException.class)
    public ResponseEntity<ErrorPayload> handleNoContent(
            NoContentException ex,
            HttpServletRequest request
    ) {
        ErrorPayload payload = ErrorPayload.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NO_CONTENT.value())
                .error(HttpStatus.NO_CONTENT.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(payload);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorPayload> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        ErrorPayload payload = ErrorPayload.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(payload);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorPayload> handleGeneric(
            HttpServletRequest request
    ) {
        ErrorPayload payload = ErrorPayload.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Unexpected error occurred")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payload);
    }
}
