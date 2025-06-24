package com.prgrmsfinal.skypedia.member.service;

import com.prgrmsfinal.skypedia.global.constant.RoleType;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.entity.MemberRole;
import com.prgrmsfinal.skypedia.member.exception.MemberRoleException;
import com.prgrmsfinal.skypedia.member.repository.MemberRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberRoleServiceImpl implements MemberRoleService {
    private final MemberRoleRepository memberRoleRepository;

    @Autowired
    public MemberRoleServiceImpl(MemberRoleRepository memberRoleRepository) {
        this.memberRoleRepository = memberRoleRepository;
    }

    @Override
    public List<RoleType> getRolesByMemberId(Long memberId) {
        List<RoleType> roleTypes = memberRoleRepository.findRoleTypesByMemberId(memberId);

        if (roleTypes == null || roleTypes.isEmpty()) {
            throw new MemberRoleException("해당 회원에 역할이 배정되지 않았습니다.", HttpStatus.NOT_FOUND);
        }

        return roleTypes;
    }

    @Override
    public MemberRole save(Member member, RoleType roleType) {
        MemberRole memberRole = MemberRole.builder()
                .member(member)
                .roleType(roleType)
                .build();

        return memberRoleRepository.save(memberRole);
    }
}
