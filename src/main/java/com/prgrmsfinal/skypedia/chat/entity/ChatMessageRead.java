package com.prgrmsfinal.skypedia.chat.entity;

import com.prgrmsfinal.skypedia.chat.entity.compositekey.ChatMessageReadId;
import com.prgrmsfinal.skypedia.global.entity.AbstractAssociationEntity;
import com.prgrmsfinal.skypedia.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatMessageRead extends AbstractAssociationEntity<ChatMessageReadId, ChatMessage, Member> {
    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("chatMessageId")
    @JoinColumn(name = "chat_message_id", referencedColumnName = "id", nullable = false)
    private ChatMessage chatMessage;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("memberId")
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @Builder
    public ChatMessageRead(ChatMessage chatMessage, Member member) {
        super.initializeId(chatMessage, member);
        this.chatMessage = chatMessage;
        this.member = member;
    }

    @Override
    protected ChatMessageReadId createId(ChatMessage chatMessage, Member member) {
        return new ChatMessageReadId(chatMessage.getId(), member.getId());
    }
}
