package com.prgrmsfinal.skypedia.global.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security-endpoints")
public record SecurityUriProperties(
        String[] publicUri,
        String[] userUri,
        String[] adminUri
) {
}
