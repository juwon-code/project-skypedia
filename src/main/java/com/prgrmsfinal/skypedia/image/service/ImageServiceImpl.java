package com.prgrmsfinal.skypedia.image.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.cloud.storage.StorageException;
import com.prgrmsfinal.skypedia.image.dto.ImageRequestDTO;
import com.prgrmsfinal.skypedia.image.dto.ImageResponseDTO;
import com.prgrmsfinal.skypedia.image.entity.Image;
import com.prgrmsfinal.skypedia.image.repository.ImageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {
	private final GCSService gcsService;

	private final ImageRepository imageRepository;

	@Override
	@Transactional
	public List<ImageResponseDTO.Upload> uploadImages(List<ImageRequestDTO.Upload> uploadDatas) {
		List<ImageResponseDTO.Upload> uploadResponses = new ArrayList<>();

		List<Image> saveImages = new ArrayList<>();

		uploadDatas.forEach(uploadData -> {
			String uuid = UUID.randomUUID().toString();
			String extension = uploadData.filetype().replace("image/", "");
			String fullname = String.format("%s.%s", uuid, extension);

			try {
				String url = gcsService.getUploadSignedUrl(fullname);

				uploadResponses.add(ImageResponseDTO.Upload.builder()
					.uuid(uuid).url(url).build()
				);

				saveImages.add(Image.builder()
					.uuid(uuid)
					.filename(uploadData.filename())
					.extension(extension)
					.postType(uploadData.postType())
					.postContentId(uploadData.postContentId())
					.build()
				);
			} catch (StorageException e) {
				log.info("File upload failed: Cannot found {} in file storage. Skipping...", fullname);
			}
		});

		if (!saveImages.isEmpty()) {
			imageRepository.saveAll(saveImages);
		}

		return uploadResponses;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ImageResponseDTO.Read> readImages(ImageRequestDTO.Read readData) {
		List<ImageResponseDTO.Read> foundImages = new ArrayList<>();

		List<Image> foundImageDatas = imageRepository
			.findAllByPostTypeAndPostContentId(readData.postType(), readData.postContentId());

		foundImageDatas.forEach(imageData -> {
			String fullname = String.format("%s.%s", imageData.getUuid(), imageData.getExtension());

			try {
				String url = gcsService.getReadSignedUrl(fullname);

				foundImages.add(ImageResponseDTO.Read.builder()
					.fullname(fullname).url(url).build()
				);
			} catch (StorageException e) {
				log.info("File read failed: Cannot found {} in file storage. Skipping...", fullname);
			}
		});

		return foundImages;
	}

	@Override
	@Transactional
	public void deleteImage(List<String> fullnames) {
		try {
			gcsService.deleteStoredImages(fullnames);
		} catch (StorageException e) {
			log.info("File delete failed: 1) GCS authentication or permissions are incorrect. 2) Cannot found that file in file storage.");
		}

		imageRepository.deleteAllByUuids(fullnames.stream()
			.filter(Objects::nonNull)
			.map(fullname -> StringUtils.substringBeforeLast(fullname, "."))
			.toList()
		);
	}

	@Override
	@Transactional
	public void deleteImages(ImageRequestDTO.Delete deleteData) {
		List<String> fullnames = imageRepository
			.findAllByPostTypeAndPostContentId(deleteData.postType(), deleteData.postContentId())
			.stream()
			.filter(Objects::nonNull)
			.map(image -> String.format("%s.%s", image.getUuid(), image.getExtension()))
			.toList();

		deleteImage(fullnames);
	}
}
