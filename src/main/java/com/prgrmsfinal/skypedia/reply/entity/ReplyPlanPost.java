package com.prgrmsfinal.skypedia.reply.entity;

import com.prgrmsfinal.skypedia.planpost.entity.PlanPost;
import com.prgrmsfinal.skypedia.reply.entity.compositekey.ReplyPlanPostId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReplyPlanPost {
	@EmbeddedId
	private ReplyPlanPostId id;

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
		this.id = new ReplyPlanPostId(reply.getId(), planPost.getId());
		this.reply = reply;
		this.planPost = planPost;
	}
}
