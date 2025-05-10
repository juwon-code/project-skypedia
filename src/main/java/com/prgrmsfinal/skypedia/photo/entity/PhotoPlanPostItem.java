package com.prgrmsfinal.skypedia.photo.entity;

import com.prgrmsfinal.skypedia.photo.entity.compositekey.PhotoPlanPostItemId;
import com.prgrmsfinal.skypedia.planpost.entity.PlanPostItem;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PhotoPlanPostItem {
    @EmbeddedId
    private PhotoPlanPostItemId id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("photoId")
    @JoinColumn(name = "photo_id", referencedColumnName = "id", nullable = false)
    private Photo photo;

    @ManyToOne
    @MapsId("planPostItemId")
    @JoinColumn(name = "plan_post_item_id", referencedColumnName = "id", nullable = false)
    private PlanPostItem planPostItem;

    @Column(insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public PhotoPlanPostItem(Photo photo, PlanPostItem planPostItem) {
        this.id = new PhotoPlanPostItemId(photo.getId(), planPostItem.getId());
        this.photo = photo;
        this.planPostItem = planPostItem;
    }
}
