package com.prgrmsfinal.skypedia.member.service;

import com.prgrmsfinal.skypedia.global.constant.RoleType;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.entity.MemberRole;

import java.util.List;

public interface MemberRoleService {
    List<RoleType> getRolesByMemberId(Long memberId);

    MemberRole save(Member member, RoleType roleType);
}
