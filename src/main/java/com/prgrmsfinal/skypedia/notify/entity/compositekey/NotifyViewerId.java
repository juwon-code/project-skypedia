package com.prgrmsfinal.skypedia.notify.entity.compositekey;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class NotifyViewerId implements Serializable {
    private Long notifyMessageId;

    private Long memberId;
}
