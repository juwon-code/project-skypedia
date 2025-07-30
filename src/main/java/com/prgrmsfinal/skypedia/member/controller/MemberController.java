package com.prgrmsfinal.skypedia.member.controller;

import com.prgrmsfinal.skypedia.global.dto.CommonResponseDto;
import com.prgrmsfinal.skypedia.member.dto.MemberRequestDto;
import com.prgrmsfinal.skypedia.member.dto.MemberResponseDto;
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
@RequestMapping("/api/v1/member")
@Slf4j
@Validated
public class MemberController {
	private final MemberService memberService;

	@Autowired
	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}

	@GetMapping
	public ResponseEntity<CommonResponseDto> readMe() {
		MemberResponseDto.Profile responseDto = memberService.getMyProfile();

		return ResponseEntity.ok(CommonResponseDto.ApiSuccess.builder()
				.message("내 프로필을 성공적으로 조회했습니다.")
				.result(responseDto)
				.build()
		);
	}

	@PatchMapping
	public ResponseEntity<CommonResponseDto> modifyMe(@Valid @RequestBody MemberRequestDto.Modify requestDto) {
		MemberResponseDto.Modify responseDto = memberService.modifyMe(requestDto);

		return ResponseEntity.ok(CommonResponseDto.ApiSuccess.builder()
				.message("내 프로필을 성공적으로 업데이트했습니다.")
				.result(responseDto)
				.build()
		);
	}

	@DeleteMapping
	public ResponseEntity<CommonResponseDto> withdrawMe() {
		MemberResponseDto.Remove responseDto = memberService.removeMe();

		return ResponseEntity.ok(CommonResponseDto.ApiSuccess.builder()
				.message("회원이 탈퇴되었습니다.")
				.result(responseDto)
				.build()
		);
	}
}