package com.prgrmsfinal.skypedia.photo.entity;

import com.prgrmsfinal.skypedia.chat.entity.ChatMessage;
import com.prgrmsfinal.skypedia.photo.entity.compositekey.PhotoChatMessageId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PhotoChatMessage {
    @EmbeddedId
    private PhotoChatMessageId id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("photoId")
    @JoinColumn(name = "photo_id", referencedColumnName = "id", nullable = false)
    private Photo photo;

    @ManyToOne
    @MapsId("chatMessageId")
    @JoinColumn(name = "chat_message_id", referencedColumnName = "id", nullable = false)
    private ChatMessage chatMessage;

    @Column(insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public PhotoChatMessage(Photo photo, ChatMessage chatMessage) {
        this.id = new PhotoChatMessageId(photo.getId(), chatMessage.getId());
        this.photo = photo;
        this.chatMessage = chatMessage;
    }
}
