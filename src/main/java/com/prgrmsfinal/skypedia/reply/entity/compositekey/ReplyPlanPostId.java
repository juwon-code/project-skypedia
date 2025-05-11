package com.prgrmsfinal.skypedia.reply.entity.compositekey;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ReplyPlanPostId implements Serializable {
	private Long replyId;

	private Long planPostId;
}
