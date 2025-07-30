package com.prgrmsfinal.skypedia.votepost.entity;

import com.prgrmsfinal.skypedia.global.entity.AbstractMetricsEntity;
import jakarta.persistence.Entity;
import lombok.Builder;

@Entity
public class VotePostMetrics extends AbstractMetricsEntity {
    @Builder
    public VotePostMetrics(Long votePostId) {
        super(votePostId);
    }
}
