package com.smbc.library.rent_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.smbc.library.rent_service.dto.ResponseDto;
import com.smbc.library.rent_service.utils.ResponseUtil;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class CommonExceptionHandler {

    @ExceptionHandler(exception = MethodArgumentNotValidException.class)
    public ResponseDto<?> handleValidationException(MethodArgumentNotValidException e) {
        FieldError err = e.getBindingResult().getFieldErrors().getFirst();
        String fieldName = err.getField().substring(0, 1).toUpperCase() + err.getField().substring(1);
        String defaultMessage = err.getDefaultMessage();
        if (defaultMessage != null) {
            log.error("Method argument exception: {}", defaultMessage);
            return ResponseUtil.failed(HttpStatus.BAD_REQUEST.value(), defaultMessage);
        }

        String message = fieldName.concat(" error");
        log.error("Method argument exception: {}", message);
        return ResponseUtil.failed(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseDto<?> handleGenericException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage());
        return ResponseUtil.failed(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong.");
    }
}
