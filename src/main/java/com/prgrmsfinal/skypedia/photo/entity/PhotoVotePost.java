package com.prgrmsfinal.skypedia.photo.entity;

import com.prgrmsfinal.skypedia.photo.entity.compositekey.PhotoVotePostId;
import com.prgrmsfinal.skypedia.votepost.entity.VotePost;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PhotoVotePost {
    @EmbeddedId
    private PhotoVotePostId id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("photoId")
    @JoinColumn(name = "photo_id", referencedColumnName = "id", nullable = false)
    private Photo photo;

    @ManyToOne
    @MapsId("votePostId")
    @JoinColumn(name = "vote_post_id", referencedColumnName = "id", nullable = false)
    private VotePost votePost;

    @Column(insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public PhotoVotePost(Photo photo, VotePost votePost) {
        this.id = new PhotoVotePostId(photo.getId(), votePost.getId());
        this.photo = photo;
        this.votePost = votePost;
    }
}
