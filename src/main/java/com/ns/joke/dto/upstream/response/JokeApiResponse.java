package com.ns.joke.dto.upstream.response;

import java.util.SortedSet;

public record JokeApiResponse(boolean error,
                              int amount,
                              SortedSet<Joke> jokes) {

}
