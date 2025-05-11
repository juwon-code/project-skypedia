package com.prgrmsfinal.skypedia.photo.entity;

import com.prgrmsfinal.skypedia.global.entity.AbstractAssociationEntity;
import com.prgrmsfinal.skypedia.photo.entity.compositekey.PhotoPlanPostItemId;
import com.prgrmsfinal.skypedia.planpost.entity.PlanPostItem;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PhotoPlanPostItem extends AbstractAssociationEntity<PhotoPlanPostItemId, Photo, PlanPostItem> {
    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("photoId")
    @JoinColumn(name = "photo_id", referencedColumnName = "id", nullable = false)
    private Photo photo;

    @ManyToOne
    @MapsId("planPostItemId")
    @JoinColumn(name = "plan_post_item_id", referencedColumnName = "id", nullable = false)
    private PlanPostItem planPostItem;

    @Builder
    public PhotoPlanPostItem(Photo photo, PlanPostItem planPostItem) {
        super.initializeId(photo, planPostItem);
        this.photo = photo;
        this.planPostItem = planPostItem;
    }

    @Override
    protected PhotoPlanPostItemId createId(Photo photo, PlanPostItem planPostItem) {
        return new PhotoPlanPostItemId(photo.getId(), planPostItem.getId());
    }
}
