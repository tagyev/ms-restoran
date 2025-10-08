package com.example.msrestoran.expection;

import com.example.msrestoran.dto.response.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandle {

    @ExceptionHandler(NotFoundException.class)
    public ExceptionResponse handleAccountNotFound(NotFoundException ex) {
        log.error("Account not found: {}", ex.getMessage(), ex);
        return ExceptionResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .details("Account related error")
                .build();
    }
}