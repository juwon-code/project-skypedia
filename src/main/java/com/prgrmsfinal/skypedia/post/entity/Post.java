package com.prgrmsfinal.skypedia.post.entity;

import java.time.LocalDateTime;
import com.prgrmsfinal.skypedia.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
	private Member member;

	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "post_category_id", referencedColumnName = "id", nullable = false)
	private PostCategory postCategory;

	@Column(length = 50, nullable = false)
	private String title;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private boolean removed;

	@Column(nullable = false, insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false, insertable = false, updatable = false)
	private LocalDateTime updatedAt;

	private LocalDateTime removedAt;

	@Builder
	public Post(Member member, PostCategory postCategory, String title, String content) {
		this.member = member;
		this.postCategory = postCategory;
		this.title = title;
		this.content = content;
		this.removed = false;
		this.removedAt = null;
	}

	public void modify(String title, String content) {
		this.title = title;
		this.content = content;
	}

	public void remove() {
		this.removed = true;
		this.removedAt = LocalDateTime.now();
	}
}
