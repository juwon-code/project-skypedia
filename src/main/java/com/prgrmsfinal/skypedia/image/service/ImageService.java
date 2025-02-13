package com.prgrmsfinal.skypedia.image.service;

import java.util.List;

import com.prgrmsfinal.skypedia.image.dto.ImageRequestDTO;
import com.prgrmsfinal.skypedia.image.dto.ImageResponseDTO;

public interface ImageService {
	List<ImageResponseDTO.Upload> uploadImages(List<ImageRequestDTO.Upload> uploadDatas);

	List<ImageResponseDTO.Read> readImages(ImageRequestDTO.Read readData);

	void deleteImage(List<String> fullnames);

	void deleteImages(ImageRequestDTO.Delete deleteData);
}
