package com.prgrmsfinal.skypedia.notify.entity.compositekey;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class NotifyViewerId {
    private Long notifyMessageId;

    private Long memberId;
}
