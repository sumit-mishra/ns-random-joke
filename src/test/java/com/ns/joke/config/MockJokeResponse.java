package com.ns.joke.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.ns.joke.dto.upstream.response.JokeApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

import static java.nio.charset.Charset.defaultCharset;
import static org.springframework.util.StreamUtils.copyToString;

public class MockJokeResponse {
    public static void setup(WireMockServer mockService) throws IOException {
        mockService.stubFor(WireMock.get(WireMock.urlPathEqualTo("/joke/Any"))
                .willReturn(
                        WireMock.aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(
                                        copyToString(
                                                JokeApiResponse.class.getClassLoader().getResourceAsStream("payload/jokes-response.json"),
                                                defaultCharset()))));
    }
}
