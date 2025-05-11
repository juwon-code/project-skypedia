package com.prgrmsfinal.skypedia.planpost.entity;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.planpost.entity.compositekey.PlanPostLikesId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PlanPostLikes {
    @EmbeddedId
    private PlanPostLikesId id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("planPostId")
    @JoinColumn(name = "plan_post_id", referencedColumnName = "id", nullable = false)
    private PlanPost planPost;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("memberId")
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @Column(insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public PlanPostLikes(PlanPost planPost, Member member) {
        this.id = new PlanPostLikesId(planPost.getId(), member.getId());
        this.planPost = planPost;
        this.member = member;
    }
}
