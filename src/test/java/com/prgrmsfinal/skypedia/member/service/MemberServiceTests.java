package com.prgrmsfinal.skypedia.member.service;

import com.prgrmsfinal.skypedia.global.constant.RoleType;
import com.prgrmsfinal.skypedia.global.constant.SearchOption;
import com.prgrmsfinal.skypedia.global.constant.SocialType;
import com.prgrmsfinal.skypedia.global.constant.SortType;
import com.prgrmsfinal.skypedia.global.dto.SearchRequestDto;
import com.prgrmsfinal.skypedia.global.dto.SearchResponseDto;
import com.prgrmsfinal.skypedia.global.exception.SearchNotFoundException;
import com.prgrmsfinal.skypedia.member.dto.MemberRequestDto;
import com.prgrmsfinal.skypedia.member.dto.MemberResponseDto;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.entity.MemberDocument;
import com.prgrmsfinal.skypedia.member.entity.MemberRole;
import com.prgrmsfinal.skypedia.member.exception.*;
import com.prgrmsfinal.skypedia.member.repository.MemberDocumentRepository;
import com.prgrmsfinal.skypedia.member.repository.MemberQueryRepository;
import com.prgrmsfinal.skypedia.member.repository.MemberRepository;
import com.prgrmsfinal.skypedia.member.util.SecurityUtil;
import com.prgrmsfinal.skypedia.photo.dto.PhotoRequestDto;
import com.prgrmsfinal.skypedia.photo.dto.PhotoResponseDto;
import com.prgrmsfinal.skypedia.photo.entity.PhotoMember;
import com.prgrmsfinal.skypedia.photo.service.PhotoMemberSerivceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTests {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberQueryRepository memberQueryRepository;

    @Mock
    private MemberDocumentRepository memberDocumentRepository;

    @Mock
    private MemberRoleServiceImpl memberRoleService;

    @Mock
    private PhotoMemberSerivceImpl photoProfileService;

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private JwtTokenServiceImpl jwtTokenService;

    @InjectMocks
    private MemberServiceImpl memberService;

    @DisplayName("[성공] 회원 등록")
    @Test
    void create_whenValidRequest_thenReturnInfo() throws Exception {
        MemberRequestDto.SocialInfo socialInfo = MemberRequestDto.SocialInfo.builder()
                .oauthId("oauthId")
                .name("name")
                .email("email@email.com")
                .socialType(SocialType.GOOGLE)
                .build();

        Member savedMember = Member.builder()
                .oauthId("oauthId")
                .name("name")
                .nickname("nickname")
                .email("email@email.com")
                .socialType(SocialType.GOOGLE)
                .build();

        Field idField = Member.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(savedMember, 1L);

        when(memberRepository.save(any(Member.class)))
                .thenReturn(savedMember);
        doNothing().when(memberDocumentRepository)
                .save(any(MemberDocument.class));

        MemberResponseDto.Info result = memberService.create(socialInfo);

        assertEquals(1L, result.id());
        assertEquals("nickname", result.nickname());
        assertEquals(List.of(RoleType.USER), result.roleTypes());
        assertNull(result.photoUrl());

        verify(memberRepository).save(any(Member.class));
    }

    @DisplayName("[성공] 회원 정보 조회 (인증 ID)")
    @Test
    void getInfo_whenValidOauthId_thenReturnInfo() throws Exception {
        List<MemberRole> memberRoles = List.of(MemberRole.builder()
                .roleType(RoleType.USER)
                .build()
        );

        Member foundMember = Member.builder()
                .nickname("nickname")
                .roles(memberRoles)
                .build();

        Field idField = Member.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(foundMember, 1L);

        when(memberQueryRepository.findOneBy(any(MemberRequestDto.SearchOptions.class)))
                .thenReturn(Optional.of(foundMember));
        when(photoProfileService.getReadUrl(anyLong())).thenReturn("photoUrl");

        MemberResponseDto.Info result = memberService.getInfo("oauthId");

        assertEquals(1L, result.id());
        assertEquals("nickname", result.nickname());
        assertEquals(List.of(RoleType.USER), result.roleTypes());
        assertEquals("photoUrl", result.photoUrl());

        verify(memberQueryRepository).findOneBy(any(MemberRequestDto.SearchOptions.class));
        verify(photoProfileService).getReadUrl(anyLong());
    }

    @DisplayName("[실패] 회원 정보 조회 (인증 ID) :: 회원이 존재하지 않음")
    @Test
    void getInfo_whenMemberNotExists_thenThrowException() {
        when(memberQueryRepository.findOneBy(any(MemberRequestDto.SearchOptions.class)))
                .thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.getInfo("oauthId"));

        verify(memberQueryRepository).findOneBy(any(MemberRequestDto.SearchOptions.class));
        verify(photoProfileService, never()).getReadUrl(anyLong());
    }

    @DisplayName("[실패] 회원 정보 조회 (인증 ID) :: 탈퇴된 회원임")
    @Test
    void getInfo_whenMemberRemoved_thenThrowException() {
        Member foundMember = Member.builder().build();
        foundMember.setRemoved(true);

        when(memberQueryRepository.findOneBy(any(MemberRequestDto.SearchOptions.class)))
                .thenReturn(Optional.of(foundMember));

        assertThrows(WithdrawnMemberException.class, () -> memberService.getInfo("oauthId"));

        verify(memberQueryRepository).findOneBy(any(MemberRequestDto.SearchOptions.class));
        verify(photoProfileService, never()).getReadUrl(anyLong());
    }

    @DisplayName("[성공] 내 프로필 조회")
    @Test
    void getMyProfile_whenAuthenticatedMemberExists_thenReturnProfile() throws Exception {
        Member foundMember = Member.builder()
                .nickname("nickname")
                .email("email")
                .socialType(SocialType.GOOGLE)
                .build();

        Field field = Member.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(foundMember, 1L);

        when(securityUtil.getCurrentMemberId()).thenReturn(1L);
        when(memberQueryRepository.findOneBy(any(MemberRequestDto.SearchOptions.class)))
                .thenReturn(Optional.of(foundMember));
        when(photoProfileService.getReadUrl(anyLong()))
                .thenReturn("photoUrl");

        MemberResponseDto.Profile result = memberService.getMyProfile();

        assertEquals("nickname", result.nickname());
        assertEquals("email", result.email());
        assertEquals(SocialType.GOOGLE, result.socialType());
        assertEquals("photoUrl", result.photoUrl());

        verify(securityUtil).getCurrentMemberId();
        verify(memberQueryRepository).findOneBy(any(MemberRequestDto.SearchOptions.class));
        verify(photoProfileService).getReadUrl(anyLong());
    }

    @DisplayName("[실패] 내 프로필 조회 :: 유효하지 않은 회원정보")
    @Test
    void getMyProfile_whenSecurityContextHolderEmpty_thenThrowsException() {
        when(securityUtil.getCurrentMemberId()).thenThrow(NotAuthenticatedException.class);

        assertThrows(NotAuthenticatedException.class, () -> memberService.getMyProfile());

        verify(securityUtil).getCurrentMemberId();
        verify(memberQueryRepository, never()).findOneBy(any(MemberRequestDto.SearchOptions.class));
        verify(photoProfileService, never()).getReadUrl(anyLong());
    }

    @DisplayName("[실패] 내 프로필 조회 :: 탈퇴된 회원임")
    @Test
    void getMyProfile_whenWithdrawnMember_thenThrowsException() throws Exception {
        Member foundMember = Member.builder()
                .nickname("nickname")
                .email("email")
                .socialType(SocialType.GOOGLE)
                .build();

        foundMember.setRemoved(true);

        Field field = Member.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(foundMember, 1L);

        when(securityUtil.getCurrentMemberId()).thenReturn(1L);
        when(memberQueryRepository.findOneBy(any(MemberRequestDto.SearchOptions.class)))
                .thenReturn(Optional.of(foundMember));
        when(photoProfileService.getReadUrl(anyLong()))
                .thenReturn("photoUrl");

        assertThrows(WithdrawnMemberException.class, () -> memberService.getMyProfile());

        verify(securityUtil).getCurrentMemberId();
        verify(memberQueryRepository).findOneBy(any(MemberRequestDto.SearchOptions.class));
        verify(photoProfileService).getReadUrl(anyLong());
    }

    @DisplayName("[성공] 회원 프로필 조회 (ID)")
    @Test
    void getProfile_whenValidMemberId_thenReturnProfile() throws Exception {
        Member foundMember = Member.builder()
                .nickname("nickname")
                .email("email")
                .socialType(SocialType.GOOGLE)
                .build();

        Field field = Member.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(foundMember, 1L);

        when(memberQueryRepository.findOneBy(any(MemberRequestDto.SearchOptions.class)))
                .thenReturn(Optional.of(foundMember));
        when(photoProfileService.getReadUrl(anyLong()))
                .thenReturn("photoUrl");

        MemberResponseDto.Profile result = memberService.getProfile(1L);

        assertEquals("nickname", result.nickname());
        assertEquals("email", result.email());
        assertEquals(SocialType.GOOGLE, result.socialType());
        assertEquals("photoUrl", result.photoUrl());

        verify(memberQueryRepository).findOneBy(any(MemberRequestDto.SearchOptions.class));
        verify(photoProfileService).getReadUrl(anyLong());
    }

    @DisplayName("[실패] 회원 프로필 조회 :: 회원이 존재하지 않음")
    @Test
    void getProfile_whenMemberNotExists_thenThrowsException() throws Exception {
        when(memberQueryRepository.findOneBy(any(MemberRequestDto.SearchOptions.class)))
                .thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.getProfile(1L));

        verify(memberQueryRepository).findOneBy(any(MemberRequestDto.SearchOptions.class));
        verify(photoProfileService, never()).getReadUrl(anyLong());
    }

    @DisplayName("[성공] 회원 목록 검색")
    @Test
    void search_whenValidRequest_thenReturnPaginationResult() {
        SearchRequestDto.Member requestDto = SearchRequestDto.Member.builder()
                .keyword("nickname")
                .searchOption(SearchOption.MEMBER_NICKNAME)
                .sortType(SortType.NEWEST)
                .page(0)
                .build();

        MemberDocument memberDocument = MemberDocument.builder()
                .id(1L)
                .nickname("nickname")
                .email("email@email.com")
                .socialType(SocialType.KAKAO.toString())
                .roles(List.of(RoleType.USER.name()))
                .createdAt(LocalDateTime.now())
                .removedAt(null)
                .build();

        SearchHit<MemberDocument> mockSearchHit = mock(SearchHit.class);
        when(mockSearchHit.getContent()).thenReturn(memberDocument);

        SearchHits<MemberDocument> mockSearchHits = mock(SearchHits.class);
        when(mockSearchHits.hasSearchHits()).thenReturn(true);
        when(mockSearchHits.get()).thenReturn(Stream.of(mockSearchHit));
        when(mockSearchHits.getTotalHits()).thenReturn(1L);
        when(mockSearchHits.getSearchHits()).thenReturn(List.of(mockSearchHit));

        when(memberDocumentRepository.findBy(anyString(), any(SearchOption.class), any(SortType.class), any(Pageable.class)))
                .thenReturn(mockSearchHits);
        when(photoProfileService.getReadUrls(anyList())).thenReturn(Map.of(1L, "photoUrl"));

        SearchResponseDto.Pagination<MemberResponseDto.SearchProfile> result = memberService.search(requestDto);

        assertEquals(0, result.page());
        assertEquals(1, result.totalPages());
        assertEquals(1, result.totalCount());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());
        assertEquals("nickname", result.data().get(0).nickname());
        assertEquals("photoUrl", result.data().get(0).photoUrl());

        verify(memberDocumentRepository).findBy(anyString(), any(SearchOption.class), any(SortType.class), any(Pageable.class));
        verify(photoProfileService).getReadUrls(anyList());
    }

    @DisplayName("[실패] 회원 목록 검색 :: 검색 결과 없음")
    @Test
    void search_whenNoResult_thenThrowsException() {
        SearchRequestDto.Member requestDto = SearchRequestDto.Member.builder()
                .keyword("nickname")
                .searchOption(SearchOption.MEMBER_NICKNAME)
                .sortType(SortType.NEWEST)
                .page(0)
                .build();

        SearchHits<MemberDocument> mockSearchHits = mock(SearchHits.class);
        when(mockSearchHits.hasSearchHits()).thenReturn(false);
        when(memberDocumentRepository.findBy(anyString(), any(SearchOption.class), any(SortType.class), any(Pageable.class)))
                .thenReturn(mockSearchHits);

        assertThrows(SearchNotFoundException.class, () -> memberService.search(requestDto));

        verify(memberDocumentRepository).findBy(anyString(), any(SearchOption.class), any(SortType.class), any(Pageable.class));
        verify(photoProfileService, never()).getReadUrls(anyList());
    }

    @DisplayName("[성공] 내 프로필 수정")
    @Test
    void modifyMe_whenAuthenticatedMemberExists_thenReturnResponse() {
        MemberRequestDto.Modify modifyData = MemberRequestDto.Modify.builder()
                .nickname("nickname")
                .build();

        Member mockMember = mock(Member.class);
        when(mockMember.isRemoved()).thenReturn(false);

        MemberDocument mockDocument = mock(MemberDocument.class);

        when(securityUtil.getCurrentMemberId())
                .thenReturn(1L);
        when(memberQueryRepository.findOneBy(any(MemberRequestDto.SearchOptions.class)))
                .thenReturn(Optional.of(mockMember));
        when(memberQueryRepository.existsByNickname(anyString()))
                .thenReturn(false);
        when(memberDocumentRepository.get(anyLong()))
                .thenReturn(mockDocument);
        doNothing().when(memberDocumentRepository).save(any(MemberDocument.class));

        MemberResponseDto.Modify result = memberService.modifyMe(modifyData);

        assertEquals(modifyData.nickname(), result.nickname());

        verify(memberDocumentRepository).save(any(MemberDocument.class));
        verify(photoProfileService, never()).upload(any(Member.class), any(PhotoRequestDto.Upload.class));
    }

    @DisplayName("[실패] 내 프로필 수정 :: 유효하지 않은 회원정보")
    @Test
    void modifyMe_whenSecurityContextHolderEmpty_thenThrowsException() {
        MemberRequestDto.Modify modifyData = mock(MemberRequestDto.Modify.class);

        when(securityUtil.getCurrentMemberId())
                .thenThrow(NotAuthenticatedException.class);

        assertThrows(NotAuthenticatedException.class, () -> memberService.modifyMe(modifyData));

        verify(securityUtil).getCurrentMemberId();
    }

    @DisplayName("[성공] 프로필 수정")
    @Test
    void modify_whenValidRequest_thenReturnResponse() throws Exception {
        PhotoRequestDto.Upload photoData = PhotoRequestDto.Upload.builder()
                .filename("filename.jpg")
                .mediaType("image/jpg")
                .fileSize(150000L)
                .build();

        MemberRequestDto.Modify modifyData = MemberRequestDto.Modify.builder()
                .nickname("nickname")
                .photoData(photoData)
                .build();

        Member mockMember = mock(Member.class);
        when(mockMember.isRemoved()).thenReturn(false);

        MemberDocument mockDocument = mock(MemberDocument.class);
        PhotoMember mockPhotoMember = mock(PhotoMember.class);

        PhotoResponseDto.Upload<PhotoMember> uploadData = PhotoResponseDto.Upload.<PhotoMember>builder()
                .uuid("uuid")
                .uploadUrl("uploadUrl")
                .photoMember(mockPhotoMember)
                .build();

        when(memberQueryRepository.findOneBy(any(MemberRequestDto.SearchOptions.class)))
                .thenReturn(Optional.of(mockMember));
        when(memberQueryRepository.existsByNickname(anyString()))
                .thenReturn(false);
        when(memberDocumentRepository.get(anyLong()))
                .thenReturn(mockDocument);
        when(photoProfileService.upload(any(Member.class), any(PhotoRequestDto.Upload.class)))
                .thenReturn(uploadData);
        doNothing().when(memberDocumentRepository).save(any(MemberDocument.class));

        MemberResponseDto.Modify result = memberService.modify(1L, modifyData);

        assertEquals("nickname", result.nickname());
        assertEquals("uuid", result.uuid());
        assertEquals("uploadUrl", result.uploadUrl());

        verify(memberDocumentRepository).save(any(MemberDocument.class));
        verify(photoProfileService).upload(any(Member.class), any(PhotoRequestDto.Upload.class));
    }

    @DisplayName("[실패] 프로필 수정 :: 회원이 존재하지 않음")
    @Test
    void modify_whenMemberNotExists_thenThrowsException() {
        MemberRequestDto.Modify modifyData = MemberRequestDto.Modify.builder()
                .nickname("nickname")
                .photoData(null)
                .build();

        when(memberQueryRepository.findOneBy(any(MemberRequestDto.SearchOptions.class)))
                .thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.modify(1L, modifyData));
    }
    
    @DisplayName("[실패] 프로필 수정 :: 탈퇴한 회원임")
    @Test
    void modify_whenMemberWithdrawn_thenThrowsException() {
        MemberRequestDto.Modify modifyData = MemberRequestDto.Modify.builder()
                .nickname("nickname")
                .photoData(null)
                .build();

        Member foundMember = Member.builder().build();
        foundMember.setRemoved(true);

        when(memberQueryRepository.findOneBy(any(MemberRequestDto.SearchOptions.class)))
                .thenReturn(Optional.of(foundMember));

        assertThrows(WithdrawnMemberException.class, () -> memberService.modify(1L, modifyData));
    }

    @DisplayName("[실패] 프로필 수정 :: 동일한 닉네임이 존재함")
    @Test
    void modify_whenNicknameConflict_thenThrowsException() {
        MemberRequestDto.Modify modifyData = MemberRequestDto.Modify.builder()
                .nickname("nickname")
                .build();

        Member mockMember = mock(Member.class);
        when(mockMember.isRemoved()).thenReturn(false);

        when(memberQueryRepository.findOneBy(any(MemberRequestDto.SearchOptions.class)))
                .thenReturn(Optional.of(mockMember));
        when(memberQueryRepository.existsByNickname(anyString()))
                .thenReturn(true);

        assertThrows(NicknameConflictException.class, () -> memberService.modify(1L, modifyData));
    }

    @DisplayName("[성공] 회원 삭제 (본인)")
    @Test
    void removeMe_whenMemberExists_thenReturnResponse() throws Exception {
        Member member =  Member.builder().build();

        Field idField = Member.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(member, 1L);

        MemberDocument mockDocument = mock(MemberDocument.class);

        when(securityUtil.getCurrentMemberId())
                .thenReturn(1L);
        when(memberQueryRepository.findOneBy(any(MemberRequestDto.SearchOptions.class)))
                .thenReturn(Optional.of(member));
        when(memberDocumentRepository.get(anyLong()))
                .thenReturn(mockDocument);
        doNothing().when(jwtTokenService).deleteRefreshToken(anyLong());
        doNothing().when(memberDocumentRepository).save(any(MemberDocument.class));

        MemberResponseDto.Remove result = memberService.removeMe();

        assertTrue(member.isRemoved());
        assertEquals(member.getRemovedAt(), result.removedAt());

        verify(securityUtil).getCurrentMemberId();
        verify(memberQueryRepository).findOneBy(any(MemberRequestDto.SearchOptions.class));
        verify(jwtTokenService).deleteRefreshToken(anyLong());
        verify(memberDocumentRepository).get(anyLong());
        verify(memberDocumentRepository).save(any(MemberDocument.class));
    }

    @DisplayName("[실패] 회원 삭제 (본인) :: 유효하지 않은 인증")
    @Test
    void removeMe_whenNotAuthenticated_thenThrowsException() {
        when(securityUtil.getCurrentMemberId())
                .thenThrow(NotAuthenticatedException.class);

        assertThrows(NotAuthenticatedException.class, () -> memberService.removeMe());

        verify(securityUtil).getCurrentMemberId();
        verify(memberQueryRepository, never()).findOneBy(any(MemberRequestDto.SearchOptions.class));
    }

    @DisplayName("[실패] 회원 삭제 (본인) :: 이미 탈퇴한 회원")
    @Test
    void removeMe_whenAlreadyRemoved_thenThrowsException() {
        Member member = Member.builder().build();
        member.setRemoved(true);

        when(securityUtil.getCurrentMemberId())
                .thenReturn(1L);
        when(memberQueryRepository.findOneBy(any(MemberRequestDto.SearchOptions.class)))
                .thenReturn(Optional.of(member));

        assertThrows(WithdrawnMemberException.class, () -> memberService.removeMe());

        verify(securityUtil).getCurrentMemberId();
        verify(memberQueryRepository).findOneBy(any(MemberRequestDto.SearchOptions.class));
        verify(jwtTokenService, never()).deleteRefreshToken(anyLong());
    }

    @DisplayName("[성공] 회원 삭제")
    @Test
    void remove_whenMemberExists_thenReturnResponse() throws Exception {
        Member member = Member.builder().build();
        MemberDocument mockDocument = mock(MemberDocument.class);

        Field idField = Member.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(member, 1L);

        when(memberQueryRepository.findOneBy(any(MemberRequestDto.SearchOptions.class)))
                .thenReturn(Optional.of(member));
        when(memberDocumentRepository.get(anyLong()))
                .thenReturn(mockDocument);
        doNothing().when(memberDocumentRepository).save(any(MemberDocument.class));
        doNothing().when(jwtTokenService).deleteRefreshToken(anyLong());

        MemberResponseDto.Remove result = memberService.remove(1L);

        assertTrue(member.isRemoved());
        assertEquals(member.getRemovedAt(), result.removedAt());

        verify(memberQueryRepository).findOneBy(any(MemberRequestDto.SearchOptions.class));
        verify(jwtTokenService).deleteRefreshToken(anyLong());
    }

    @DisplayName("[실패] 회원 삭제 :: 존재하지 않는 회원")
    @Test
    void remove_whenMemberNotExists_thenThrowsException() {
        when(memberQueryRepository.findOneBy(any(MemberRequestDto.SearchOptions.class)))
                .thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.remove(1L));

        verify(memberQueryRepository).findOneBy(any(MemberRequestDto.SearchOptions.class));
        verify(jwtTokenService, never()).deleteRefreshToken(anyLong());
    }

    @DisplayName("[실패] 회원 삭제 :: 이미 탈퇴한 회원")
    @Test
    void remove_whenAlreadyRemoved_thenThrowsException() {
        Member member = Member.builder().build();
        member.setRemoved(true);

        when(memberQueryRepository.findOneBy(any(MemberRequestDto.SearchOptions.class)))
                .thenReturn(Optional.of(member));

        assertThrows(WithdrawnMemberException.class, () -> memberService.remove(1L));

        verify(memberQueryRepository).findOneBy(any(MemberRequestDto.SearchOptions.class));
        verify(jwtTokenService, never()).deleteRefreshToken(anyLong());
    }

    @DisplayName("[성공] 탈퇴 회원 복구")
    @Test
    void restore_whenMemberIsRemoved_thenRestoreSuccessfully() {
        Member member = Member.builder().build();
        member.setRemoved(true);
        member.setRemovedAt(LocalDateTime.now());

        MemberDocument mockDocument = mock(MemberDocument.class);

        when(memberQueryRepository.findOneBy(any(MemberRequestDto.SearchOptions.class)))
                .thenReturn(Optional.of(member));
        when(memberDocumentRepository.get(anyLong()))
                .thenReturn(mockDocument);
        doNothing().when(memberDocumentRepository).save(any(MemberDocument.class));

        memberService.restore(1L);

        verify(memberQueryRepository).findOneBy(any(MemberRequestDto.SearchOptions.class));
        verify(memberDocumentRepository).get(anyLong());
        verify(memberDocumentRepository).save(any(MemberDocument.class));
    }

    @DisplayName("[실패] 탈퇴 회원 복구 :: 존재하지 않는 회원 ID")
    @Test
    void restore_whenMemberNotExists_thenThrowsException() {
        when(memberQueryRepository.findOneBy(any(MemberRequestDto.SearchOptions.class)))
                .thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.restore(1L));

        verify(memberQueryRepository).findOneBy(any(MemberRequestDto.SearchOptions.class));
        verify(memberDocumentRepository, never()).get(anyLong());
        verify(memberDocumentRepository, never()).save(any(MemberDocument.class));
    }

    @DisplayName("[실패] 탈퇴 회원 복구 :: 탈퇴되지 않은 회원")
    @Test
    void restore_whenMemberNotRemoved_thenThrowsException() {
        Member member = Member.builder().build();

        when(memberQueryRepository.findOneBy(any(MemberRequestDto.SearchOptions.class)))
                .thenReturn(Optional.of(member));

        assertThrows(CannotRestoreMemberException.class, () -> memberService.restore(1L));

        verify(memberQueryRepository).findOneBy(any(MemberRequestDto.SearchOptions.class));
        verify(memberDocumentRepository, never()).get(anyLong());
        verify(memberDocumentRepository, never()).save(any(MemberDocument.class));
    }

    @DisplayName("[성공] 탈퇴 회원 삭제")
    @Test
    void deleteRemovedMembers_whenExpired_thenDeleteAll() {
        List<Long> mockIds = List.of(1L, 2L, 3L);

        when(memberQueryRepository.deleteAllByCutoff(any(LocalDateTime.class)))
                .thenReturn(mockIds);
        doNothing().when(memberDocumentRepository).deleteAll(mockIds);

        memberService.deleteRemovedMembers();

        verify(memberQueryRepository).deleteAllByCutoff(any(LocalDateTime.class));
        verify(memberDocumentRepository).deleteAll(mockIds);
    }

    @DisplayName("[성공] 관리자 권한 부여")
    @Test
    void grantAdmin_whenActiveMemberExists_thenGrantAdmin() {
        Member mockMember = mock(Member.class);
        MemberDocument mockMemberDocument = mock(MemberDocument.class);

        when(memberQueryRepository.findOneBy(any(MemberRequestDto.SearchOptions.class)))
                .thenReturn(Optional.of(mockMember));
        when(memberDocumentRepository.get(anyLong()))
                .thenReturn(mockMemberDocument);
        doNothing().when(memberRoleService).create(any(Member.class), any(RoleType.class));
        doNothing().when(memberDocumentRepository).save(any(MemberDocument.class));

        memberService.grantAdmin(1L);

        verify(memberRoleService).create(any(Member.class), any(RoleType.class));
        verify(memberDocumentRepository).save(any(MemberDocument.class));
    }

    @DisplayName("[실패] 관리자 권한 부여 :: 회원이 존재하지 않음")
    @Test
    void grantAdmin_whenMemberNotExists_thenThrowsException() {
        when(memberQueryRepository.findOneBy(any(MemberRequestDto.SearchOptions.class)))
                .thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.grantAdmin(1L));

        verify(memberRoleService, never()).create(any(Member.class), any(RoleType.class));
        verify(memberDocumentRepository, never()).save(any(MemberDocument.class));
    }
    
    @DisplayName("[실패] 관리자 권한 부여 :: 이미 권한 부여됨")
    @Test
    void grantAdmin_whenMemberAlreadyGranted_thenThrowsException() {
        Member mockMember = mock(Member.class);

        when(memberQueryRepository.findOneBy(any(MemberRequestDto.SearchOptions.class)))
                .thenReturn(Optional.of(mockMember));
        doThrow(AlreadyGrantedException.class).when(memberRoleService).create(any(Member.class), any(RoleType.class));

        assertThrows(AlreadyGrantedException.class, () -> memberService.grantAdmin(1L));

        verify(memberRoleService).create(any(Member.class), any(RoleType.class));
        verify(memberDocumentRepository, never()).save(any(MemberDocument.class));
    }

    @DisplayName("[성공] 관리자 권한 제거")
    @Test
    void revokeAdmin_whenAdminMemberExists_thenRevokeAdmin() {
        Member mockMember = mock(Member.class);
        MemberDocument mockMemberDocument = mock(MemberDocument.class);

        when(memberQueryRepository.findOneBy(any(MemberRequestDto.SearchOptions.class)))
                .thenReturn(Optional.of(mockMember));
        when(memberDocumentRepository.get(anyLong()))
                .thenReturn(mockMemberDocument);
        doNothing().when(memberRoleService).delete(any(Member.class), any(RoleType.class));
        doNothing().when(memberDocumentRepository).save(any(MemberDocument.class));

        memberService.revokeAdmin(1L);

        verify(memberRoleService).delete(any(Member.class), any(RoleType.class));
        verify(memberDocumentRepository).save(any(MemberDocument.class));
    }

    @DisplayName("[실패] 관리자 권한 제거 :: 회원이 존재하지 않음")
    @Test
    void revokeAdmin_whenAdminMemberExists_thenThrowsException() {
        when(memberQueryRepository.findOneBy(any(MemberRequestDto.SearchOptions.class)))
                .thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.revokeAdmin(1L));

        verify(memberRoleService, never()).delete(any(Member.class), any(RoleType.class));
        verify(memberDocumentRepository, never()).save(any(MemberDocument.class));
    }

    @DisplayName("[실패] 관리자 권한 제거 :: 관리자 권한 없음")
    @Test
    void revokeAdmin_whenHasNotAdminRole_thenThrowsException() {
        Member mockMember = mock(Member.class);

        when(memberQueryRepository.findOneBy(any(MemberRequestDto.SearchOptions.class)))
                .thenReturn(Optional.of(mockMember));
        doThrow(CannotRevokeException.class).when(memberRoleService).delete(any(Member.class), any(RoleType.class));

        assertThrows(CannotRevokeException.class, () -> memberService.revokeAdmin(1L));

        verify(memberRoleService).delete(any(Member.class), any(RoleType.class));
        verify(memberDocumentRepository, never()).save(any(MemberDocument.class));
    }
}
