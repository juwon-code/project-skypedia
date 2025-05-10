package com.prgrmsfinal.skypedia.votepost.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class VotePostMetrics {
    @Id
    private Long votePostId;

    @Column(nullable = false)
    private Long viewCount;

    @Column(nullable = false)
    private Long likeCount;

    @Builder
    public VotePostMetrics(Long votePostId, Long viewCount, Long likeCount) {
        this.votePostId = votePostId;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
    }

    public void changeMetrics(Long viewCount, Long likeCount) {
        this.viewCount = viewCount;
        this.likeCount = likeCount;
    }
}
