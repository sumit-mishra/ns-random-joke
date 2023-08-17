package com.ns.joke.service.downstream;

import com.ns.joke.dto.downstream.response.JokeResponse;
import com.ns.joke.dto.upstream.response.Flags;
import com.ns.joke.dto.upstream.response.Joke;
import com.ns.joke.dto.upstream.response.JokeApiResponse;
import com.ns.joke.service.cache.JokeCacheService;
import com.ns.joke.service.upstream.JokeApiClient;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = RandomJokesService.class)
@EnableAutoConfiguration
public class RandomJokesServiceTest {

    @MockBean
    JokeApiClient jokeApiClient;
    @MockBean
    JokeCacheService jokeCacheService;
    @Autowired
    CircuitBreakerRegistry circuitBreakerRegistry;
    @Autowired
    RandomJokesService randomJokesService;

    @BeforeEach
    void setup() {
        circuitBreakerRegistry.circuitBreaker("RANDOM_JOKE_SERVICE").reset();
    }

    @Test
    void getJoke_success() {
        JokeApiResponse jokeApiResponse = new JokeApiResponse(false, 2, getJokes());
        when(jokeApiClient.getJokes(any(), any(), anyBoolean(), any(), any(), anyInt())).thenReturn(Optional.of(jokeApiResponse));

        JokeResponse joke = randomJokesService.getJoke();

        Assertions.assertEquals(96, joke.id());
        Assertions.assertEquals("Hello World! I am a programmer", joke.randomJoke());
        verify(jokeCacheService, never()).getRandomJoke();
    }

    @Test
    void getJoke_whenJokeApiReturnsEmptyResponse_shouldFallBackToJokeCache() {
        when(jokeApiClient.getJokes(any(), any(), anyBoolean(), any(), any(), anyInt())).thenReturn(Optional.empty());
        when(jokeCacheService.getRandomJoke()).thenReturn(getJokeResponse());

        JokeResponse joke = randomJokesService.getJoke();

        Assertions.assertEquals(96, joke.id());
        Assertions.assertEquals("Hello World! I am a programmer", joke.randomJoke());
        verify(jokeApiClient, times(1)).getJokes(any(), any(), anyBoolean(), any(), any(), anyInt());
        verify(jokeCacheService, times(1)).getRandomJoke();
    }

    @Test
    void getJoke_whenJokeApiReturnsResponseWithNullJokes_shouldFallBackToJokeCache() {
        JokeApiResponse jokeApiResponse = getJokeApiResponse(null);
        when(jokeApiClient.getJokes(any(), any(), anyBoolean(), any(), any(), anyInt())).thenReturn(Optional.of(jokeApiResponse));
        when(jokeCacheService.getRandomJoke()).thenReturn(getJokeResponse());

        JokeResponse joke = randomJokesService.getJoke();

        Assertions.assertEquals(96, joke.id());
        Assertions.assertEquals("Hello World! I am a programmer", joke.randomJoke());
        verify(jokeApiClient, times(1)).getJokes(any(), any(), anyBoolean(), any(), any(), anyInt());
        verify(jokeCacheService, times(1)).getRandomJoke();
    }

    @Test
    void getJoke_whenJokeApiThrowsException_shouldFallBackToJokeCache() {
        when(jokeApiClient.getJokes(any(), any(), anyBoolean(), any(), any(), anyInt())).thenThrow(new RuntimeException("Network is unreachable, connection is dropped."));
        when(jokeCacheService.getRandomJoke()).thenReturn(getJokeResponse());

        JokeResponse joke = randomJokesService.getJoke();

        Assertions.assertEquals(96, joke.id());
        Assertions.assertEquals("Hello World! I am a programmer", joke.randomJoke());
        verify(jokeApiClient, times(1)).getJokes(any(), any(), anyBoolean(), any(), any(), anyInt());
        verify(jokeCacheService, times(1)).getRandomJoke();
    }

    @Test
    void getJoke_whenCircuitBreakerIsInOpenState_shouldFallBackToJokeCache() {
        circuitBreakerRegistry.circuitBreaker("RANDOM_JOKE_SERVICE").transitionToOpenState();
        JokeApiResponse jokeApiResponse = getJokeApiResponse(getJokes());
        when(jokeApiClient.getJokes(any(), any(), anyBoolean(), any(), any(), anyInt())).thenReturn(Optional.of(jokeApiResponse));

        JokeResponse jokeResponse = new JokeResponse(96, "Hello World! I am a programmer");
        when(jokeCacheService.getRandomJoke()).thenReturn(jokeResponse);

        JokeResponse joke = randomJokesService.getJoke();

        Assertions.assertEquals(96, joke.id());
        Assertions.assertEquals("Hello World! I am a programmer", joke.randomJoke());
        verify(jokeApiClient, never()).getJokes(any(), any(), anyBoolean(), any(), any(), anyInt());
        verify(jokeCacheService, times(1)).getRandomJoke();
    }

    private TreeSet<Joke> getJokes() {
        Flags flags = new Flags(false, false, false, false, false, false);
        Joke joke1 = new Joke("programming", "single", "Hello World! I am a programmer", flags, 96, true, "en");
        Joke joke2 = new Joke("programming", "single", "I am an exception, because I don't have any pointer.", flags, 97, true, "en");
        return Sets.newTreeSet(joke1, joke2);
    }

    private JokeResponse getJokeResponse() {
        return new JokeResponse(96, "Hello World! I am a programmer");
    }

    private JokeApiResponse getJokeApiResponse(TreeSet<Joke> jokes) {
        int amount = Objects.isNull(jokes) ? 0 : jokes.size();
        return new JokeApiResponse(false, amount, jokes);
    }
}
