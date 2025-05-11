package com.prgrmsfinal.skypedia.photo.entity;

import com.prgrmsfinal.skypedia.global.entity.AbstractAssociationEntity;
import com.prgrmsfinal.skypedia.photo.entity.compositekey.PhotoVotePostId;
import com.prgrmsfinal.skypedia.votepost.entity.VotePost;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PhotoVotePost extends AbstractAssociationEntity<PhotoVotePostId, Photo, VotePost> {
    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("photoId")
    @JoinColumn(name = "photo_id", referencedColumnName = "id", nullable = false)
    private Photo photo;

    @ManyToOne
    @MapsId("votePostId")
    @JoinColumn(name = "vote_post_id", referencedColumnName = "id", nullable = false)
    private VotePost votePost;

    @Builder
    public PhotoVotePost(Photo photo, VotePost votePost) {
        super.initializeId(photo, votePost);
        this.photo = photo;
        this.votePost = votePost;
    }

    @Override
    protected PhotoVotePostId createId(Photo photo, VotePost votePost) {
        return new PhotoVotePostId(photo.getId(), votePost.getId());
    }
}
