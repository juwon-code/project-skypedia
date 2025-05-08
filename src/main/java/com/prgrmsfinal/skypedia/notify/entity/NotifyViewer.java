package com.prgrmsfinal.skypedia.notify.entity;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.notify.entity.compositekey.NotifyViewerId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class NotifyViewer {
    @Id
    private NotifyViewerId id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("notifyMessageId")
    @JoinColumn(name = "notify_message_id", referencedColumnName = "id", nullable = false)
    private NotifyMessage notifyMessage;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("memberId")
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private boolean viewed;

    @Column(insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime viewedAt;

    public void markAsViewed() {
        this.viewed = true;
        this.viewedAt = LocalDateTime.now();
    }
}
