package com.laudog.domain.user;

import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Collection(UserDocument.USER_COLLECTION_NAME)
public interface UserRepository extends ReactiveCouchbaseRepository<UserDocument, String> {

    Flux<UserDocument> findByUsernameOrEmail(String username, String email);

    Mono<UserDocument> findByEmail(String email);

    Mono<UserDocument> findById(String id);

    Mono<UserDocument> findByUsername(String username);


}
