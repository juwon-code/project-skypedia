package com.prgrmsfinal.skypedia.member.controller;

import com.prgrmsfinal.skypedia.global.constant.SocialType;
import com.prgrmsfinal.skypedia.global.dto.SearchRequestDto;
import com.prgrmsfinal.skypedia.global.dto.SearchResponseDto;
import com.prgrmsfinal.skypedia.global.exception.SearchNotFoundException;
import com.prgrmsfinal.skypedia.member.dto.MemberResponseDto;
import com.prgrmsfinal.skypedia.member.exception.*;
import com.prgrmsfinal.skypedia.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.filter.OncePerRequestFilter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = AdminMemberController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = OncePerRequestFilter.class)
        })
public class AdminMemberControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @DisplayName("[성공] 회원 프로필 조회 :: ID와 일치하는 회원의 프로필 조회")
    @Test
    void read_whenMemberExists_thenReturnProfile() throws Exception {
        MemberResponseDto.Profile responseDto = MemberResponseDto.Profile.builder()
                .nickname("nickname")
                .email("email@email.com")
                .socialType(SocialType.GOOGLE)
                .photoUrl("photoUrl")
                .removed(false)
                .build();

        when(memberService.getProfile(anyLong())).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/admin/member/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.message").value("프로필을 성공적으로 조회했습니다."),
                        jsonPath("$.result.nickname").value("nickname"),
                        jsonPath("$.result.socialType").value("GOOGLE"),
                        jsonPath("$.result.photoUrl").value("photoUrl"),
                        jsonPath("$.result.removed").value(false)
                );
    }

    @DisplayName("[실패] 회원 프로필 조회 :: 회원이 존재하지 않을 경우 에러 메시지 반환")
    @Test
    void read_whenMemberNotExists_thenReturnErrorMessage() throws Exception {
        when(memberService.getProfile(anyLong())).thenThrow(new MemberNotFoundException());

        mockMvc.perform(get("/api/v1/admin/member/1"))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").value("해당 회원이 존재하지 않습니다.")
                );
    }

    @DisplayName("[성공] 회원 목록 검색 :: 닉네임으로 회원 검색")
    @Test
    void search_whenValidRequest_thenReturnMemberList() throws Exception {
        List<MemberResponseDto.SearchProfile> datas = new ArrayList<>();

        IntStream.rangeClosed(1, 10).forEach(i -> {
            MemberResponseDto.SearchProfile data = MemberResponseDto.SearchProfile.builder()
                    .id((long) i)
                    .nickname("nickname" + i)
                    .email("email" + i + "@email.com")
                    .socialType(SocialType.KAKAO.name())
                    .photoUrl("photoUrl" + i)
                    .createdAt(LocalDateTime.now())
                    .build();

            datas.add(data);
        });

        SearchResponseDto.Pagination<MemberResponseDto.SearchProfile> responseDto = SearchResponseDto.Pagination.<MemberResponseDto.SearchProfile>builder()
                .page(0)
                .totalPages(1)
                .totalCount(10)
                .isFirst(true)
                .isLast(true)
                .data(datas)
                .build();

        when(memberService.search(any(SearchRequestDto.Member.class))).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/admin/member/search?option=MEMBER_NICKNAME&keyword=nickname"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.message").value("데이터를 성공적으로 조회했습니다."),
                        jsonPath("$.result.page").value(0),
                        jsonPath("$.result.totalCount").value(10),
                        jsonPath("$.result.isFirst").value(true),
                        jsonPath("$.result.isLast").value(true),
                        jsonPath("$.result.data[0].nickname").value("nickname1"),
                        jsonPath("$.result.data[9].nickname").value("nickname10")
                );
    }

    @DisplayName("[실패] 회원 목록 검색 :: 잘못된 검색 파라미터")
    @Test
    void search_whenInvalidQueryParameter_thenReturnErrorMessage() throws Exception {
        mockMvc.perform(get("/api/v1/admin/member/search?option=WRONG_PARAMETER&keyword=nickname"))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("요청 타입이 잘못되었습니다."),
                        jsonPath("$.details.option").value("'WRONG_PARAMETER'는 SearchOption 타입으로 변환할 수 없습니다.")
                );
    }

    @DisplayName("[실패] 회원 목록 검색 :: 검색 결과 없음")
    @Test
    void search_whenResultNotExists_thenReturnErrorMessage() throws Exception {
        when(memberService.search(any(SearchRequestDto.Member.class)))
                .thenThrow(new SearchNotFoundException());

        mockMvc.perform(get("/api/v1/admin/member/search"))
                .andExpectAll(
                        status().isNoContent(),
                        jsonPath("$.message").value("검색 결과가 존재하지 않습니다.")
                );
    }

    @DisplayName("[성공] 회원 강제 탈퇴 :: 회원을 강제탈퇴 처리함")
    @Test
    void withdraw_whenMemberExists_thenReturnSuccessMessage() throws Exception {
        MemberResponseDto.Remove mockDto = mock(MemberResponseDto.Remove.class);

        when(memberService.removeMe()).thenReturn(mockDto);

        mockMvc.perform(delete("/api/v1/admin/member/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.message").value("해당 회원을 강제탈퇴 처리했습니다.")
                );
    }

    @DisplayName("[실패] 회원 강제 탈퇴 :: 회원이 존재하지 않음")
    @Test
    void withdraw_whenMemberNotExists_thenReturnErrorMessage() throws Exception {
        when(memberService.remove(anyLong())).thenThrow(new MemberNotFoundException());

        mockMvc.perform(delete("/api/v1/admin/member/1"))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").value("해당 회원이 존재하지 않습니다.")
                );
    }
    
    @DisplayName("[실패] 회원 강제 탈퇴 :: 회원이 이미 삭제됨")
    @Test
    void withdraw_whenMemberAlreadyRemoved_thenReturnErrorMessage() throws Exception {
        when(memberService.remove(anyLong())).thenThrow(new AlreadyWithdrawnException());

        mockMvc.perform(delete("/api/v1/admin/member/1"))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("이미 탈퇴한 회원입니다.")
                );
    }

    @DisplayName("[성공] 회원 복구 :: 삭제한 회원의 탈퇴를 취소함")
    @Test
    void restore_whenRemovedMemberExists_thenReturnSuccessMessage() throws Exception {
        doNothing().when(memberService).restore(anyLong());

        mockMvc.perform(patch("/api/v1/admin/member/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.message").value("해당 회원을 복구 처리했습니다.")
                );
    }

    @DisplayName("[실패] 회원 복구 :: 탈퇴하지 않은 회원임")
    @Test
    void restore_whenMemberNotRemoved_thenReturnErrorMessage() throws Exception {
        doThrow(new CannotRestoreMemberException()).when(memberService).restore(anyLong());

        mockMvc.perform(patch("/api/v1/admin/member/1"))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("탈퇴하지 않은 회원은 복구할 수 없습니다.")
                );
    }

    @DisplayName("[실패] 회원 복구 :: 회원이 존재하지 않음")
    @Test
    void restore_whenMemberNotExists_thenReturnErrorMessage() throws Exception {
        doThrow(new MemberNotFoundException()).when(memberService).restore(anyLong());

        mockMvc.perform(patch("/api/v1/admin/member/1"))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").value("해당 회원이 존재하지 않습니다.")
                );
    }

    @DisplayName("[성공] 관리자 권한 부여 :: 회원에게 관리자 권한을 부여함")
    @Test
    void grantAdminRole_whenMemberHasNotAdminRole_thenReturnSuccessMessage() throws Exception {
        doNothing().when(memberService).grantAdmin(anyLong());

        mockMvc.perform(patch("/api/v1/admin/member/role/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.message").value("해당 회원에 관리자 권한을 부여했습니다.")
                );
    }

    @DisplayName("[실패] 관리자 권한 부여 :: 회원이 존재하지 않음")
    @Test
    void grantAdminRole_whenMemberNotExists_thenReturnErrorMessage() throws Exception {
        doThrow(new MemberNotFoundException()).when(memberService).grantAdmin(anyLong());

        mockMvc.perform(patch("/api/v1/admin/member/role/1"))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").value("해당 회원이 존재하지 않습니다.")
                );
    }

    @DisplayName("[실패] 관리자 권한 부여 :: 이미 관리자 권한 부여됨")
    @Test
    void grantAdminRole_whenMemberHasAdminRole_thenReturnErrorMessage() throws Exception {
        doThrow(new AlreadyGrantedException()).when(memberService).grantAdmin(anyLong());

        mockMvc.perform(patch("/api/v1/admin/member/role/1"))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("해당 회원에게 이미 부여된 역할입니다.")
                );
    }

    @DisplayName("[성공] 관리자 권한 제거 :: 회원에게서 관리자 권한을 제거함")
    @Test
    void revokeAdminRole_whenMemberHasAdminRole_thenReturnSuccessMessage() throws Exception {
        doNothing().when(memberService).revokeAdmin(anyLong());

        mockMvc.perform(delete("/api/v1/admin/member/role/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.message").value("해당 회원의 관리자 권한을 제거했습니다.")
                );
    }
    
    @DisplayName("[실패] 관리자 권한 제거 :: 회원이 존재하지 않음")
    @Test
    void revokeAdminRole_whenMemberNotExists_thenReturnErrorMessage() throws Exception {
        doThrow(new MemberNotFoundException()).when(memberService).revokeAdmin(anyLong());

        mockMvc.perform(delete("/api/v1/admin/member/role/1"))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").value("해당 회원이 존재하지 않습니다.")
                );
    }

    @DisplayName("[실패] 관리자 권한 제거 :: 관리자 권한을 갖고있지 않음")
    @Test
    void revokeAdminRole_whenMemberHasNotAdminRole_thenReturnErrorMessage() throws Exception {
        doThrow(new CannotRevokeException()).when(memberService).revokeAdmin(anyLong());

        mockMvc.perform(delete("/api/v1/admin/member/role/1"))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").value("제거할 역할이 존재하지 않습니다.")
                );
    }
}
