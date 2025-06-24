package com.prgrmsfinal.skypedia.member.repository;

import com.prgrmsfinal.skypedia.global.constant.RoleType;
import com.prgrmsfinal.skypedia.member.entity.MemberRole;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRoleRepository extends JpaRepository<MemberRole, Long> {
    @Query("SELECT mr.roleType FROM MemberRole mr WHERE mr.member.id = :memberId")
    List<RoleType> findRoleTypesByMemberId(@Param("memberId") Long memberId);
}
