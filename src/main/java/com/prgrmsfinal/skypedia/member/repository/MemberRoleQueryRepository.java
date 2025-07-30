package com.prgrmsfinal.skypedia.member.repository;

import com.prgrmsfinal.skypedia.global.constant.RoleType;
import com.prgrmsfinal.skypedia.member.entity.QMemberRole;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MemberRoleQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Autowired
    public MemberRoleQueryRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<RoleType> findRoleTypesByMemberId(Long memberId) {
        QMemberRole mr = QMemberRole.memberRole;

        return jpaQueryFactory
                .select(mr.roleType)
                .from(mr)
                .where(mr.member.id.eq(memberId))
                .fetch();
    }

    public boolean existsByMemberIdAndRoleType(Long memberId, RoleType roleType) {
        QMemberRole mr = QMemberRole.memberRole;

        return jpaQueryFactory
                .selectOne()
                .from(mr)
                .where(
                        mr.member.id.eq(memberId),
                        mr.roleType.eq(roleType)
                )
                .fetchFirst() != null;
    }

    public long deleteByMemberIdAndRoleType(Long memberId, RoleType roleType) {
        QMemberRole mr = QMemberRole.memberRole;

        return jpaQueryFactory
                .delete(mr)
                .where(
                        mr.member.id.eq(memberId),
                        mr.roleType.eq(roleType)
                )
                .execute();
    }
}
