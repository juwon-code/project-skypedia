package com.prgrmsfinal.skypedia.reply.entity;

import java.time.LocalDateTime;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.reply.entity.compositekey.ReplyLikesId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReplyLikes {
	@EmbeddedId
	private ReplyLikesId id;

	@ManyToOne(cascade = CascadeType.REMOVE)
	@MapsId("replyId")
	@JoinColumn(name = "reply_id", referencedColumnName = "id", nullable = false)
	private Reply reply;

	@ManyToOne
	@MapsId("memberId")
	@JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
	private Member member;

	@Column(insertable = false, updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Builder
	public ReplyLikes(Reply reply, Member member) {
		this.id = new ReplyLikesId(reply.getId(), member.getId());
		this.reply = reply;
		this.member = member;
	}
}
