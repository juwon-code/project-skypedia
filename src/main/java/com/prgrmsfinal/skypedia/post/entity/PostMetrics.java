package com.prgrmsfinal.skypedia.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PostMetrics {
    @Id
    private Long postId;

    @Column(nullable = false)
    private Long viewCount;

    @Column(nullable = false)
    private Long likeCount;

    @Builder
    public PostMetrics(Long postId, Long viewCount, Long likeCount) {
        this.postId = postId;
        this.viewCount = 0L;
        this.likeCount = 0L;
    }

    public void refresh(Long viewCount, Long likeCount) {
        this.viewCount += viewCount;
        this.likeCount += likeCount;
    }
}
