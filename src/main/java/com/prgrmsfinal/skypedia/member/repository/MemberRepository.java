package com.prgrmsfinal.skypedia.member.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prgrmsfinal.skypedia.member.entity.Member;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	@Query("SELECT m FROM Member m WHERE m.id = :memberId")
	Optional<Member> findMemberById(@Param("memberId") Long memberId);

	@Query("SELECT m FROM Member m WHERE m.id = :memberId AND m.removed = :removed")
	Optional<Member> findMemberByIdAndStatus(@Param("memberId") Long memberId, @Param("removed") boolean removed);

	@Query("SELECT m FROM Member m "
			+ "LEFT JOIN FETCH m.memberRoles mr "
			+ "LEFT JOIN FETCH m.photoProfile mp "
			+ "WHERE m.id = :memberId AND m.removed = :removed")
	Optional<Member> findMemberWithRolesAndPhotoByIdAndStatus(@Param("memberId") Long id, @Param("removed") boolean removed);

	@Query("SELECT m FROM Member m "
			+ "LEFT JOIN FETCH m.memberRoles mr "
			+ "LEFT JOIN FETCH m.photoProfile mp "
			+ "WHERE m.oauthId = :oauthId")
	Optional<Member> findMemberWithRolesAndPhotoByOauthId(@Param("oauthId") String oauthId);

	@Query("SELECT m.id FROM Member m WHERE m.removed = true AND m.removedAt <= :cutoff")
	List<Long> findPurgeableMemberIds(@Param("cutoff") LocalDateTime cutoff);

	@Query(value = "SELECT EXISTS (SELECT 1 FROM member WHERE nickname = :nickname)", nativeQuery = true)
	boolean existsByNickname(@Param("nickname") String nickname);

	@Modifying
	@Query("DELETE FROM Member m WHERE m.id IN :memberIds")
	long deleteAllByIds(@Param("memberIds") List<Long> memberIds);
}