package com.prgrmsfinal.skypedia.reply.entity;

import com.prgrmsfinal.skypedia.post.entity.Post;
import com.prgrmsfinal.skypedia.reply.entity.compositekey.ReplyLikesId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReplyPost {
	@EmbeddedId
	private ReplyLikesId id;

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
		this.id = new ReplyLikesId(reply.getId(), post.getId());
		this.reply = reply;
		this.post = post;
	}
}
