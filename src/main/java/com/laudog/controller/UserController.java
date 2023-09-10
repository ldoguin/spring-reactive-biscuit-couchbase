package com.laudog.controller;

import com.laudog.domain.user.UserDto;
import com.laudog.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author hantsy
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{username}")
    public Mono<UserDto> get(@PathVariable() String username) {
        return userService.findByEmail(username);
    }

}
