package com.prgrmsfinal.skypedia.planpost.entity;

import com.prgrmsfinal.skypedia.global.entity.AbstractMetricsEntity;
import jakarta.persistence.Entity;
import lombok.Builder;

@Entity
public class PlanPostMetrics extends AbstractMetricsEntity {
    @Builder
    public PlanPostMetrics(Long planPostId) {
        super(planPostId);
    }
}
