package com.prgrmsfinal.skypedia.chat.entity;

import com.prgrmsfinal.skypedia.chat.entity.compositekey.ChatBlackListId;
import com.prgrmsfinal.skypedia.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatBlackList {
    @EmbeddedId
    private ChatBlackListId id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("memberId")
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("targetId")
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    private Member target;

    @Column(insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public ChatBlackList(Member member, Member target) {
        this.id = new ChatBlackListId(member.getId(), target.getId());
        this.member = member;
        this.target = target;
    }
}
