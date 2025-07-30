package com.prgrmsfinal.skypedia.post.entity.compositekey;

import java.io.Serializable;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class PostScrapId implements Serializable {
	private Long postId;

	private Long memberId;
}
