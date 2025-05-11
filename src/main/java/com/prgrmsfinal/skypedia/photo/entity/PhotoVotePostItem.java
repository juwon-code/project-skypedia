package com.prgrmsfinal.skypedia.photo.entity;

import com.prgrmsfinal.skypedia.global.entity.AbstractAssociationEntity;
import com.prgrmsfinal.skypedia.photo.entity.compositekey.PhotoVotePostItemId;
import com.prgrmsfinal.skypedia.votepost.entity.VotePostItem;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PhotoVotePostItem extends AbstractAssociationEntity<PhotoVotePostItemId, Photo, VotePostItem> {
    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("photoId")
    @JoinColumn(name = "photo_id", referencedColumnName = "id", nullable = false)
    private Photo photo;

    @ManyToOne
    @MapsId("votePostItemId")
    @JoinColumn(name = "vote_post_item_id", referencedColumnName = "id", nullable = false)
    private VotePostItem votePostItem;

    @Builder
    public PhotoVotePostItem(Photo photo, VotePostItem votePostItem) {
        super.initializeId(photo, votePostItem);
        this.photo = photo;
        this.votePostItem = votePostItem;
    }

    @Override
    protected PhotoVotePostItemId createId(Photo photo, VotePostItem votePostItem) {
        return new PhotoVotePostItemId(photo.getId(), votePostItem.getId());
    }
}
