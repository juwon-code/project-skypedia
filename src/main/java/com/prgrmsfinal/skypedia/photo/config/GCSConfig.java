package com.prgrmsfinal.skypedia.photo.config;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Configuration
public class GCSConfig {
	@Value("${spring.cloud.gcp.storage.credentials.location}")
	private String keyLocation;

	@Bean
	public Storage storage() throws IOException {
		InputStream googleCredentialsStream = ResourceUtils.getURL(keyLocation).openStream();

		return StorageOptions.newBuilder()
			.setCredentials(GoogleCredentials.fromStream(googleCredentialsStream))
			.build()
			.getService();
	}
}
