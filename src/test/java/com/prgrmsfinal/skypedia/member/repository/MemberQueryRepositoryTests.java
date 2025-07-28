package com.prgrmsfinal.skypedia.member.repository;

import com.prgrmsfinal.skypedia.global.config.QueryDslConfig;
import com.prgrmsfinal.skypedia.member.dto.MemberRequestDto;
import com.prgrmsfinal.skypedia.member.entity.Member;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import({MemberQueryRepository.class, QueryDslConfig.class})
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MemberQueryRepositoryTests {
    private static DockerImageName image = DockerImageName
            .parse("postgis/postgis:17-master")
            .asCompatibleSubstituteFor("postgres");

    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(image)
            .withDatabaseName("skypedia");

    @Autowired
    private MemberQueryRepository memberQueryRepository;

    @Autowired
    private Flyway flyway;

    @BeforeEach
    void migrateDB() {
        flyway.clean();
        flyway.migrate();
    }

    @DisplayName("[성공] 회원 단일 조회 :: OAuthID와 일치하는 회원 조회")
    @Test
    void findOneBy_whenMemberExists_thenReturnFoundMember() {
        Member foundMember = memberQueryRepository.findOneBy(
                MemberRequestDto.SearchOptions.builder()
                        .oauthId("Oauth_Id_1")
                        .build()
        ).get();

        assertEquals(1L, foundMember.getId());
        assertEquals("홍길동", foundMember.getName());
        assertEquals("닉네임1", foundMember.getNickname());
        assertEquals("gildong@naver.com", foundMember.getEmail());
        assertEquals("NAVER", foundMember.getSocialType().name());
    }

    @DisplayName("[성공] 회원 중복 검사 :: 닉네임이 일치하는 회원이 존재하는지 확인")
    @Test
    void existsByNickname_whenMemberNotExists_thenReturnTrue() {
        assertTrue(memberQueryRepository.existsByNickname("닉네임1"));
    }

    @DisplayName("[성공] 이전 일자 회원 삭제 :: 특정일을 기준으로 탈퇴한 회원의 데이터를 영구 삭제")
    @Test
    void deleteAllByCutoff_whenBeforeCutoffDate_thenReturnRemovedIds() {
        LocalDateTime cutoff = LocalDateTime.now();

        List<Long> deleteIds = memberQueryRepository.deleteAllByCutoff(cutoff);

        assertEquals(1, deleteIds.size());
        assertEquals(3, deleteIds.get(0));
    }
}
