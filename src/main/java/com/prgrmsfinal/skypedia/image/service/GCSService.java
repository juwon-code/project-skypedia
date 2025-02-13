package com.prgrmsfinal.skypedia.image.service;

import java.util.List;

public interface GCSService {
	String getUploadSignedUrl(String fullname);

	String getReadSignedUrl(String fullname);

	void deleteStoredImages(List<String> fullnames);
}
