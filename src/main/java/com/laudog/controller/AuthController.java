package com.laudog.controller;

import com.laudog.domain.user.UserDto;
import com.laudog.security.BiscuitTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author hantsy
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final BiscuitTokenProvider tokenProvider;

    private final ReactiveAuthenticationManager authenticationManager;

    @PostMapping("/login")
    public Mono<ResponseEntity> login(
            @Valid @RequestBody Mono<UserDto.Login> authRequest) {

        return authRequest
                .flatMap(login -> this.authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(
                                login.getEmail(), login.getPassword()))
                        .map(this.tokenProvider::createToken))
                .map(biscuit -> {
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + biscuit);
                    return new ResponseEntity<>("", httpHeaders, HttpStatus.OK);
                });

    }

}
