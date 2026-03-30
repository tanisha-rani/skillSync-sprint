package com.skillsync.group.exception;

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
 * Centralized exception handling for Group Service.
 * Catches all exceptions and returns structured ErrorResponse.
 * Prevents raw stack traces from ever reaching the client.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles GroupNotFoundException.
     * Thrown when a group with given ID does not exist in the database.
     * Returns 404 NOT FOUND.
     */
    @ExceptionHandler(GroupNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGroupNotFound(
            GroupNotFoundException ex,
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
     * Handles UserAlreadyMemberException.
     * Thrown when a user attempts to join a group they are already part of.
     * Returns 409 CONFLICT.
     */
    @ExceptionHandler(UserAlreadyMemberException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyMember(
            UserAlreadyMemberException ex,
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
     * Handles UserNotMemberException.
     * Thrown when a user attempts to leave or post in a group they haven't joined.
     * Returns 400 BAD REQUEST.
     */
    @ExceptionHandler(UserNotMemberException.class)
    public ResponseEntity<ErrorResponse> handleUserNotMember(
            UserNotMemberException ex,
            HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles GroupFullException.
     * Thrown when a user tries to join a group that has reached its maxMembers limit.
     * Returns 409 CONFLICT.
     */
    @ExceptionHandler(GroupFullException.class)
    public ResponseEntity<ErrorResponse> handleGroupFull(
            GroupFullException ex,
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
     * Handles GroupNotOpenException.
     * Thrown when a user tries to join a group whose status is CLOSED or ARCHIVED.
     * Returns 400 BAD REQUEST.
     */
    @ExceptionHandler(GroupNotOpenException.class)
    public ResponseEntity<ErrorResponse> handleGroupNotOpen(
            GroupNotOpenException ex,
            HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles UnauthorizedActionException.
     * Thrown when a non-creator user attempts an admin-only action (e.g. close group).
     * Returns 403 FORBIDDEN.
     */
    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedAction(
            UnauthorizedActionException ex,
            HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
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
     * Thrown when an invalid value is passed for enum or Long path variable.
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
     * Acts as a fallback for any unhandled exception type.
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
