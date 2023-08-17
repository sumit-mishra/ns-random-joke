package com.ns.joke.dto.upstream.response;

import lombok.NonNull;

import java.util.Comparator;
import java.util.Objects;

public record Joke(String category,
                   String type,
                   String joke,
                   Flags flags,
                   int id,
                   boolean safe,
                   String lang) implements Comparable<Joke> {

    @Override
    public int compareTo(@NonNull Joke joke) {
        return Objects.compare(this, joke, shortestJokeComparator());
    }

    private Comparator<Joke> shortestJokeComparator() {
        return Comparator.comparing(j -> j.joke.length());
    }

}
