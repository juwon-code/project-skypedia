package com.prgrmsfinal.skypedia.photo.entity;

import com.prgrmsfinal.skypedia.global.entity.AbstractAssociationEntity;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.photo.entity.compositekey.PhotoProfileId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PhotoMember extends AbstractAssociationEntity<PhotoProfileId, Photo, Member> {
    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("photoId")
    @JoinColumn(name = "photo_id", referencedColumnName = "id", nullable = false)
    private Photo photo;

    @ManyToOne
    @MapsId("memberId")
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @Builder
    public PhotoMember(Photo photo, Member member) {
        super.initializeId(photo, member);
        this.photo = photo;
        this.member = member;
    }

    @Override
    protected PhotoProfileId createId(Photo photo, Member member) {
        return new PhotoProfileId(photo.getId(), member.getId());
    }
}
