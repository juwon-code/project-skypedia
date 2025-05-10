package com.prgrmsfinal.skypedia.post.entity;

import java.time.LocalDateTime;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.post.entity.compositekey.PostLikesId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PostLikes {
	@EmbeddedId
	private PostLikesId id;

	@ManyToOne(cascade = CascadeType.REMOVE)
	@MapsId("postId")
	@JoinColumn(name = "post_id", referencedColumnName = "id", nullable = false)
	private Post post;

	@ManyToOne(cascade = CascadeType.REMOVE)
	@MapsId("memberId")
	@JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
	private Member member;

	@Column(nullable = false, insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Builder
	public PostLikes(Post post, Member member) {
		this.id = new PostLikesId(post.getId(), member.getId());
		this.post = post;
		this.member = member;
	}
}
