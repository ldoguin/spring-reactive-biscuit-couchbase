package com.laudog.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("couchbase")
@Configuration
@Getter
@Setter
public class CouchbaseProperties{
    private String connectionString, username, password, defaultBucket, defaultScope, defaultCollection;

    private boolean useCapella;
}
