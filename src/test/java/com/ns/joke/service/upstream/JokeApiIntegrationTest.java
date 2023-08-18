package com.ns.joke.service.upstream;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.ns.joke.config.MockJokeResponse;
import com.ns.joke.config.WireMockConfig;
import com.ns.joke.dto.upstream.response.JokeApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {WireMockConfig.class})
public class JokeApiIntegrationTest {

    @Autowired
    private WireMockServer mockJokeService;

    @Autowired
    private JokeApiClient jokeApiClient;

    @BeforeEach
    void setUp() throws IOException {
        MockJokeResponse.setup(mockJokeService);
    }

    @Test
    public void whenGetBooks_thenBooksShouldBeReturned() {
        Optional<JokeApiResponse> response = jokeApiClient.getJokes("Any",
                                                                    "single",
                                                                    true,
                                                                    "sexist,explicit",
                                                                    "json",
                                                                    9);
        assertTrue(response.isPresent());
        assertEquals(9, response.get().jokes().size());
    }

}
