package com.prgrmsfinal.skypedia.reply.entity;

import com.prgrmsfinal.skypedia.global.entity.AbstractAssociationEntity;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.reply.entity.compositekey.ReplyLikesId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReplyLikes extends AbstractAssociationEntity<ReplyLikesId, Reply, Member> {
	@ManyToOne(cascade = CascadeType.REMOVE)
	@MapsId("replyId")
	@JoinColumn(name = "reply_id", referencedColumnName = "id", nullable = false)
	private Reply reply;

	@ManyToOne
	@MapsId("memberId")
	@JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
	private Member member;

	@Builder
	public ReplyLikes(Reply reply, Member member) {
		super.initializeId(reply, member);
		this.reply = reply;
		this.member = member;
	}

	@Override
	protected ReplyLikesId createId(Reply reply, Member member) {
		return new ReplyLikesId(reply.getId(), member.getId());
	}
}
