package com.prgrmsfinal.skypedia.chat.entity.compositekey;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ChatBlackListId implements Serializable {
    private Long memberId;

    private Long targetId;
}
