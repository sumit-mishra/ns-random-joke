package com.ns.joke.controller;

import com.ns.joke.NsRandomJokeApplication;
import com.ns.joke.dto.downstream.response.JokeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(classes = NsRandomJokeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RandomJokesControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetJoke() {
        JokeResponse response = restTemplate.getForObject("http://localhost:" + port + "/joke", JokeResponse.class);

        assertFalse(response.randomJoke().isBlank());
    }

}
