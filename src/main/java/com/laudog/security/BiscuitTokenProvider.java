package com.laudog.security;

import org.biscuitsec.biscuit.crypto.KeyPair;
import org.biscuitsec.biscuit.error.Error;
import org.biscuitsec.biscuit.token.Biscuit;
import org.biscuitsec.biscuit.token.UnverifiedBiscuit;
import org.biscuitsec.biscuit.token.builder.Fact;
import org.biscuitsec.biscuit.token.builder.Term;
import com.laudog.config.BiscuitConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.laudog.security.BiscuitUtils.createFact;
import static com.laudog.security.BiscuitUtils.createFactCheck;

@Component
@RequiredArgsConstructor
public class BiscuitTokenProvider {

    private static final String AUTHORITIES_KEY = "role";

    private final BiscuitConfig securityConfig;

    private KeyPair keyPair;

    @PostConstruct
    public void init() {
        this.keyPair = new KeyPair();
    }

    public String createToken(Authentication authentication) {

        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication
            .getAuthorities();
        Collection<String> facts =
            authorities.stream().map(ah -> createFact("role", ah.getAuthority())).toList();
        return craftAuthNBiscuit(username, facts);
    }

    public String createToken(String username,  Collection<String> facts) {
        return craftAuthNBiscuit(username, facts);
    }

    private String craftAuthNBiscuit(String username, Collection<String> facts) {
        try {
            org.biscuitsec.biscuit.token.builder.Biscuit b = Biscuit.builder(this.keyPair)
                .add_authority_fact(createFact("user", username))
                .add_authority_check(createFactCheck("user", username));
            if (!facts.isEmpty()) {
                facts.forEach(fact ->
                        {
                            try {
                                b.add_authority_fact(fact);
                                b.add_authority_check(createFactCheck(fact));
                            } catch (Error.Parser | Error.Language e) {
                                throw new RuntimeException(e);
                            }
                        }
                    );
           }

            return b.build().serialize_b64url();
        } catch (Error e) {
            throw new RuntimeException(e);
        }
    }

    public Authentication getAuthentication(String token) {
        try {
            Biscuit b = getVerifiedToken(token);

            Set<Fact> userFact = b.authorizer()
                .query("user($user) <- user($user)");
            Term.Str principal = (Term.Str) userFact.stream().findFirst()
                .get().terms().get(0);

            Set<Fact> rolesFact = b.authorizer()
                .query("role($role) <- role($role)");
            List<GrantedAuthority> authorities = rolesFact.stream().map(fact -> {
                Term.Str t = (Term.Str) fact.terms().get(0);
                return new SimpleGrantedAuthority(String.format("ROLE_%s", t.getValue()));
            }).collect(Collectors.toUnmodifiableList());

            return new UsernamePasswordAuthenticationToken(principal.getValue(), token, authorities);
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException | Error e) {
            throw new RuntimeException(e);
        }
    }

    private Biscuit getVerifiedToken(String token) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, Error {
        UnverifiedBiscuit ub = Biscuit.from_b64url(token, this.keyPair.public_key());
        Biscuit b = ub.verify(this.keyPair.public_key());
        return b;
    }

    public boolean validateToken(String token) {
        try {
            Biscuit b = getVerifiedBiscuit(token);
            return true;
        } catch (NoSuchAlgorithmException | Error | InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }
        // return false;
    }

    public Biscuit getVerifiedBiscuit(String token) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, Error {
        UnverifiedBiscuit ub = Biscuit.from_b64url(token, this.keyPair.public_key());
        return ub.verify(this.keyPair.public_key());
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }
}
