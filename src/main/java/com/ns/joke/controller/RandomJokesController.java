package com.ns.joke.controller;

import com.ns.joke.dto.downstream.response.JokeResponse;
import com.ns.joke.service.downstream.RandomJokesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/joke")
public class RandomJokesController {

    private final RandomJokesService randomJokesService;

    @GetMapping
    public ResponseEntity<JokeResponse> getJokes() {
        JokeResponse joke = randomJokesService.getJoke();
        return new ResponseEntity<>(joke, HttpStatus.OK);
    }

}
