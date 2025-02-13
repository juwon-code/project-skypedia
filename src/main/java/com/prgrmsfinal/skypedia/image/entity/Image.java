package com.prgrmsfinal.skypedia.image.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Image {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_seq_gen")
	@SequenceGenerator(name = "image_seq_gen", sequenceName = "image_seq", allocationSize = 50)
	private Long id;

	private String uuid;

	private String filename;

	private String extension;

	@Enumerated(EnumType.STRING)
	private PostType postType;

	private Long postContentId;

	@Column(insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(insertable = false, updatable = false)
	private LocalDateTime updatedAt;
}