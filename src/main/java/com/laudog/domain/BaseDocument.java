package com.laudog.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;

import java.util.Date;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseDocument {
    @Id
    @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
    protected String id;

    @Field
    protected Date createdAt = new Date();


    @Field
    protected Date updatedAt = new Date();

    @Field
    @Version
    private long version;
}
