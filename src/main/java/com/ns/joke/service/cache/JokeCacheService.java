package com.ns.joke.service.cache;

import com.ns.joke.dto.downstream.response.JokeResponse;
import com.ns.joke.exception.EmptyCacheException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class JokeCacheService {
    private final CacheManager cacheManager;

    public JokeResponse getRandomJoke() {
        ConcurrentHashMap<?, ?> cachedJokeResponse = (ConcurrentHashMap<?, ?>) cacheManager.getCache("JokeResponse")
                                                                                           .getNativeCache();
        Object[] jokeCachedIndexes = cachedJokeResponse.keySet().toArray();
        if (jokeCachedIndexes.length < 1) {
            throw new EmptyCacheException("JokeCacheService is empty of JokeResponse.");
        }
        int randomJokeIndex = (int) jokeCachedIndexes[new Random().nextInt(jokeCachedIndexes.length)];
        return (JokeResponse) cachedJokeResponse.get(randomJokeIndex);
    }
}
