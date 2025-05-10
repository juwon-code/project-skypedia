package com.prgrmsfinal.skypedia.votepost.entity.compositekey;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class VotePostScrapId implements Serializable {
    private Long votePostId;

    private Long memberId;
}
