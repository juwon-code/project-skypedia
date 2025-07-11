package com.prgrmsfinal.skypedia.member.repository;

import com.prgrmsfinal.skypedia.member.entity.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRoleRepository extends JpaRepository<MemberRole, Long> {
}
