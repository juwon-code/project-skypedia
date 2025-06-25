package com.prgrmsfinal.skypedia.member.entity;

import com.prgrmsfinal.skypedia.global.constant.RoleType;
import com.querydsl.core.annotations.QueryEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@QueryEntity
@Document(indexName = "member")
@Setting(settingPath = "/elasticsearch/korean-analyzer.json", replicas = 0)
public class MemberDocument {
    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "korean_index_analyzer", searchAnalyzer = "korean_search_analyzer")
    private String name;

    @Field(type = FieldType.Text, analyzer = "korean_index_analyzer", searchAnalyzer = "korean_search_analyzer")
    private String nickname;

    @Field(type = FieldType.Text)
    private String email;

    @Field(type = FieldType.Keyword)
    private String socialType;

    @Field(type = FieldType.Keyword)
    private List<String> roles;

    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date)
    private LocalDateTime removedAt;

    @Builder
    public MemberDocument(Long id, String name, String nickname, String email, String socialType, List<String> roles
            , LocalDateTime createdAt, LocalDateTime removedAt) {
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.socialType = socialType;
        this.roles = roles;
        this.createdAt = createdAt;
        this.removedAt = removedAt;
    }

    public static MemberDocument of(Member member, List<RoleType> roles) {
        return MemberDocument.builder()
                .id(member.getId())
                .name(member.getName())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .roles(roles.stream().map(RoleType::name).toList())
                .createdAt(member.getCreatedAt())
                .removedAt(member.getRemovedAt())
                .build();
    }
}
