package com.prgrmsfinal.skypedia.hashtag.entity;

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
public class HashtagPlanPost {
    @EmbeddedId
    private HashtagPlanPostId id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("hashtagId")
    @JoinColumn(name = "hashtag_id", referencedColumnName = "id", nullable = false)
    private Hashtag hashtag;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("planPostId")
    @JoinColumn(name = "plan_post_id", referencedColumnName = "id", nullable = false)
    private PlanPost planPost;

    @Column(insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public HashtagPlanPost(Hashtag hashtag, PlanPost planPost) {
        this.id = new HashtagPlanPostId(hashtag.getId(), planPost.getId());
        this.hashtag = hashtag;
        this.planPost = planPost;
    }
}
