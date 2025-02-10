package com.prgrmsfinal.skypedia.photo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.prgrmsfinal.skypedia.photo.entity.Image;
import com.prgrmsfinal.skypedia.photo.entity.PostType;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
	List<Image> findAllByPostTypeAndPostContentId(PostType postType, Long postContentId);

	@Modifying
	@Query("DELETE FROM Image i WHERE i.uuid IN :uuids")
	void deleteAllByUuids(List<String> uuids);
}
