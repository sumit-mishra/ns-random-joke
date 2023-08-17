package com.ns.joke.dto.upstream.response;

public record Flags(boolean nsfw,
                    boolean religious,
                    boolean racist,
                    boolean sexist,
                    boolean political,
                    boolean explicit) {
}
