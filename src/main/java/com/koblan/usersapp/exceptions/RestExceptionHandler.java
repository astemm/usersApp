package com.koblan.usersapp.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;

import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    //@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object>  handleMethodArgumentNotValid (MethodArgumentNotValidException ex,
    HttpHeaders headers, HttpStatusCode status, WebRequest request) {
       
       Map<String, String> errors = new HashMap<>();
       ex.getBindingResult().getAllErrors().forEach((error) -> {
       String fieldName;
       if (error instanceof FieldError) {
       fieldName = ((FieldError) error).getField();
       }
       else {
       fieldName=error.getObjectName();
       }
       String errorMessage = error.getDefaultMessage();
       errors.put(fieldName, errorMessage);
       });

       return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(NoSuchUserException.class)
    public ResponseEntity<String> handleNoSuchUserException(NoSuchUserException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        
    }

    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
    HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @Override
    public ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
    HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(NotHaveMinimumAgeException.class)
    public ResponseEntity<String> handleMinAgeException(NotHaveMinimumAgeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(PatchUserException.class)
    public ResponseEntity<String> handlePatchUserException(PatchUserException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(IncorrectDateRangeException.class)
    public ResponseEntity<String> handleIncorrectDateRange(IncorrectDateRangeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}

