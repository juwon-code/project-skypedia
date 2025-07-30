package com.prgrmsfinal.skypedia.photo.entity;

import com.prgrmsfinal.skypedia.global.entity.AbstractAssociationEntity;
import com.prgrmsfinal.skypedia.photo.entity.compositekey.PhotoPlanPostId;
import com.prgrmsfinal.skypedia.planpost.entity.PlanPost;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PhotoPlanPost extends AbstractAssociationEntity<PhotoPlanPostId, Photo, PlanPost> {
    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("photoId")
    @JoinColumn(name = "photo_id", referencedColumnName = "id", nullable = false)
    private Photo photo;

    @ManyToOne
    @MapsId("planPostId")
    @JoinColumn(name = "plan_post_id", referencedColumnName = "id", nullable = false)
    private PlanPost planPost;

    @Builder
    public PhotoPlanPost(Photo photo, PlanPost planPost) {
        super.initializeId(photo, planPost);
        this.photo = photo;
        this.planPost = planPost;
    }

    @Override
    protected PhotoPlanPostId createId(Photo photo, PlanPost planPost) {
        return new PhotoPlanPostId(photo.getId(), planPost.getId());
    }
}
