package com.prgrmsfinal.skypedia.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class AbstractMetricsEntity {
    @Id
    protected Long id;

    @Column(nullable = false)
    protected Long viewCount;

    @Column(nullable = false)
    protected Long likeCount;

    protected AbstractMetricsEntity(Long id) {
        this.id = id;
        this.viewCount = 0L;
        this.likeCount = 0L;
    }

    protected void refresh(Long viewCount, Long likeCount) {
        this.viewCount += viewCount;
        this.likeCount += likeCount;
    }
}
