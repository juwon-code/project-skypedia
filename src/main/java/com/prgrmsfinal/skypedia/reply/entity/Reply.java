package com.prgrmsfinal.skypedia.reply.entity;

import java.time.LocalDateTime;
import com.prgrmsfinal.skypedia.member.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Reply {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
	private Member member;

	@ManyToOne
	@JoinColumn(name = "parent_reply_id", referencedColumnName = "id")
	private Reply parentReply;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private boolean updated;

	@Column(nullable = false)
	private boolean removed;

	@Column(insertable = false, updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(insertable = false, updatable = false, nullable = false)
	private LocalDateTime updatedAt;

	private LocalDateTime removedAt;

	@Builder
	public Reply(Member member, String content, Reply parentReply) {
		this.member = member;
		this.parentReply = parentReply;
		this.content = content;
		this.updated = false;
		this.removed = false;
		this.removedAt = null;
	}

	public void update(String content) {
		this.content = content;
		this.updated = true;
	}

	public void remove() {
		this.removed = true;
		this.removedAt = LocalDateTime.now();
	}
}
