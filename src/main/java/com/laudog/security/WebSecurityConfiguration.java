package com.laudog.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import java.util.Set;

import static com.laudog.security.BiscuitAuthorizerLogic.checkRules;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
public class WebSecurityConfiguration {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                     BiscuitTokenProvider tokenProvider,
                                                     ReactiveAuthenticationManager reactiveAuthenticationManager) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .authenticationManager(reactiveAuthenticationManager)
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .authorizeExchange(exchanges -> exchanges
//                .pathMatchers(HttpMethod.GET, "/message", "/secret")
//                    .access(checkRules(Set.of("check if role(\"USER\")"), tokenProvider))
                .pathMatchers("/me").authenticated()
                .pathMatchers("/users/{user}/**").access(this::currentUserMatchesPath)
                .anyExchange().permitAll()
            )
            .addFilterAt(new BiscuitAuthFilter(tokenProvider), SecurityWebFiltersOrder.HTTP_BASIC);
        return http.build();
    }


    private Mono<AuthorizationDecision> currentUserMatchesPath(Mono<Authentication> authentication,
                                                               AuthorizationContext context) {

        return authentication
            .map(a -> context.getVariables().get("user").equals(a.getName()))
            .map(AuthorizationDecision::new);

    }


    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService,
                                                                       PasswordEncoder passwordEncoder) {
        var authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder);
        return authenticationManager;
    }

}
