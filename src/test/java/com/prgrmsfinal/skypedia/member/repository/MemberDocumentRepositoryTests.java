package com.prgrmsfinal.skypedia.member.repository;

import com.prgrmsfinal.skypedia.global.constant.RoleType;
import com.prgrmsfinal.skypedia.global.constant.SearchOption;
import com.prgrmsfinal.skypedia.global.constant.SocialType;
import com.prgrmsfinal.skypedia.global.constant.SortType;
import com.prgrmsfinal.skypedia.member.entity.MemberDocument;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataElasticsearchTest
@Import(MemberDocumentRepositoryImpl.class)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MemberDocumentRepositoryTests {
    @Container
    @ServiceConnection
    private static ElasticsearchContainer elasticsearchContainer
            = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.17.5")
            .withCommand("sh", "-c", "elasticsearch-plugin install analysis-nori --batch && /usr/local/bin/docker-entrypoint.sh eswrapper");

    @Autowired
    private MemberDocumentRepositoryImpl memberDocumentRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @BeforeAll
    void setup() {
        IndexOperations indexOperations = elasticsearchOperations.indexOps(MemberDocument.class);
        indexOperations.create();
        indexOperations.putMapping(indexOperations.createMapping());
    }

    @BeforeEach
    void setupData() {
        IndexCoordinates indexCoordinates = IndexCoordinates.of("member");

        IntStream.rangeClosed(1, 10).forEach(i -> {
            IndexQuery indexQuery = new IndexQuery();
            indexQuery.setId(String.valueOf(i));
            indexQuery.setObject(
                    MemberDocument.builder()
                            .id((long) i)
                            .nickname("nickname" + i)
                            .email("email" + i + "@email.com")
                            .roles((i <= 5) ? List.of(RoleType.USER.name()) : List.of(RoleType.USER.name(), RoleType.ADMIN.name()))
                            .createdAt(LocalDateTime.now())
                            .removedAt((i % 2 == 0) ?  LocalDateTime.now() : null)
                            .socialType((i % 2 == 0) ? SocialType.NAVER.name() : SocialType.KAKAO.name())
                            .build()
            );

            elasticsearchOperations.index(indexQuery, indexCoordinates);
        });

        elasticsearchOperations.indexOps(IndexCoordinates.of("member")).refresh();
    }

    @AfterEach
    void cleanup() {
        IndexOperations indexOperations = elasticsearchOperations.indexOps(MemberDocument.class);
        indexOperations.delete();
        indexOperations.create();
        indexOperations.putMapping(indexOperations.createMapping());

    }

    @DisplayName("[성공] 회원 데이터 조회 :: ID와 일치하는 회원 데이터 조회")
    @Test
    void get_whenDocumentIdExists_thenReturnData() {
        MemberDocument result = memberDocumentRepository.get(1L);

        assertEquals(1L, result.getId());
        assertEquals("nickname1", result.getNickname());
        assertEquals("email1@email.com", result.getEmail());
    }

    @DisplayName("[성공] 회원 데이터 검색 :: 대문자 닉네임으로 회원을 검색하고 최신순으로 정렬")
    @Test
    void findBy_whenSearchWithCaseInsensitiveKeywordAndSortedByNewest_thenReturn10FoundDatas() {
        Pageable pageable = PageRequest.of(0, 10);

        SearchHits<MemberDocument> result
                = memberDocumentRepository.findBy("NICKNAME", SearchOption.MEMBER_NICKNAME, SortType.NEWEST, pageable);

        assertEquals(10, result.getTotalHits());

        List<String> sortedNicknames = result.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(MemberDocument::getNickname)
                .toList();

        List<String> expectedNicknames = IntStream.rangeClosed(1, 10)
                .mapToObj(i -> "nickname" + (11 - i))
                .toList();

        assertEquals(sortedNicknames, expectedNicknames);
    }

    @DisplayName("[성공] 회원 데이터 검색 :: 닉네임이 정확히 일치하는 회원 1건 조회")
    @Test
    void findBy_whenSearchWithExactKeyword_thenReturn1FoundData() {
        Pageable pageable = PageRequest.of(0, 10);

        SearchHits<MemberDocument> result
                = memberDocumentRepository.findBy("nickname10", SearchOption.MEMBER_NICKNAME, SortType.NEWEST, pageable);

        assertEquals(1, result.getTotalHits());

        MemberDocument content = result.getSearchHit(0).getContent();

        assertEquals(10L, content.getId());
        assertEquals("nickname10", content.getNickname());
        assertEquals("email10@email.com", content.getEmail());
        assertEquals(List.of(RoleType.USER.name(), RoleType.ADMIN.name()), content.getRoles());
        assertEquals(SocialType.NAVER.name(), content.getSocialType());
    }

    @DisplayName("[성공] 회원 데이터 저장 :: 회원 데이터를 성공적으로 저장")
    @Test
    void save_whenValidMemberDocument_thenSaveData() {
        MemberDocument memberDocument = MemberDocument.builder()
                .id((long) 999)
                .nickname("nickname999")
                .email("email999@email.com")
                .roles(List.of(RoleType.USER.name(), RoleType.ADMIN.name()))
                .createdAt(LocalDateTime.now())
                .removedAt(null)
                .socialType(SocialType.KAKAO.name())
                .build();

        memberDocumentRepository.save(memberDocument);

        MemberDocument result = elasticsearchOperations.get("999", MemberDocument.class, IndexCoordinates.of("member"));

        assertEquals(999, result.getId());
        assertEquals("nickname999", result.getNickname());
        assertEquals("email999@email.com", result.getEmail());
    }

    @DisplayName("[성공] 회원 데이터 삭제 :: 회원 ID와 일치하는 문서를 성공적으로 삭제")
    @Test
    void deleteAll_whenValidMemberIds_thenDeleteData() {
        List<Long> memberIds = List.of(1L, 2L, 3L);

        memberDocumentRepository.deleteAll(memberIds);

        IntStream.rangeClosed(1, 3).forEach(index -> {
            MemberDocument result = elasticsearchOperations.get(String.valueOf(index), MemberDocument.class, IndexCoordinates.of("member"));
            assertNull(result);
        });
    }
}
