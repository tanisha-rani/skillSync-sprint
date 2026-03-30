package com.skillsync.reviewservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Centralized exception handling for Review Service.
 * Catches all exceptions and returns structured ErrorResponse.
 * Prevents raw stack traces from reaching the client.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ReviewNotFoundException — returns 404.
     */
    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReviewNotFound(
            ReviewNotFoundException ex, HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorResponse(
                LocalDateTime.now(), ex.getMessage(),
                HttpStatus.NOT_FOUND.value(), request.getRequestURI()),
                HttpStatus.NOT_FOUND);
    }

    /**
     * Handles ReviewAlreadyExistsException — returns 409.
     */
    @ExceptionHandler(ReviewAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleReviewAlreadyExists(
            ReviewAlreadyExistsException ex, HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorResponse(
                LocalDateTime.now(), ex.getMessage(),
                HttpStatus.CONFLICT.value(), request.getRequestURI()),
                HttpStatus.CONFLICT);
    }

    /**
     * Handles validation errors — returns 400 with field-wise messages.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return new ResponseEntity<>(new ErrorResponse(
                LocalDateTime.now(), errors.toString(),
                HttpStatus.BAD_REQUEST.value(), request.getRequestURI()),
                HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles type mismatch in path variables — returns 400.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorResponse(
                LocalDateTime.now(),
                "Invalid value '" + ex.getValue() + "' for parameter '" + ex.getName() + "'",
                HttpStatus.BAD_REQUEST.value(), request.getRequestURI()),
                HttpStatus.BAD_REQUEST);
    }

    /**
     * Fallback handler for all unexpected exceptions — returns 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex, HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorResponse(
                LocalDateTime.now(), ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getRequestURI()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
