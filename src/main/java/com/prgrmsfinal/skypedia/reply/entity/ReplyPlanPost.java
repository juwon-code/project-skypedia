package com.prgrmsfinal.skypedia.reply.entity;

import com.prgrmsfinal.skypedia.global.entity.AbstractAssociationEntity;
import com.prgrmsfinal.skypedia.planpost.entity.PlanPost;
import com.prgrmsfinal.skypedia.reply.entity.compositekey.ReplyPlanPostId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReplyPlanPost extends AbstractAssociationEntity<ReplyPlanPostId, Reply, PlanPost> {
	@ManyToOne
	@MapsId("replyId")
	@JoinColumn(name = "reply_id", referencedColumnName = "id", nullable = false)
	private Reply reply;

	@ManyToOne
	@MapsId("planPostId")
	@JoinColumn(name = "plan_post_id", referencedColumnName = "id", nullable = false)
	private PlanPost planPost;

	@Builder
	public ReplyPlanPost(Reply reply, PlanPost planPost) {
		super.initializeId(reply, planPost);
		this.reply = reply;
		this.planPost = planPost;
	}

	@Override
	protected ReplyPlanPostId createId(Reply reply, PlanPost planPost) {
		return new ReplyPlanPostId(reply.getId(), planPost.getId());
	}
}
