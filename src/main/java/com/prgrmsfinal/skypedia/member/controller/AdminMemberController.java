package com.prgrmsfinal.skypedia.member.controller;

import com.prgrmsfinal.skypedia.global.constant.SearchOption;
import com.prgrmsfinal.skypedia.global.constant.SortType;
import com.prgrmsfinal.skypedia.global.dto.CommonResponseDto;
import com.prgrmsfinal.skypedia.global.dto.SearchRequestDto;
import com.prgrmsfinal.skypedia.global.dto.SearchResponseDto;
import com.prgrmsfinal.skypedia.member.dto.MemberResponseDto;
import com.prgrmsfinal.skypedia.member.service.MemberService;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/admin/member")
@RestController
public class AdminMemberController {
    private final MemberService memberService;

    @Autowired
    public AdminMemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<CommonResponseDto> read(@PathVariable @Min(value = 0, message = "회원의 ID 값은 0이상이어야 합니다.") Long memberId) {
        MemberResponseDto.Profile responseDto = memberService.getProfile(memberId);

        return ResponseEntity.ok(CommonResponseDto.ApiSuccess.builder()
                .message("프로필을 성공적으로 조회했습니다.")
                .result(responseDto)
                .build()
        );
    }


    @GetMapping("/search")
    public ResponseEntity<CommonResponseDto> search(
            @RequestParam(required = false, name = "option", defaultValue = "MEMBER_NICKNAME") SearchOption searchOption,
            @RequestParam(required = false, name = "keyword", defaultValue = "") String keyword,
            @RequestParam(required = false, name = "sort", defaultValue = "NEWEST") SortType sortType,
            @RequestParam(required = false, name = "page", defaultValue = "1") int page
    ) {
        SearchRequestDto.Member requestDto = SearchRequestDto.Member.builder()
                .searchOption(searchOption)
                .keyword(keyword)
                .sortType(sortType)
                .page(page)
                .build();

        SearchResponseDto.Pagination<MemberResponseDto.SearchProfile> responseDto = memberService.search(requestDto);

        return ResponseEntity.ok(CommonResponseDto.ApiSuccess.builder()
                .message("데이터를 성공적으로 조회했습니다.")
                .result(responseDto)
                .build()
        );
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<CommonResponseDto> withdraw(@PathVariable @Min(value = 0, message = "회원의 ID 값은 0이상이어야 합니다.") Long memberId) {
        memberService.remove(memberId);

        return ResponseEntity.ok(CommonResponseDto.ApiSuccess.builder()
                .message("해당 회원을 강제탈퇴 처리했습니다.")
                .build()
        );
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<CommonResponseDto> restore(@PathVariable @Min(value = 0, message = "회원의 ID 값은 0이상이어야 합니다.") Long memberId) {
        memberService.restore(memberId);

        return ResponseEntity.ok(CommonResponseDto.ApiSuccess.builder()
                .message("해당 회원을 복구 처리했습니다.")
                .build()
        );
    }

    @PatchMapping("/role/{memberId}")
    public ResponseEntity<CommonResponseDto> grantAdminRole(@PathVariable @Min(value = 0, message = "회원의 ID 값은 0이상이어야 합니다.") Long memberId) {
        memberService.grantAdmin(memberId);

        return ResponseEntity.ok(CommonResponseDto.ApiSuccess.builder()
                .message("해당 회원에 관리자 권한을 부여했습니다.")
                .build()
        );
    }

    @DeleteMapping("/role/{memberId}")
    public ResponseEntity<CommonResponseDto> revokeAdminRole(@PathVariable @Min(value = 0, message = "회원의 ID 값은 0이상이어야 합니다.") Long memberId) {
        memberService.revokeAdmin(memberId);

        return ResponseEntity.ok(CommonResponseDto.ApiSuccess.builder()
                .message("해당 회원의 관리자 권한을 제거했습니다.")
                .build()
        );
    }
}
