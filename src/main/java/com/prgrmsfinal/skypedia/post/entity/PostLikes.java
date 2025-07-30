package com.prgrmsfinal.skypedia.post.entity;

import com.prgrmsfinal.skypedia.global.entity.AbstractAssociationEntity;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.post.entity.compositekey.PostLikesId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PostLikes extends AbstractAssociationEntity<PostLikesId, Post, Member> {
	@ManyToOne(cascade = CascadeType.REMOVE)
	@MapsId("postId")
	@JoinColumn(name = "post_id", referencedColumnName = "id", nullable = false)
	private Post post;

	@ManyToOne(cascade = CascadeType.REMOVE)
	@MapsId("memberId")
	@JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
	private Member member;

	@Builder
	public PostLikes(Post post, Member member) {
		super.initializeId(post, member);
		this.post = post;
		this.member = member;
	}

	@Override
	protected PostLikesId createId(Post post, Member member) {
		return new PostLikesId(post.getId(), member.getId());
	}
}
