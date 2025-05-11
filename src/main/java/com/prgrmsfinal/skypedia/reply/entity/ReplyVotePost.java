package com.prgrmsfinal.skypedia.reply.entity;

import com.prgrmsfinal.skypedia.reply.entity.compositekey.ReplyLikesId;
import com.prgrmsfinal.skypedia.votepost.entity.VotePost;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReplyVotePost {
	@EmbeddedId
	private ReplyLikesId id;

	@ManyToOne
	@MapsId("replyId")
	@JoinColumn(name = "reply_id", referencedColumnName = "id", nullable = false)
	private Reply reply;

	@ManyToOne
	@MapsId("votePostId")
	@JoinColumn(name = "vote_post_id", referencedColumnName = "id", nullable = false)
	private VotePost votePost;

	@Builder
	public ReplyVotePost(Reply reply, VotePost votePost) {
		this.id = new ReplyLikesId(reply.getId(), votePost.getId());
		this.reply = reply;
		this.votePost = votePost;
	}
}
