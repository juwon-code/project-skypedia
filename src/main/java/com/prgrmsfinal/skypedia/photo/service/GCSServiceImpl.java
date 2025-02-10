package com.prgrmsfinal.skypedia.photo.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GCSServiceImpl implements GCSService {
	@Value("${spring.cloud.gcp.storage.bucket-name}")
	private String bucket;

	private final Storage storage;

	@Override
	public String getUploadSignedUrl(String fullname) {
		BlobInfo blobInfo = BlobInfo.newBuilder(bucket, fullname).build();

		return storage.signUrl(
			blobInfo, 5, TimeUnit.MINUTES,
			Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
			Storage.SignUrlOption.withV4Signature()
		).toString();
	}

	@Override
	public String getReadSignedUrl(String fullname) {
		BlobId blobId = BlobId.of(bucket, fullname);

		return storage.signUrl(
			storage.get(blobId), 5, TimeUnit.MINUTES,
			Storage.SignUrlOption.withV4Signature()
		).toString();
	}

	@Override
	public void deleteStoredImages(List<String> fullnames) {
		List<BlobId> blobIds = fullnames.stream()
			.map(filename -> BlobId.of(bucket, filename))
			.toList();

		storage.delete(blobIds);
	}
}
