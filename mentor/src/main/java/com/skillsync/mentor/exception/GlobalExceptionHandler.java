package com.skillsync.mentor.exception;

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
 * Centralized exception handling for Mentor Service.
 * Catches all exceptions and returns structured ErrorResponse.
 * Prevents raw error stack traces from reaching the client.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles MentorNotFoundException.
     * Thrown when mentor with given ID does not exist.
     * Returns 404 NOT FOUND.
     */
    @ExceptionHandler(MentorNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMentorNotFound(
            MentorNotFoundException ex,
            HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles MentorAlreadyExistsException.
     * Thrown when mentor profile already exists for given userId.
     * Returns 409 CONFLICT.
     */
    @ExceptionHandler(MentorAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleMentorAlreadyExists(
            MentorAlreadyExistsException ex,
            HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /**
     * Handles validation errors on request body fields.
     * Thrown when @Valid fails on any DTO field.
     * Returns 400 BAD REQUEST with field-wise error messages.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                errors.toString(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles type mismatch errors in path variables.
     * Thrown when invalid value is passed for enum or Long path variable.
     * Returns 400 BAD REQUEST.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                "Invalid value '" + ex.getValue() + "' for parameter '" + ex.getName() + "'",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all unexpected exceptions.
     * Acts as fallback for any unhandled exception.
     * Returns 500 INTERNAL SERVER ERROR.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}