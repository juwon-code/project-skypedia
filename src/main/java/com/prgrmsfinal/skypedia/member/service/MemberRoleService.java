package com.prgrmsfinal.skypedia.member.service;

import com.prgrmsfinal.skypedia.global.constant.RoleType;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.entity.MemberRole;

import java.util.List;

public interface MemberRoleService {
    List<RoleType> getRoleTypes(Long memberId);

    void create(Member member, RoleType roleType);

    void delete(Member member, RoleType roleType);
}
