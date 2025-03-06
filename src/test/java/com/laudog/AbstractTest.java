package com.laudog;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import com.couchbase.client.java.Scope;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryScanConsistency;
import com.laudog.domain.user.UserDocument;
import com.laudog.domain.user.UserDto;
import com.laudog.domain.user.UserService;
import com.laudog.security.BiscuitTokenProvider;

public abstract class AbstractTest {
    
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
