package com.prgrmsfinal.skypedia.planpost.entity.compositekey;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class PlanPostScrapId implements Serializable {
    private Long planPostId;

    private Long memberId;
}
