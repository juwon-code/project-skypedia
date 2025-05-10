package com.prgrmsfinal.skypedia.post.entity;

import java.time.LocalDateTime;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.post.entity.compositekey.PostScrapId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PostScrap {
	@EmbeddedId
	private PostScrapId id;

	@ManyToOne(cascade = CascadeType.REMOVE)
	@MapsId("postId")
	@JoinColumn(name = "post_id", referencedColumnName = "id", nullable = false)
	private Post post;

	@ManyToOne(cascade = CascadeType.REMOVE)
	@MapsId("memberId")
	@JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
	private Member member;

	@Column(insertable = false, updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Builder
	public PostScrap(Post post, Member member) {
		this.id = new PostScrapId(post.getId(), member.getId());
		this.post = post;
		this.member = member;
	}
}
