package com.prgrmsfinal.skypedia.votepost.entity;

import java.time.LocalDateTime;
import com.prgrmsfinal.skypedia.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class VotePost {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
	private Member member;

	@Column(nullable = false)
	private boolean ended;

	@Column(nullable = false)
	private boolean removed;

	@Column(nullable = false, insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false, insertable = false, updatable = false)
	private LocalDateTime updatedAt;

	@Column(nullable = false)
	private LocalDateTime endedAt;

	private LocalDateTime removedAt;

	@Builder
	public VotePost(Member member, LocalDateTime endedAt) {
		if (LocalDateTime.now().isAfter(endedAt)) {
			throw new IllegalArgumentException("투표일시를 과거로 설정할 수 없습니다.");
		}

		this.member	= member;
		this.ended = false;
		this.removed = false;
		this.endedAt = endedAt;
		this.removedAt = null;
	}

	public void changeEndDate(LocalDateTime endedAt) {
		if (LocalDateTime.now().isAfter(endedAt)) {
			throw new IllegalArgumentException("투표일시를 과거로 설정할 수 없습니다.");
		}

		this.endedAt = endedAt;
	}

	public void remove() {
		this.removed = true;
		this.removedAt = LocalDateTime.now();
	}
}