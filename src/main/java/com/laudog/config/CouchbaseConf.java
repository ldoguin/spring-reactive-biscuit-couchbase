package com.laudog.config;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.codec.JsonSerializer;
import com.couchbase.client.java.env.ClusterEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.CouchbaseClientFactory;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class CouchbaseConf extends AbstractCouchbaseConfiguration {

    private final CouchbaseProperties couchbaseProperties;

    public CouchbaseConf(CouchbaseProperties couchbaseProperties) {
        this.couchbaseProperties = couchbaseProperties;
    }

    @Override
    public String getConnectionString() {
        return couchbaseProperties.getConnectionString();
    }

    @Override
    public String getUserName() {
        return couchbaseProperties.getUsername();
    }

    @Override
    public String getPassword() {
        return couchbaseProperties.getPassword();
    }

    @Override
    public String getBucketName() {
        return couchbaseProperties.getDefaultBucket();
    }

    @Override
    protected String getScopeName() {
        return couchbaseProperties.getDefaultScope();
    }

    @Bean
    public Bucket getDefaultBucket(CouchbaseClientFactory couchbaseClientFactory) {
        return couchbaseClientFactory.getBucket();
    }
    @Bean
    public Scope getDefaultScope(CouchbaseClientFactory couchbaseClientFactory) {
        return couchbaseClientFactory.getBucket().scope(getScopeName());
    }

    @Bean
    public JsonSerializer getCouchbaseObjectMapper(ClusterEnvironment couchbaseClusterEnvironment) {
        return  couchbaseClusterEnvironment.jsonSerializer();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
