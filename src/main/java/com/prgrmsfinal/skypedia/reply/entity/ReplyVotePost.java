package com.prgrmsfinal.skypedia.reply.entity;

import com.prgrmsfinal.skypedia.global.entity.AbstractAssociationEntity;
import com.prgrmsfinal.skypedia.reply.entity.compositekey.ReplyLikesId;
import com.prgrmsfinal.skypedia.votepost.entity.VotePost;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReplyVotePost extends AbstractAssociationEntity<ReplyLikesId, Reply, VotePost> {
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
		super.initializeId(reply, votePost);
		this.reply = reply;
		this.votePost = votePost;
	}

	@Override
	protected ReplyLikesId createId(Reply reply, VotePost votePost) {
		return new ReplyLikesId(reply.getId(), votePost.getId());
	}
}
