package com.laudog.controller;

import com.laudog.service.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class Controller {

    private final Service messages;

    public Controller(Service messages) {
        this.messages = messages;
    }

    @GetMapping("/message")
    public Mono<String> message() {
        return this.messages.findMessage();
    }

    @GetMapping("/secret")
    public Mono<String> secretMessage() {
        return this.messages.findSecretMessage();
    }
}
