package com.prgrmsfinal.skypedia.member.repository;

import com.prgrmsfinal.skypedia.member.dto.MemberRequestDto;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.entity.QMember;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Autowired
    public MemberQueryRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public Optional<Member> findOneBy(MemberRequestDto.SearchOptions options) {
        QMember m = QMember.member;
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (options.memberId() != null) {
            booleanBuilder.and(m.id.eq(options.memberId()));
        }

        if (options.oauthId() != null) {
            booleanBuilder.and(m.oauthId.eq(options.oauthId()));
        }

        if (options.removed() != null) {
            booleanBuilder.and(m.removed.eq(options.removed()));
        }

        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(m)
                        .where(booleanBuilder)
                        .fetchOne()
        );
    }

    public boolean existsByNickname(String nickname) {
        QMember m = QMember.member;

        return jpaQueryFactory
                .selectOne()
                .from(m)
                .where(m.nickname.eq(nickname))
                .fetchFirst() != null;
    }

    public List<Long> deleteAllByCutoff(LocalDateTime cutoff) {
        QMember m = QMember.member;

        List<Long> memberIds = jpaQueryFactory
                .select(m.id)
                .from(m)
                .where(
                    m.removed.isTrue(),
                    m.removedAt.lt(cutoff)
                )
                .fetch();

        jpaQueryFactory
                .delete(m)
                .where(m.id.in(memberIds))
                .execute();

        return memberIds;
    }
}
