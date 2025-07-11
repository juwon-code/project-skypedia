package com.prgrmsfinal.skypedia.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.prgrmsfinal.skypedia.member.entity.Member;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

}