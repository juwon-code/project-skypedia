package com.prgrmsfinal.skypedia.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrmsfinal.skypedia.global.constant.SocialType;
import com.prgrmsfinal.skypedia.member.dto.MemberRequestDto;
import com.prgrmsfinal.skypedia.member.dto.MemberResponseDto;
import com.prgrmsfinal.skypedia.member.exception.AlreadyWithdrawnException;
import com.prgrmsfinal.skypedia.member.exception.MemberNotFoundException;
import com.prgrmsfinal.skypedia.member.exception.WithdrawnMemberException;
import com.prgrmsfinal.skypedia.member.service.*;
import com.prgrmsfinal.skypedia.photo.dto.PhotoRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.filter.OncePerRequestFilter;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = MemberController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = {
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = OncePerRequestFilter.class)
})
public class MemberControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @DisplayName("[성공] 내 프로필 조회 API :: 정상 요청 시 사용자 정보를 반환")
    @Test
    void readMe_whenApiCalledSuccessfully_thenReturnUserProfile() throws Exception {
        MemberResponseDto.Profile responseDto = MemberResponseDto.Profile.builder()
                .nickname("nickname")
                .email("email@email.com")
                .socialType(SocialType.GOOGLE)
                .photoUrl("photoUrl")
                .removed(false)
                .build();

        when(memberService.getMyProfile()).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/member"))
            .andExpectAll(
                status().isOk(),
                jsonPath("$.message").value("내 프로필을 성공적으로 조회했습니다."),
                jsonPath("$.result.nickname").value("nickname"),
                jsonPath("$.result.socialType").value("GOOGLE"),
                jsonPath("$.result.photoUrl").value("photoUrl"),
                jsonPath("$.result.removed").value(false)
        );
    }

    @DisplayName("[실패] 내 프로필 조회 API :: 탈퇴 회원이 요청시 에러 메시지 반환")
    @Test
    void readMe_whenMemberInWithdrawnState_thenReturnErrorMessage() throws Exception {
        when(memberService.getMyProfile()).thenThrow(new WithdrawnMemberException());

        mockMvc.perform(get("/api/v1/member"))
            .andExpectAll(
                status().isBadRequest(),
                jsonPath("$.message").value("탈퇴한 회원은 이용할 수 없는 서비스입니다.")
        );
    }

    @DisplayName("[실패] 내 프로필 조회 API :: 회원이 존재하지 않을 경우 에러 메시지 반환")
    @Test
    void readMe_whenMemberNotExists_thenReturnErrorMessage() throws Exception {
        when(memberService.getMyProfile()).thenThrow(new MemberNotFoundException());

        mockMvc.perform(get("/api/v1/member"))
                .andExpectAll(
                    status().isNotFound(),
                    jsonPath("$.message").value("해당 회원이 존재하지 않습니다.")
        );
    }
    
    @DisplayName("[성공] 내 프로필 수정 API :: 사진 및 닉네임 변경시 변경된 닉네임 및 업로드 링크 반환")
    @Test
    void modifyMe_whenApiCalledSuccessfullyWithNicknameAndPhoto_thenReturnChangedNicknameAndUploadUrl() throws Exception {
        PhotoRequestDto.Upload photoData = PhotoRequestDto.Upload.builder()
                .filename("filename.jpg")
                .mediaType("image/jpg")
                .fileSize(1048576L)
                .build();

        MemberRequestDto.Modify requestDto = MemberRequestDto.Modify.builder()
                .nickname("nickname")
                .photoData(photoData)
                .build();

        String requestJson = objectMapper.writeValueAsString(requestDto);

        MemberResponseDto.Modify responseDto = MemberResponseDto.Modify.builder()
                .nickname("nickname")
                .uuid("uuid")
                .uploadUrl("uploadUrl")
                .build();

        when(memberService.modifyMe(requestDto)).thenReturn(responseDto);

        mockMvc.perform(patch("/api/v1/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.message").value("내 프로필을 성공적으로 업데이트했습니다."),
                    jsonPath("$.result.nickname").value("nickname"),
                    jsonPath("$.result.uuid").value("uuid"),
                    jsonPath("$.result.uploadUrl").value("uploadUrl")
                );
    }

    @DisplayName("[실패] 내 프로필 수정 API :: 탈퇴 회원이 요청시 에러 메시지 반환")
    @Test
    void modifyMe_whenMemberInWithdrawnState_thenReturnErrorMessage() throws Exception {
        MemberRequestDto.Modify requestDto = MemberRequestDto.Modify.builder()
                .nickname("nickname")
                .build();

        String requestJson = objectMapper.writeValueAsString(requestDto);

        when(memberService.modifyMe(requestDto)).thenThrow(new WithdrawnMemberException());

        mockMvc.perform(patch("/api/v1/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("탈퇴한 회원은 이용할 수 없는 서비스입니다.")
                );
    }

    @DisplayName("[실패] 내 프로필 조회 API :: 회원이 존재하지 않을 경우 에러 메시지 반환")
    @Test
    void ModifyMe_whenMemberNotExists_thenReturnErrorMessage() throws Exception {
        MemberRequestDto.Modify requestDto = MemberRequestDto.Modify.builder()
                .nickname("nickname")
                .build();

        String requestJson = objectMapper.writeValueAsString(requestDto);

        when(memberService.modifyMe(requestDto)).thenThrow(new MemberNotFoundException());

        mockMvc.perform(patch("/api/v1/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").value("해당 회원이 존재하지 않습니다.")
                );
    }

    @DisplayName("[성공] 회원 탈퇴 API :: 회원 탈퇴 일자 및 영구 탈퇴 일자 반환")
    @Test
    void withdrawMe_whenMemberNotRemoved_thenReturnRemovedDateAndDeletingDate() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        MemberResponseDto.Remove responseDto = MemberResponseDto.Remove.builder()
                .removedAt(now)
                .willDeletedAt(now.plusDays(30))
                .build();

        when(memberService.removeMe()).thenReturn(responseDto);

        String removedAtStr = objectMapper.writeValueAsString(responseDto.removedAt()).replace("\"", "");
        String willDeletedAtStr = objectMapper.writeValueAsString(responseDto.willDeletedAt()).replace("\"", "");

        mockMvc.perform(delete("/api/v1/member"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.message").value("회원이 탈퇴되었습니다."),
                        jsonPath("$.result.removedAt").value(removedAtStr),
                        jsonPath("$.result.willDeletedAt").value(willDeletedAtStr)
                );
    }

    @DisplayName("[실패] 회원 탈퇴 API :: 탈퇴 회원이 요청시 에러 메시지 반환")
    @Test
    void withdrawMe_whenMemberAlreadyRemoved_thenReturnErrorMessage() throws Exception {
        when(memberService.removeMe()).thenThrow(new AlreadyWithdrawnException());

        mockMvc.perform(delete("/api/v1/member"))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("이미 탈퇴한 회원입니다.")
                );
    }

    @DisplayName("[실패] 회원 탈퇴 API :: 회원이 존재하지 않을 경우 에러 메시지 반환")
    @Test
    void withdrawMe_whenMemberNotExists_thenReturnErrorMessage() throws Exception {
        when(memberService.removeMe()).thenThrow(new MemberNotFoundException());

        mockMvc.perform(delete("/api/v1/member"))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").value("해당 회원이 존재하지 않습니다.")
                );
    }
}
