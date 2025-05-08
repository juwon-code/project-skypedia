package com.prgrmsfinal.skypedia.member.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String oauthId;

	@Column(nullable = false)
	private String name;

	@Column(length = 20, unique = true, nullable = false)
	private String username;

	@Column(length = 50, unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private boolean removed;

	@Column(insertable = false, updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(insertable = false, updatable = false, nullable = false)
	private LocalDateTime updatedAt;

	private LocalDateTime removedAt;

	@Builder
	public Member(String oauthId, String name, String username, String email) {
		this.oauthId = oauthId;
		this.name = name;
		this.username = username;
		this.email = email;
		this.removed = false;
		this.removedAt = null;
	}

	public void remove() {
		this.removed = true;
		this.removedAt = LocalDateTime.now();
	}

	public void changeUsername(String username) {
		this.username = username;
	}
}
