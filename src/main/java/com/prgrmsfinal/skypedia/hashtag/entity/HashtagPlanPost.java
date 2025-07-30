package com.prgrmsfinal.skypedia.hashtag.entity;

import com.prgrmsfinal.skypedia.global.entity.AbstractAssociationEntity;
import com.prgrmsfinal.skypedia.hashtag.entity.compositekey.HashtagPlanPostId;
import com.prgrmsfinal.skypedia.planpost.entity.PlanPost;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class HashtagPlanPost extends AbstractAssociationEntity<HashtagPlanPostId, Hashtag, PlanPost> {
    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("hashtagId")
    @JoinColumn(name = "hashtag_id", referencedColumnName = "id", nullable = false)
    private Hashtag hashtag;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("planPostId")
    @JoinColumn(name = "plan_post_id", referencedColumnName = "id", nullable = false)
    private PlanPost planPost;

    @Builder
    public HashtagPlanPost(Hashtag hashtag, PlanPost planPost) {
        super.initializeId(hashtag, planPost);
        this.hashtag = hashtag;
        this.planPost = planPost;
    }

    @Override
    protected HashtagPlanPostId createId(Hashtag hashtag, PlanPost planPost) {
        return new HashtagPlanPostId(hashtag.getId(), planPost.getId());
    }
}
