package com.prgrmsfinal.skypedia.notify.entity;

import java.time.LocalDateTime;
import com.prgrmsfinal.skypedia.notify.constant.NotifyType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class NotifyMessage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotifyType notifyType;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private String url;

	@Column(nullable = false)
	private boolean activated;

	@Column(insertable = false, updatable = false)
	private LocalDateTime createdAt;

	private LocalDateTime expiredAt;

	@Builder
	public NotifyMessage(NotifyType notifyType, String content, String url) {
		this.notifyType = notifyType;
		this.content = content;
		this.url = url;
		this.activated = true;
	}
}
