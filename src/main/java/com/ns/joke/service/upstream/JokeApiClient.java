package com.ns.joke.service.upstream;

import com.ns.joke.dto.upstream.response.JokeApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@FeignClient(name = "joke-api", url = "${ns.default.joke-source-url}")
public interface JokeApiClient {

    @GetMapping("/joke/{category}")
    Optional<JokeApiResponse> getJokes(@PathVariable(value = "category") String category,
                                       @RequestParam(value = "type") String type,
                                       @RequestParam(value = "safe-mode") boolean isSafe,
                                       @RequestParam(value = "blacklistFlags") String blacklistFlags,
                                       @RequestParam(value = "format") String format,
                                       @RequestParam(value = "amount") int amount);
}
