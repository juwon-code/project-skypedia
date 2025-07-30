INSERT INTO member (id, oauth_id, name, nickname, email, social_type, removed, created_at, updated_at, removed_at)
VALUES
    (1, 'Oauth_Id_1', '홍길동', '닉네임1', 'gildong@naver.com', 'NAVER', false, '2025-01-01 00:00:00', '2025-01-01 00:00:00', null),
    (2, 'Oauth_Id_2', '김철수', '닉네임2', 'kim@naver.com', 'NAVER',false, '2025-01-01 00:00:00', '2025-01-01 00:00:00', null),
    (3, 'Oauth_Id_3', '이영희', '닉네임3', 'lee@naver.com', 'NAVER', true, '2024-01-01 00:00:00', '2024-01-01 00:00:00', '2025-01-01 00:00:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO member_role (id, member_id, role_type, created_at)
VALUES
    (1, 1, 'USER', '2025-01-01 00:00:00'),
    (2, 1, 'ADMIN', '2025-01-01 00:00:00'),
    (3, 2, 'USER', '2025-01-01 00:00:00'),
    (4, 3, 'USER', '2025-01-01 00:00:00')
ON CONFLICT (id) DO NOTHING;