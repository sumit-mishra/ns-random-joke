package com.ns.joke.exception;

public class JokeServiceException extends RuntimeException {
    public JokeServiceException(String message) {
        super(message);
    }
}