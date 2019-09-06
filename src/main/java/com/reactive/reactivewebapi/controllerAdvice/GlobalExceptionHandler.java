package com.reactive.reactivewebapi.controllerAdvice;

import com.reactive.reactivewebapi.common.response.BaseWebResponse;
import com.reactive.reactivewebapi.exception.BookNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.reactive.reactivewebapi.common.ErrorCode.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<BaseWebResponse> handleException() {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(BaseWebResponse.error(AUTHOR_NOT_FOUND));
    }
}
