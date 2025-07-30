package com.prgrmsfinal.skypedia.member.repository;

import com.prgrmsfinal.skypedia.global.config.QueryDslConfig;
import com.prgrmsfinal.skypedia.global.constant.RoleType;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({MemberRoleQueryRepository.class, QueryDslConfig.class})
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MemberRoleQueryRepositoryTests {
    private static DockerImageName image = DockerImageName
            .parse("postgis/postgis:17-master")
            .asCompatibleSubstituteFor("postgres");

    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(image)
            .withDatabaseName("skypedia");

    @Autowired
    private MemberRoleQueryRepository memberRoleQueryRepository;

    @Autowired
    private Flyway flyway;

    @BeforeEach
    void migrateDB() {
        flyway.clean();
        flyway.migrate();
    }

    @DisplayName("[성공] 회원 역할 조회 :: ID와 일치하는 회원 역할 조회")
    @Test
    void findRoleTypesByMemberId_whenMemberHasRoles_thenReturnRoleTypes() {
        List<RoleType> roleTypes = memberRoleQueryRepository.findRoleTypesByMemberId(1L);

        assertThat(roleTypes).hasSize(2);
        assertThat(roleTypes).containsExactlyInAnyOrder(RoleType.USER, RoleType.ADMIN);
    }

    @DisplayName("[성공] 회원 역할 중복 검사 :: ID와 역할이 일치하는 회원 역할이 존재하는지 확인")
    @Test
    void existsByMemberIdAndRoleType_whenMemberHasRole_thenReturnTrue() {
        assertThat(memberRoleQueryRepository.existsByMemberIdAndRoleType(1L, RoleType.USER)).isTrue();
    }

    @DisplayName("[성공] 회원 역할 삭제 :: ID와 역할이 일치하는 회원 역할 삭제")
    @Test
    void deleteByMemberIdAndRoleType_whenMemberHasRole_thenReturnAffectedCount() {
        long affectedCount = memberRoleQueryRepository.deleteByMemberIdAndRoleType(1L, RoleType.USER);

        assertThat(affectedCount).isEqualTo(1);
    }
}
