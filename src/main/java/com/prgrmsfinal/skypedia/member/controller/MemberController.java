package com.prgrmsfinal.skypedia.member.controller;

import com.prgrmsfinal.skypedia.global.constant.SearchOption;
import com.prgrmsfinal.skypedia.global.constant.SortType;
import com.prgrmsfinal.skypedia.global.dto.CommonResponseDto;
import com.prgrmsfinal.skypedia.global.dto.SearchRequestDto;
import com.prgrmsfinal.skypedia.global.dto.SearchResponseDto;
import com.prgrmsfinal.skypedia.member.entity.MemberDocument;
import com.prgrmsfinal.skypedia.member.dto.MemberRequestDto;
import com.prgrmsfinal.skypedia.member.dto.MemberResponseDto;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.prgrmsfinal.skypedia.member.service.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "회원 API 컨트롤러", description = "회원과 관련된 REST API를 제공하는 컨트롤러")
@RestController
@RequestMapping("/api/v1")
@Slf4j
@Validated
public class MemberController {
	private final MemberService memberService;

	@Autowired
	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}

	@GetMapping("/member")
	public ResponseEntity<CommonResponseDto> readMyProfile() {
		MemberResponseDto.ReadProfile responseDto = memberService.getMyProfile();

		return ResponseEntity.ok(CommonResponseDto.ApiSuccess.builder()
				.message("내 프로필을 성공적으로 조회했습니다.")
				.result(responseDto)
				.build()
		);
	}

	@PatchMapping("/member")
	public ResponseEntity<CommonResponseDto> updateMyProfile(@Valid @RequestBody MemberRequestDto.ChangeProfile dto) {
		MemberResponseDto.ChangeProfile responseDto = memberService.changeMyProfile(dto);

		return ResponseEntity.ok(CommonResponseDto.ApiSuccess.builder()
				.message("내 프로필을 성공적으로 업데이트했습니다.")
				.result(responseDto)
				.build()
		);
	}

	@DeleteMapping("/member")
	public ResponseEntity<CommonResponseDto> withdrawMe() {
		MemberResponseDto.Withdraw responseDto = memberService.removeMe();

		return ResponseEntity.ok(CommonResponseDto.ApiSuccess.builder()
				.message("회원이 탈퇴되었습니다.")
				.result(responseDto)
				.build()
		);
	}

	@GetMapping("/admin/member/{memberId}")
	public ResponseEntity<CommonResponseDto> read(@PathVariable @Min(value = 0, message = "회원의 ID 값은 0이상이어야 합니다.") Long memberId) {
		return null;
	}


	@GetMapping("/admin/member/search")
	public ResponseEntity<CommonResponseDto> search(
			@RequestParam(required = false, name = "option", defaultValue = "MEMBER_NICKNAME") SearchOption searchOption,
			@RequestParam(required = false, name = "keyword", defaultValue = "") String keyword,
			@RequestParam(required = false, name = "sort", defaultValue = "newest") SortType sortType,
			@RequestParam(required = false, name = "page", defaultValue = "1") int page
			) {
		SearchRequestDto.Member requestDto = SearchRequestDto.Member.builder()
				.searchOption(searchOption)
				.keyword(keyword)
				.sortType(sortType)
				.page(page)
				.build();

		SearchResponseDto.Pagination<MemberDocument> responseDto = memberService.search(requestDto);

		return ResponseEntity.ok(CommonResponseDto.ApiSuccess.builder()
				.message("데이터를 성공적으로 조회했습니다.")
				.result(responseDto)
				.build()
		);
	}

	@PatchMapping("/admin/member/{memberId}/role")
	public ResponseEntity<CommonResponseDto> updateRoles(@PathVariable @Min(value = 0, message = "회원의 ID 값은 0이상이어야 합니다.") Long memberId
			, @Valid @RequestBody MemberRequestDto.ChangeRole requestDto) {
		String message = memberService.changeRole(memberId, requestDto);

		return ResponseEntity.ok(CommonResponseDto.ApiSuccess.builder()
				.message(message)
				.build()
		);
	}

	@DeleteMapping("/admin/member/{memberId}")
	public ResponseEntity<CommonResponseDto> withdraw(@PathVariable @Min(value = 0, message = "회원의 ID 값은 0이상이어야 합니다.") Long memberId) {
		memberService.remove(memberId);

		return ResponseEntity.ok(CommonResponseDto.ApiSuccess.builder()
				.message("해당 회원을 강제탈퇴 처리했습니다.")
				.build()
		);
	}

	@PatchMapping("/admin/member/{memberId}/reset")
	public ResponseEntity<CommonResponseDto> reset(@PathVariable @Min(value = 0, message = "회원의 ID 값은 0이상이어야 합니다.") Long memberId) {
		memberService.reset(memberId);

		return ResponseEntity.ok(CommonResponseDto.ApiSuccess.builder()
				.message("해당 회원의 프로필을 초기화했습니다.")
				.build()
		);
	}

	@PatchMapping("/admin/member/{memberId}/restore")
	public ResponseEntity<CommonResponseDto> restore(@PathVariable @Min(value = 0, message = "회원의 ID 값은 0이상이어야 합니다.") Long memberId) {
		memberService.restore(memberId);

		return ResponseEntity.ok(CommonResponseDto.ApiSuccess.builder()
				.message("해당 회원을 복구 처리했습니다.")
				.build()
		);
	}


}