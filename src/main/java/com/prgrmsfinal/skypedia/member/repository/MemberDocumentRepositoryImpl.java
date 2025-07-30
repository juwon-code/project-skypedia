package com.prgrmsfinal.skypedia.member.repository;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.prgrmsfinal.skypedia.global.constant.SearchOption;
import com.prgrmsfinal.skypedia.global.constant.SortType;
import com.prgrmsfinal.skypedia.member.entity.MemberDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
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
            case MEMBER_NICKNAME -> WildcardQuery.of(wildcard -> wildcard
                    .field("nickname").value(String.format("*%s*", keyword)))
                    ._toQuery();
            case MEMBER_EMAIL -> WildcardQuery.of(wildcard -> wildcard
                    .field("email").value(String.format("*%s*", keyword)))
                    ._toQuery();
            case MEMBER_ALL -> BoolQuery.of(bool -> bool
                    .should(WildcardQuery.of(w -> w.field("nickname").value(String.format("*%s*", keyword))))
                    .should(WildcardQuery.of(w -> w.field("email").value(String.format("*%s*", keyword)))))
                    ._toQuery();
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
        List<String> stringIds = ids.stream()
                .map(String::valueOf)
                .toList();

        Query idsQuery = IdsQuery.of(iq -> iq.values(stringIds))._toQuery();

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(idsQuery)
                .build();

        DeleteQuery deleteQuery = DeleteQuery.builder(nativeQuery).build();

        elasticsearchOperations.delete(deleteQuery, MemberDocument.class, IndexCoordinates.of("member"));
    }
}
