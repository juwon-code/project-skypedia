package com.prgrmsfinal.skypedia.global.config;

import com.prgrmsfinal.skypedia.global.constant.ElasticProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
@EnableConfigurationProperties(ElasticProperties.class)
public class ElasticConfig extends ElasticsearchConfiguration {
    private final ElasticProperties elasticProperties;

    public ElasticConfig(ElasticProperties elasticProperties) {
        this.elasticProperties = elasticProperties;
    }


    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticProperties.uris())
                .build();
    }
}
