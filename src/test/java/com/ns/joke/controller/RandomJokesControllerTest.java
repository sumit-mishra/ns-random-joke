package com.ns.joke.controller;

import com.ns.joke.dto.downstream.response.JokeResponse;
import com.ns.joke.exception.EmptyCacheException;
import com.ns.joke.service.downstream.RandomJokesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RandomJokesController.class)
public class RandomJokesControllerTest {

    @MockBean
    RandomJokesService randomJokesService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void getJoke_success() throws Exception {
        JokeResponse jokeResponse = new JokeResponse(101, "I have too much free time, so I am reading jokes");
        when(randomJokesService.getJoke()).thenReturn(jokeResponse);

        mvc.perform(MockMvcRequestBuilders
                        .get("/joke")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.randomJoke").isNotEmpty());
    }

    @Test
    public void getJoke_return_jokeNotFound_when_jokeCacheIsEmpty() throws Exception {
        when(randomJokesService.getJoke()).thenThrow(new EmptyCacheException("JokeCache is empty of JokeResponse."));

        mvc.perform(MockMvcRequestBuilders
                        .get("/joke")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value("JOKE NOT FOUND"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").isNotEmpty());
    }

}
