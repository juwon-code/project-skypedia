package com.prgrmsfinal.skypedia.reply.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReplyMetrics {
    @Id
    private Long id;

    @Column(nullable = false)
    private Long likeCount;

    public ReplyMetrics(Long id) {
        this.id = id;
        this.likeCount = 0L;
    }

    public void refresh(Long likeCount) {
        this.likeCount += likeCount;
    }
}
