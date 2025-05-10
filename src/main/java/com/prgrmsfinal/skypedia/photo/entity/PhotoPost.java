package com.prgrmsfinal.skypedia.photo.entity;

import com.prgrmsfinal.skypedia.photo.entity.compositekey.PhotoPostId;
import com.prgrmsfinal.skypedia.post.entity.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PhotoPost {
    @EmbeddedId
    private PhotoPostId id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("photoId")
    @JoinColumn(name = "photo_id", referencedColumnName = "id", nullable = false)
    private Photo photo;

    @ManyToOne
    @MapsId("postId")
    @JoinColumn(name = "post_id", referencedColumnName = "id", nullable = false)
    private Post post;

    @Column(insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public PhotoPost(Photo photo, Post post) {
        this.id = new PhotoPostId(photo.getId(), post.getId());
        this.photo = photo;
        this.post = post;
    }
}
