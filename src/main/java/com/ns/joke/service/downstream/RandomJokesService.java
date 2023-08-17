package com.ns.joke.service.downstream;

import com.ns.joke.dto.downstream.response.JokeResponse;
import com.ns.joke.dto.upstream.response.Joke;
import com.ns.joke.dto.upstream.response.JokeApiResponse;
import com.ns.joke.service.cache.JokeCacheService;
import com.ns.joke.service.upstream.JokeApiClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.SortedSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class RandomJokesService {

    @Value("${ns.default.format}")
    private String format;
    @Value("${ns.default.joke-amount}")
    private int amount;
    @Value("${ns.default.joke-type}")
    private String jokeType;
    @Value("${ns.default.joke-category}")
    private String category;
    @Value("${ns.default.safe-mode}")
    private boolean isSafe;
    @Value("${ns.default.blacklist-flags}")
    private String blacklistFlags;

    private final JokeApiClient jokeApiClient;
    private final JokeCacheService jokeCacheService;

    @CachePut(value="JokeResponse", key="#result.id()", unless="#result.randomJoke().length() > 100")
    @CircuitBreaker(name = "RANDOM_JOKE_SERVICE", fallbackMethod = "getRandomJokeFromCache")
    public JokeResponse getJoke() {
        Optional<JokeApiResponse> jokeApiResponse = jokeApiClient.getJokes(category,
                                                                           jokeType,
                                                                           isSafe,
                                                                           blacklistFlags,
                                                                           format,
                                                                           amount);
        return jokeApiResponse.map(this::getOneJoke).orElseGet(this::getRandomJokeFromCache);
    }

    private JokeResponse getOneJoke(JokeApiResponse jokeApiResponse) {
        log.debug("RandomJokesService::getOneJoke, filtering a joke from {} jokes.", jokeApiResponse.jokes().size());
        return getShortestJoke(jokeApiResponse.jokes());
    }

    private JokeResponse getShortestJoke(SortedSet<Joke> jokes) {
        Joke shortestJoke = jokes.iterator().next();
        return new JokeResponse(shortestJoke.id(), shortestJoke.joke());
    }

    private JokeResponse getRandomJokeFromCache() {
        log.error("RandomJokesService::getRandomJokeFromCache, jokeApiClient resulted might have resulted with 'null' response");
        return jokeCacheService.getRandomJoke();
    }

    private JokeResponse getRandomJokeFromCache(Exception e) {
        log.error("RandomJokesService::getRandomJokeFromCache, jokeApiClient resulted in exception : {}", e.getMessage());
        log.info("RandomJokesService::getRandomJokeFromCache, upstream api did not send valid response, fetching jokes from cache.");
        return getRandomJokeFromCache();
    }

}
