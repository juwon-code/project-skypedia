package com.prgrmsfinal.skypedia.photo.repository;

import com.prgrmsfinal.skypedia.photo.entity.Photo;
import com.prgrmsfinal.skypedia.photo.entity.PhotoMember;
import com.prgrmsfinal.skypedia.photo.entity.compositekey.PhotoProfileId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PhotoMemberRepository extends JpaRepository<PhotoMember, PhotoProfileId> {
    @Query("SELECT pm.photo FROM PhotoMember pm WHERE pm.member.id =: memberId")
    Optional<Photo> findPhotoByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT pm.photo FROM PhotoMember pm WHERE pm.member.id IN :memberIds")
    List<Photo> findPhotosByMemberId(@Param("memberIds") List<Long> memberIds);
}
