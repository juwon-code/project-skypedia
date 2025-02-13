package com.prgrmsfinal.skypedia.image.dto;

import com.prgrmsfinal.skypedia.image.entity.PostType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

public class ImageRequestDTO {
	@Schema(title = "[요청] 사진 업로드 데이터", description = "사진 업로드 요청에 사용할 데이터의 구조입니다.")
	@Builder
	public record Upload(
		@Schema(title = "이름", description = "사진의 원본 파일명입니다.", example = "짜빠구리.jpg")
		@Pattern(regexp = "^[\\p{L}\\p{N}_\\-. ]+(\\.jpg|\\.jpeg|\\.png|\\.gif|\\.bmp|\\.webp|\\.svg)$"
			, message = "파일명 또는 확장자가 올바르지 않습니다.")
		String filename,

		@Schema(title = "형식", description = "사진의 파일 형식입니다.", example = "image/jpeg")
		@Pattern(regexp = "^image\\/(jpg|jpeg|png|gif|bmp|webp|svg)$", message = "파일 형식이 올바르지 않습니다.")
		String filetype,

		@Schema(title = "크기", description = "사진 파일의 크기입니다.", example = "5242880")
		@Min(value = 1, message = "파일 크기가 잘못되었거나 비어있습니다.")
		@Max(value = 10485760, message = "10MB 이하의 파일만 업로드할 수 있습니다.")
		Long filesize,

		@Schema(title = "게시글 형식", description = "사진이 속할 게시글의 형식입니다.", example = "post")
		PostType postType,

		@Schema(title = "부모 ID", description = "사진이 속할 게시글의 ID(PK)입니다.", example = "1")
		@Min(value = 1, message = "잘못된 형태의 식별키가 사용되었습니다.")
		Long postContentId
	) {}

	@Schema(title = "[요청] 사진 조회 데이터", description = "사진 조회 요청에 사용할 데이터의 구조입니다.")
	@Builder
	public record Read(
		@Schema(title = "게시글 형식", description = "조회할 사진의 게시글 형식입니다.", example = "post")
		PostType postType,

		@Schema(title = "부모 ID", description = "사진이 속할 게시글의 ID(PK)입니다.", example = "1")
		@Min(value = 1, message = "잘못된 형태의 식별키가 사용되었습니다.")
		Long postContentId
	) {}

	@Schema(title = "[요청] 사진 삭제 데이터", description = "사진 삭제 요청에 사용할 데이터의 구조입니다.")
	@Builder
	public record Delete(
		@Schema(title = "게시글 형식", description = "삭제할 사진의 게시글 형식입니다.", example = "post")
		PostType postType,

		@Schema(title = "부모 ID", description = "삭제할 사진의 게시글의 ID(PK)입니다.", example = "1")
		@Min(value = 1, message = "잘못된 형태의 식별키가 사용되었습니다.")
		Long postContentId
	) {}
}
