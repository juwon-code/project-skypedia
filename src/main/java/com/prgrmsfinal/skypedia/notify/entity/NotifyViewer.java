package com.prgrmsfinal.skypedia.notify.entity;

import com.prgrmsfinal.skypedia.global.entity.AbstractAssociationEntity;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.notify.entity.compositekey.NotifyViewerId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class NotifyViewer extends AbstractAssociationEntity<NotifyViewerId, NotifyMessage, Member> {
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

    private LocalDateTime viewedAt;

    @Builder
    public NotifyViewer(NotifyMessage notifyMessage, Member member) {
        super.initializeId(notifyMessage, member);
        this.notifyMessage = notifyMessage;
        this.member = member;
    }

    @Override
    protected NotifyViewerId createId(NotifyMessage notifyMessage, Member member) {
        return new NotifyViewerId(notifyMessage.getId(), member.getId());
    }

    public void markAsViewed() {
        this.viewed = true;
        this.viewedAt = LocalDateTime.now();
    }
}
