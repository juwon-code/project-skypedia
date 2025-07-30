package com.prgrmsfinal.skypedia.reply.entity.compositekey;

import java.io.Serializable;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ReplyLikesId implements Serializable {
	private Long replyId;

	private Long likerId;
}
