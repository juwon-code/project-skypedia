package com.prgrmsfinal.skypedia.planpost.entity.compositekey;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class PlanPostLikesId {
    private Long planPostId;

    private Long memberId;
}
