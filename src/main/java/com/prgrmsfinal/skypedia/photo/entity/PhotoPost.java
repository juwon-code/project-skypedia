package com.prgrmsfinal.skypedia.photo.entity;

import com.prgrmsfinal.skypedia.global.entity.AbstractAssociationEntity;
import com.prgrmsfinal.skypedia.photo.entity.compositekey.PhotoPostId;
import com.prgrmsfinal.skypedia.post.entity.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PhotoPost extends AbstractAssociationEntity<PhotoPostId, Photo, Post> {
    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("photoId")
    @JoinColumn(name = "photo_id", referencedColumnName = "id", nullable = false)
    private Photo photo;

    @ManyToOne
    @MapsId("postId")
    @JoinColumn(name = "post_id", referencedColumnName = "id", nullable = false)
    private Post post;

    @Builder
    public PhotoPost(Photo photo, Post post) {
        super.initializeId(photo, post);
        this.photo = photo;
        this.post = post;
    }

    @Override
    protected PhotoPostId createId(Photo photo, Post post) {
        return new PhotoPostId(photo.getId(), post.getId());
    }
}
