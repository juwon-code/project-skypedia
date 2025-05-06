package com.prgrmsfinal.skypedia.hashtag.entity.key;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class HashtagPlanPostId implements Serializable {
    private Long hashtagId;

    private Long planPostId;
}
