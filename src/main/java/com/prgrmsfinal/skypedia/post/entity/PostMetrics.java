package com.prgrmsfinal.skypedia.post.entity;

import com.prgrmsfinal.skypedia.global.entity.AbstractMetricsEntity;
import jakarta.persistence.Entity;
import lombok.Builder;

@Entity
public class PostMetrics extends AbstractMetricsEntity {
    @Builder
    public PostMetrics(Long postId) {
        super(postId);
    }
}
