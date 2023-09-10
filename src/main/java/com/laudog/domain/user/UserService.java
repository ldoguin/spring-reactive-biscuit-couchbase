package com.laudog.domain.user;

import com.laudog.security.AuthUserDetails;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserDto> registration(final UserDto.Registration registration);

    Mono<UserDto> login(final UserDto.Login login);

    Mono<UserDto> currentUser(final AuthUserDetails authUserDetails);

    Mono<UserDto> update(final UserDto.Update update, final AuthUserDetails authUserDetails);

    Flux<UserDto> findAllUsers();

    Mono<UserDto> findByEmail(String email);

    Mono<Void> deleteUser(String userId);
}
