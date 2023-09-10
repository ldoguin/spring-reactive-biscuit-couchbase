package com.laudog;

import com.clevercloud.biscuit.error.Error;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryScanConsistency;
import com.laudog.domain.user.UserDocument;
import com.laudog.domain.user.UserService;
import com.laudog.domain.user.UserDto;
import com.laudog.security.AuthUserDetails;
import com.laudog.security.BiscuitTokenProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Collections;
import java.util.List;

import static com.laudog.service.Service.MESSAGE;
import static com.laudog.service.Service.SECRET;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoApplicationTests {


    @Value(value="${local.server.port}")
    private int port;

    @Autowired
    Scope defaultScope;

    @Autowired
    UserService userService;
    @Autowired
    BiscuitTokenProvider biscuitTokenProvider;

    @BeforeEach
    void init() {
        flushDB(defaultScope, UserDocument.USER_COLLECTION_NAME);

    }

    @AfterEach
    void teardown() {
        flushDB(defaultScope, UserDocument.USER_COLLECTION_NAME);
    }

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
        assertEquals(403, statusCode.value());


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

    public UserDto createUser(String email, String username, String password, String... roles) {
        UserDto.Registration reg = UserDto.Registration.builder()
            .username(username)
            .password(password)
            .email(email)
            .roles(List.of(roles))
            .build();
        var u =  userService.registration(reg).block();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return u;
    }


    public void flushDB(Scope defaultScope, String collection) {
        defaultScope.query("DELETE FROM " + collection, QueryOptions.queryOptions().scanConsistency(QueryScanConsistency.REQUEST_PLUS));
    }

}
