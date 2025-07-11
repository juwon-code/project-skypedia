package com.prgrmsfinal.skypedia.member.repository;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.prgrmsfinal.skypedia.global.constant.SearchOption;
import com.prgrmsfinal.skypedia.global.constant.SortType;
import com.prgrmsfinal.skypedia.member.entity.MemberDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MemberDocumentRepositoryImpl implements MemberDocumentRepository {
    private final ElasticsearchOperations elasticsearchOperations;

    @Autowired
    public MemberDocumentRepositoryImpl(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    public MemberDocument get(Long memberId) {
        return elasticsearchOperations.get(memberId.toString(), MemberDocument.class);
    }

    @Override
    public SearchHits<MemberDocument> findBy(String keyword, SearchOption option, SortType sortType, Pageable pageable) {
        Query query = switch (option) {
            case MEMBER_NICKNAME -> MatchQuery.of(match -> match
                    .field("nickname").query(keyword))._toQuery();
            case MEMBER_EMAIL -> MatchQuery.of(match -> match
                    .field("email").query(keyword))._toQuery();
            case MEMBER_ALL -> MultiMatchQuery.of(match -> match
                    .fields("nickname", "email").query(keyword))._toQuery();
        };

        SortOptions sortOptions = switch (sortType) {
            case OLDEST -> SortOptions.of(sort -> sort
                    .field(field -> field.field("createdAt").order(SortOrder.Asc)));
            case NEWEST -> SortOptions.of(sort -> sort
                    .field(field -> field.field("createdAt").order(SortOrder.Desc)));
            case RELEVANCE -> null;
        };

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(query)
                .withSort(sortOptions)
                .withPageable(pageable)
                .build();

        return elasticsearchOperations.search(nativeQuery, MemberDocument.class);
    }

    @Override
    public void save(MemberDocument document) {
        IndexQuery indexQuery = new IndexQuery();
        indexQuery.setId(document.getId().toString());
        indexQuery.setObject(document);

        IndexCoordinates indexCoordinates = IndexCoordinates.of("member");

        elasticsearchOperations.index(indexQuery, indexCoordinates);
    }



    @Override
    public void deleteAll(List<Long> ids) {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withIds(ids.stream().map(String::valueOf).toList())
                .build();

        IndexCoordinates indexCoordinates = IndexCoordinates.of("member");

        elasticsearchOperations.delete(nativeQuery, indexCoordinates);
    }
}
