package com.prgrmsfinal.skypedia.global.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.elasticsearch")
public record ElasticProperties(
        String[] uris
) {
}
