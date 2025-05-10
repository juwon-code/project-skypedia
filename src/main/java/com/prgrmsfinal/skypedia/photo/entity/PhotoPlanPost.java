package com.prgrmsfinal.skypedia.photo.entity;

import com.prgrmsfinal.skypedia.photo.entity.compositekey.PhotoPlanPostId;
import com.prgrmsfinal.skypedia.planpost.entity.PlanPost;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PhotoPlanPost {
    @EmbeddedId
    private PhotoPlanPostId id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("photoId")
    @JoinColumn(name = "photo_id", referencedColumnName = "id", nullable = false)
    private Photo photo;

    @ManyToOne
    @MapsId("planPostId")
    @JoinColumn(name = "plan_post_id", referencedColumnName = "id", nullable = false)
    private PlanPost planPost;

    @Column(insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public PhotoPlanPost(Photo photo, PlanPost planPost) {
        this.id = new PhotoPlanPostId(photo.getId(), planPost.getId());
        this.photo = photo;
        this.planPost = planPost;
    }
}
