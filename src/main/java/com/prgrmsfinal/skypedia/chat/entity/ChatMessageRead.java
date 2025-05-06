package com.prgrmsfinal.skypedia.chat.entity;

import com.prgrmsfinal.skypedia.chat.entity.compositekey.ChatMessageReadId;
import com.prgrmsfinal.skypedia.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatMessageRead {
    @EmbeddedId
    private ChatMessageReadId id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("chatMessageId")
    @JoinColumn(name = "chat_message_id", referencedColumnName = "id")
    private ChatMessage chatMessage;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("memberId")
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @Column(insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public ChatMessageRead(ChatMessage chatMessage, Member member) {
        this.id = new ChatMessageReadId(chatMessage.getId(), member.getId());
        this.chatMessage = chatMessage;
        this.member = member;
    }
}
