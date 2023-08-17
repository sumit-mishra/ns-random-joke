package com.ns.joke.config;

import com.ns.joke.dto.downstream.response.Error;
import com.ns.joke.exception.EmptyCacheException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class JokeServiceExceptionHandler {

    @ExceptionHandler(EmptyCacheException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error handleServiceException(RuntimeException exception) {
        return new Error("JOKE NOT FOUND", exception.getMessage());
    }

}
