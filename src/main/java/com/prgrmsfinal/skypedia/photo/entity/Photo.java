package com.prgrmsfinal.skypedia.photo.entity;

import java.time.LocalDateTime;
import com.prgrmsfinal.skypedia.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Photo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
	private Member member;

	@Column(unique = true, nullable = false)
	private String uuid;

	@Column(nullable = false)
	private String filename;

	@Column(nullable = false)
	private String extension;

	@Column(nullable = false)
	private boolean removed;

	@Column(insertable = false, updatable = false, nullable = false)
	private LocalDateTime createdAt;

	private LocalDateTime removedAt;

	@Builder
	public Photo(Member member, String uuid, String filename, String extension) {
		this.member = member;
		this.uuid = uuid;
		this.filename = filename;
		this.extension = extension;
		this.removed = false;
		this.removedAt = null;
	}

	public void remove() {
		this.removed = true;
		this.removedAt = LocalDateTime.now();
	}
}