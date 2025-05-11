package com.prgrmsfinal.skypedia.reply.entity;

import com.prgrmsfinal.skypedia.global.entity.AbstractAssociationEntity;
import com.prgrmsfinal.skypedia.post.entity.Post;
import com.prgrmsfinal.skypedia.reply.entity.compositekey.ReplyLikesId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReplyPost extends AbstractAssociationEntity<ReplyLikesId, Reply, Post> {
	@ManyToOne
	@MapsId("replyId")
	@JoinColumn(name = "reply_id", referencedColumnName = "id", nullable = false)
	private Reply reply;

	@ManyToOne
	@MapsId("postId")
	@JoinColumn(name = "post_id", referencedColumnName = "id", nullable = false)
	private Post post;

	@Builder
	public ReplyPost(Reply reply, Post post) {
		super.initializeId(reply, post);
		this.reply = reply;
		this.post = post;
	}

	@Override
	protected ReplyLikesId createId(Reply reply, Post post) {
		return new ReplyLikesId(reply.getId(), post.getId());
	}
}
