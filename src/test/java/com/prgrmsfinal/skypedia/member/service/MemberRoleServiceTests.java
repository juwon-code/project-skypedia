package com.prgrmsfinal.skypedia.member.service;

import com.prgrmsfinal.skypedia.global.constant.RoleType;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.entity.MemberRole;
import com.prgrmsfinal.skypedia.member.exception.AlreadyGrantedException;
import com.prgrmsfinal.skypedia.member.exception.CannotRevokeException;
import com.prgrmsfinal.skypedia.member.repository.MemberRoleQueryRepository;
import com.prgrmsfinal.skypedia.member.repository.MemberRoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberRoleServiceTests {
    @Mock
    private MemberRoleRepository memberRoleRepository;

    @Mock
    private MemberRoleQueryRepository memberRoleQueryRepository;

    @InjectMocks
    private MemberRoleServiceImpl memberRoleService;

    @DisplayName("[성공] 회원 역할 목록 조회")
    @Test
    void getRoleTypes_whenValidMemberId_thenReturnRoleList() {
        List<RoleType> mockRoles = List.of(RoleType.USER, RoleType.ADMIN);

        when(memberRoleQueryRepository.findRoleTypesByMemberId(anyLong()))
                .thenReturn(mockRoles);

        List<RoleType> result = memberRoleService.getRoleTypes(1L);

        verify(memberRoleQueryRepository).findRoleTypesByMemberId(1L);
        assertThat(result).hasSize(2).containsExactlyInAnyOrder(RoleType.USER, RoleType.ADMIN);
    }

    @DisplayName("[실패] 회원 역할 목록 조회 :: 조회된 데이터 없음")
    @Test
    void getRoleTypes_whenRoleNotExists_thenThrowException() {
        when(memberRoleQueryRepository.findRoleTypesByMemberId(anyLong()))
                .thenReturn(List.of());

        assertThrows(NoSuchElementException.class, () -> memberRoleService.getRoleTypes(1L));
        verify(memberRoleQueryRepository).findRoleTypesByMemberId(1L);
    }

    @DisplayName("[성공] 회원 역할 부여")
    @Test
    void grant_whenRoleNotExists_thenNotThrowException() {
        Member mockMember = mock(Member.class);
        MemberRole mockRole = mock(MemberRole.class);

        when(mockMember.getId()).thenReturn(1L);
        when(memberRoleQueryRepository.existsByMemberIdAndRoleType(anyLong(), any(RoleType.class)))
                .thenReturn(false);
        when(memberRoleRepository.save(any())).thenReturn(mockRole);

        memberRoleService.create(mockMember,  RoleType.USER);

        verify(memberRoleQueryRepository).existsByMemberIdAndRoleType(anyLong(), any(RoleType.class));
        verify(memberRoleRepository).save(any());
    }

    @DisplayName("[실패] 회원 역할 부여 :: 동일한 역할 있음")
    @Test
    void grant_whenRoleExists_thenThrowException() {
        Member mockMember = mock(Member.class);

        when(mockMember.getId()).thenReturn(1L);
        when(memberRoleQueryRepository.existsByMemberIdAndRoleType(anyLong(), any(RoleType.class)))
                .thenReturn(true);

        assertThrows(AlreadyGrantedException.class, () -> memberRoleService.create(mockMember, RoleType.USER));

        verify(memberRoleQueryRepository).existsByMemberIdAndRoleType(anyLong(), any(RoleType.class));
        verify(memberRoleRepository, never()).save(any(MemberRole.class));
    }

    @DisplayName("[성공] 회원 역할 제거")
    @Test
    void revoke_whenAffectedCountNotZero_thenNotThrowException() {
        Member mockMember = mock(Member.class);

        when(mockMember.getId()).thenReturn(1L);
        when(memberRoleQueryRepository.existsByMemberIdAndRoleType(anyLong(), any(RoleType.class)))
                .thenReturn(true);
        when(memberRoleQueryRepository.deleteByMemberIdAndRoleType(anyLong(), any(RoleType.class)))
                .thenReturn(1L);

        memberRoleService.delete(mockMember, RoleType.USER);

        verify(memberRoleQueryRepository).existsByMemberIdAndRoleType(anyLong(), any(RoleType.class));
        verify(memberRoleQueryRepository).deleteByMemberIdAndRoleType(1L, RoleType.USER);
    }
    
    @DisplayName("[실패] 회원 역할 제거 :: 삭제된 데이터 없음")
    @Test
    void revoke_whenAffectedCountZero_thenThrowException() {
        Member mockMember = mock(Member.class);

        when(mockMember.getId()).thenReturn(1L);
        when(memberRoleQueryRepository.existsByMemberIdAndRoleType(anyLong(), any(RoleType.class)))
                .thenReturn(false);

        assertThrows(CannotRevokeException.class, () -> memberRoleService.delete(mockMember, RoleType.USER));

        verify(memberRoleQueryRepository).existsByMemberIdAndRoleType(anyLong(), any(RoleType.class));
        verify(memberRoleQueryRepository, never()).deleteByMemberIdAndRoleType(anyLong(), any(RoleType.class));
    }
}
