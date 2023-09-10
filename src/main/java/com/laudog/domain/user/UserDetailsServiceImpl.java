package com.laudog.domain.user;

import com.laudog.domain.user.UserService;
import com.laudog.security.AuthUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UserService userService;

    @Override
    public Mono<UserDetails> findByUsername(String email) throws UsernameNotFoundException {
        return userService.findByEmail(email)
            .map(userEntity ->
                AuthUserDetails.builder()
                    .userDto(userEntity)
                    .build());
    }

}
