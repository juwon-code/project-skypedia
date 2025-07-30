package com.prgrmsfinal.skypedia.member.service;

import com.prgrmsfinal.skypedia.global.constant.RoleType;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.entity.MemberRole;
import com.prgrmsfinal.skypedia.member.exception.AlreadyGrantedException;
import com.prgrmsfinal.skypedia.member.exception.CannotRevokeException;
import com.prgrmsfinal.skypedia.member.repository.MemberRoleQueryRepository;
import com.prgrmsfinal.skypedia.member.repository.MemberRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MemberRoleServiceImpl implements MemberRoleService {
    private final MemberRoleRepository memberRoleRepository;
    private final MemberRoleQueryRepository memberRoleQueryRepository;

    @Autowired
    public MemberRoleServiceImpl(MemberRoleRepository memberRoleRepository
            , MemberRoleQueryRepository memberRoleQueryRepository) {
        this.memberRoleRepository = memberRoleRepository;
        this.memberRoleQueryRepository = memberRoleQueryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleType> getRoleTypes(Long memberId) {
        List<RoleType> roles = memberRoleQueryRepository.findRoleTypesByMemberId(memberId);

        if (roles == null || roles.isEmpty()) {
            throw new NoSuchElementException();
        }

        return roles;
    }

    @Override
    @Transactional
    public void create(Member member, RoleType roleType) {
        if (memberRoleQueryRepository.existsByMemberIdAndRoleType(member.getId(), roleType)) {
            throw new AlreadyGrantedException();
        }

        memberRoleRepository.save(MemberRole.builder()
                .member(member)
                .roleType(roleType)
                .build()
        );
    }

    @Override
    @Transactional
    public void delete(Member member, RoleType roleType) {
        if (!memberRoleQueryRepository.existsByMemberIdAndRoleType(member.getId(), roleType)) {
            throw new CannotRevokeException();
        }

        memberRoleQueryRepository.deleteByMemberIdAndRoleType(member.getId(), roleType);
    }
}
