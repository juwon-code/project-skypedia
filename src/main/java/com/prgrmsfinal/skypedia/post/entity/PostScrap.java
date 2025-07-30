package com.prgrmsfinal.skypedia.post.entity;

import com.prgrmsfinal.skypedia.global.entity.AbstractAssociationEntity;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.post.entity.compositekey.PostScrapId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PostScrap extends AbstractAssociationEntity<PostScrapId, Post, Member> {
	@ManyToOne(cascade = CascadeType.REMOVE)
	@MapsId("postId")
	@JoinColumn(name = "post_id", referencedColumnName = "id", nullable = false)
	private Post post;

	@ManyToOne(cascade = CascadeType.REMOVE)
	@MapsId("memberId")
	@JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
	private Member member;

	@Builder
	public PostScrap(Post post, Member member) {
		super.initializeId(post, member);
		this.post = post;
		this.member = member;
	}

	@Override
	protected PostScrapId createId(Post post, Member member) {
		return new PostScrapId(post.getId(), member.getId());
	}
}
