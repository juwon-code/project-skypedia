package com.prgrmsfinal.skypedia.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class ImageResponseDTO {
	@Schema(title = "사진 정보 조회 DTO", description = "사진 정보 조회 응답에 사용하는 DTO입니다.")
	@Getter
	@Builder
	@AllArgsConstructor
	public static class Info {
		@Schema(title = "사진 ID", description = "사진 ID입니다.", minimum = "1", example = "25")
		private final Long id;

		@Schema(title = "사진 URL", description = "사진 URL입니다.")
		private final String photoUrl;
	}

	@Schema(title = "[응답] 사진 업로드 데이터", description = "사진 업로드 응답에 사용할 데이터의 구조입니다.")
	@Builder
	public record Upload(
		@Schema(title = "파일 UUID", description = "사진 파일명을 대체하는 고유 UUID입니다.")
		String uuid,

		@Schema(title = "업로드 URL", description = "사진 파일을 업로드에 사용되는 일회용 서명 URL입니다.")
		String url
	) {}

	@Schema(title = "[응답] 사진 조회 데이터", description = "사진 조회 응답에 사용할 데이터 구조입니다.")
	@Builder
	public record Read(
		@Schema(title = "전체 파일명", description = "사진 파일명과 확장자를 포함한 전체 파일명입니다.")
		String fullname,

		@Schema(title = "파일 URL", description = "사진 파일 조회에 사용되는 일회용 서명 URL입니다.")
		String url
	) {}
}
