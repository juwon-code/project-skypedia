package com.prgrmsfinal.skypedia.photo.entity;

import com.prgrmsfinal.skypedia.chat.entity.ChatMessage;
import com.prgrmsfinal.skypedia.global.entity.AbstractAssociationEntity;
import com.prgrmsfinal.skypedia.photo.entity.compositekey.PhotoChatMessageId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PhotoChatMessage extends AbstractAssociationEntity<PhotoChatMessageId, Photo, ChatMessage> {
    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("photoId")
    @JoinColumn(name = "photo_id", referencedColumnName = "id", nullable = false)
    private Photo photo;

    @ManyToOne
    @MapsId("chatMessageId")
    @JoinColumn(name = "chat_message_id", referencedColumnName = "id", nullable = false)
    private ChatMessage chatMessage;

    @Builder
    public PhotoChatMessage(Photo photo, ChatMessage chatMessage) {
        super.initializeId(photo, chatMessage);
        this.photo = photo;
        this.chatMessage = chatMessage;
    }

    @Override
    protected PhotoChatMessageId createId(Photo photo, ChatMessage chatMessage) {
        return new PhotoChatMessageId(photo.getId(), chatMessage.getId());
    }
}
