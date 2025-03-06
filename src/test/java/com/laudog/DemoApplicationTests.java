package com.laudog;

import com.laudog.domain.user.UserDto;
import com.laudog.security.AuthUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static com.laudog.service.Service.MESSAGE;
import static com.laudog.service.Service.SECRET;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoApplicationTests extends AbstractTest {


    @Value(value="${local.server.port}")
    private int port;

    @Test
    public void controllerTestAdmin() {
        UserDto u = createUser("lo@lo.com", "ldoguin", "pass", "ADMIN");

        WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:"+port)
            .build();

        var token = webClient.post().uri("/auth/login")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue("{\"user\":{\"email\":\"lo@lo.com\",\"password\":\"pass\"}}")
            .exchangeToMono(
                clientResponse -> {
                    return Mono.just(clientResponse.headers()
                        .header(HttpHeaders.AUTHORIZATION));
                })
            .block().get(0);
        System.out.println(token);

        webClient = webClient.mutate().defaultHeader(HttpHeaders.AUTHORIZATION, token).build();
        String msg = webClient.get().uri("/message")
            .retrieve().bodyToMono(String.class).block();
        assertEquals(MESSAGE, msg);

        webClient = webClient.mutate().defaultHeader(HttpHeaders.AUTHORIZATION, token).build();
        msg = webClient.get().uri("/secret")
            .retrieve().bodyToMono(String.class).block();
        assertEquals(SECRET, msg);

        userService.update(UserDto.Update.builder().roles(Collections.emptyList()).build(),
            AuthUserDetails.builder().userDto(u).build()).block();

        webClient = webClient.mutate().defaultHeader(HttpHeaders.AUTHORIZATION, token).build();
        HttpStatusCode statusCode = webClient.get().uri("/secret")
            .exchangeToMono(clientResponse ->
                Mono.just(clientResponse.statusCode())).block();
        assertEquals(200, statusCode.value());


    }

    @Test
    public void controllerTestUser() {
        createUser("lo@lo.com", "ldoguin", "pass", "USER");

        WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:"+port)
            .build();

        var token = webClient.post().uri("/auth/login")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue("{\"user\":{\"email\":\"lo@lo.com\",\"password\":\"pass\"}}")
            .exchangeToMono(
                clientResponse -> {
                    return Mono.just(clientResponse.headers()
                        .header(HttpHeaders.AUTHORIZATION));
                })
            .block().get(0);
        System.out.println(token);

        webClient = webClient.mutate().defaultHeader(HttpHeaders.AUTHORIZATION, token).build();
        String msg = webClient.get().uri("/message")
            .retrieve().bodyToMono(String.class).block();
        assertEquals(MESSAGE, msg);

        webClient = webClient.mutate().defaultHeader(HttpHeaders.AUTHORIZATION, token).build();
        HttpStatusCode statusCode = webClient.get().uri("/secret")
            .exchangeToMono(clientResponse ->
                Mono.just(clientResponse.statusCode())).block();
        assertEquals(403, statusCode.value());

    }

}
