package com.ancora.customerbookshelf.exception;


import com.ancora.customerbookshelf.dto.ErrorPayload;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorPayload> handleNoHandlerFound(NoHandlerFoundException ex, HttpServletRequest request) {
        log.warn("No handler found for request {}: {}", request.getRequestURI(), ex.getMessage());
        ErrorPayload payload = ErrorPayload.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message("Endpoint not found: " + request.getRequestURI())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(payload);
    }

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

        log.error("Validation error for request {}: {}", request.getRequestURI(), errorDetails);

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
        log.error("ConflictException on request {}: {}", request.getRequestURI(), ex.getMessage());
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
        log.info("NoContentException on request {}: {}", request.getRequestURI(), ex.getMessage());
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
        log.error("ResourceNotFoundException on request {}: {}", request.getRequestURI(), ex.getMessage());
        ErrorPayload payload = ErrorPayload.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(payload);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorPayload> handleDataIntegrity(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        String message = "Data integrity violation. A unique constraint might have been violated.";
        if (ex.getMessage().toLowerCase().contains("cpf")) {
            message = "A customer with this CPF already exists.";
        } else if (ex.getMessage().toLowerCase().contains("email")) {
            message = "A customer with this email already exists.";
        }
        log.error("DataIntegrityViolationException on request {}: {}", request.getRequestURI(), message, ex);

        ErrorPayload payload = ErrorPayload.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(payload);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorPayload> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unexpected generic Exception on request {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ErrorPayload payload = ErrorPayload.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("An unexpected internal error occurred.")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payload);
    }
}
