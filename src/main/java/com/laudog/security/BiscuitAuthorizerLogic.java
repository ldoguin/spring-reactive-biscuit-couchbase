package com.laudog.security;

import lombok.RequiredArgsConstructor;

import org.biscuitsec.biscuit.token.Authorizer;
import org.biscuitsec.biscuit.token.Biscuit;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Set;

@RequiredArgsConstructor
public class BiscuitAuthorizerLogic  implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final Set<String> rules;

    public final BiscuitTokenProvider biscuitTokenProvider;

    public static BiscuitAuthorizerLogic checkRules(Set<String> rules, BiscuitTokenProvider biscuitTokenProvider) {
        Assert.notNull(rules, "rules cannot be null");
        return new BiscuitAuthorizerLogic(rules, biscuitTokenProvider);
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        String token = BiscuitAuthFilter.resolveToken(authorizationContext.getExchange().getRequest());
        try {
            Biscuit b = biscuitTokenProvider.getVerifiedBiscuit(token);
            Authorizer authorizer = b.authorizer();
            for (String rule : rules) {
                authorizer.add_check(rule);
            }
            authorizer.allow().authorize();
        } catch (org.biscuitsec.biscuit.error.Error e) {
            return Mono.just(new AuthorizationDecision(false));
        }
        catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException | Error e) {
            throw new RuntimeException(e);
        }
        return Mono.just(new AuthorizationDecision(true));
    }
}
