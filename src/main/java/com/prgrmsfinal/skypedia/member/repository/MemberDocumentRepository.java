package com.prgrmsfinal.skypedia.member.repository;

import com.prgrmsfinal.skypedia.global.constant.SearchOption;
import com.prgrmsfinal.skypedia.global.constant.SortType;
import com.prgrmsfinal.skypedia.member.entity.MemberDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;

public interface MemberDocumentRepository {
    MemberDocument get(Long memberId);

    SearchHits<MemberDocument> findBy(String keyword, SearchOption option, SortType sortType, Pageable pageable);

    void save(MemberDocument document);

    void deleteAll(List<Long> ids);
}
