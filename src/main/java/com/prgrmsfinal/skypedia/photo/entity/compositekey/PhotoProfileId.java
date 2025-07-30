package com.prgrmsfinal.skypedia.photo.entity.compositekey;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class PhotoProfileId implements Serializable {
    private Long photoId;

    private Long memberId;
}
