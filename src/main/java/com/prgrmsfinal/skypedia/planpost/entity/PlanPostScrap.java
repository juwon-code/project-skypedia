package com.prgrmsfinal.skypedia.planpost.entity;

import com.prgrmsfinal.skypedia.global.entity.AbstractAssociationEntity;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.planpost.entity.compositekey.PlanPostScrapId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PlanPostScrap extends AbstractAssociationEntity<PlanPostScrapId, PlanPost, Member> {
    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("planPostId")
    @JoinColumn(name = "plan_post_id", referencedColumnName = "id", nullable = false)
    private PlanPost planPost;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("memberId")
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @Builder
    public PlanPostScrap(PlanPost planPost, Member member) {
        super.initializeId(planPost, member);
        this.planPost = planPost;
        this.member = member;
    }

    @Override
    protected PlanPostScrapId createId(PlanPost planPost, Member member) {
        return new PlanPostScrapId(planPost.getId(), member.getId());
    }
}
