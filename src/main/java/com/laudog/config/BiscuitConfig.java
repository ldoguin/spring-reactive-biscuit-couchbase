package com.laudog.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("biscuit")
@Configuration
@Getter
@Setter
public class BiscuitConfig{
    String publicKey, privateKey;
}
