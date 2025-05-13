package com.prgrmsfinal.skypedia.post.entity;

import java.time.LocalDateTime;
import com.prgrmsfinal.skypedia.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PostCategory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
	private Member member;

	@Column(length = 100, unique = true, nullable = false)
	private String name;

	@Column(nullable = false)
	private String description;

	@Column(nullable = false, insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Builder
	public PostCategory(Member member, String name, String description) {
		this.member = member;
		this.name = name;
		this.description = description;
	}
}
