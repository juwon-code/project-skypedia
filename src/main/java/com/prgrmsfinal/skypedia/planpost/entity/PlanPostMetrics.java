package com.prgrmsfinal.skypedia.planpost.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PlanPostMetrics {
    @Id
    private Long planPostId;

    @Column(nullable = false)
    private Long viewCount;

    @Column(nullable = false)
    private Long likeCount;

    public PlanPostMetrics(Long planPostId) {
        this.planPostId = planPostId;
        this.viewCount = 0L;
        this.likeCount = 0L;
    }

    public void refresh(Long viewCount, Long likeCount) {
        this.viewCount += viewCount;
        this.likeCount += likeCount;
    }
}
