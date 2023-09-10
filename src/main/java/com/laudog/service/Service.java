package com.laudog.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class Service {

    public static final String MESSAGE = "Hello User!";

    public static final String SECRET = "Hello Admin!";

    /**
     * Gets a message if authenticated.
     * @return the message
     */
    @PreAuthorize("authenticated")
    public Mono<String> findMessage() {
        return Mono.just(MESSAGE);
    }

    /**
     * Gets a message if admin.
     * @return the message
     */
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<String> findSecretMessage() {
        return Mono.just("Hello Admin!");
    }

}
