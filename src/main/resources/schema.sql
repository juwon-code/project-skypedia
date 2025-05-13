USE skypedia;

CREATE TABLE IF NOT EXISTS member
(
    id          BIGINT        AUTO_INCREMENT PRIMARY KEY,
    oauth_id    VARCHAR(255)  UNIQUE NOT NULL,
    name        VARCHAR(255)  NOT NULL,
    nickname    VARCHAR(20)   UNIQUE NOT NULL,
    email       VARCHAR(50)   UNIQUE NOT NULL,
    removed     TINYINT(1)    NOT NULL DEFAULT 0 CHECK (removed IN (0, 1)),
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    removed_at  TIMESTAMP     NULL,
    FULLTEXT INDEX f_idx_hashtag_nickname (nickname)
);

CREATE TABLE IF NOT EXISTS member_role
(
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT       NOT NULL,
    role_type   VARCHAR(20)  NOT NULL CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN')),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS notify_message
(
    id           BIGINT        AUTO_INCREMENT PRIMARY KEY,
    notify_type  VARCHAR(20)   NOT NULL CHECK (notify_type IN ('NOTICE', 'CHAT', 'REPLY', 'LIKES')),
    content      VARCHAR(255)  NOT NULL,
    url          VARCHAR(255)  NOT NULL,
    activated    TINYINT(1)    NOT NULL DEFAULT 0 CHECK (viewed IN (0, 1)),
    created_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expired_at   TIMESTAMP     GENERATED ALWAYS AS (
        CASE
            WHEN notify_type = 'NOTICE' THEN created_at + INTERVAL 7 DAY
            ELSE NULL
        END
    ) STORED
);

CREATE TABLE IF NOT EXISTS notify_viewer
(
    notify_message_id  BIGINT      NOT NULL,
    member_id          BIGINT      NOT NULL,
    viewed             TINYINT(1)  NOT NULL DEFAULT 0 CHECK (viewed IN (0, 1)),
    created_at         TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    viewed_at          TIMESTAMP   NULL,
    PRIMARY KEY (notify_message_id, member_id),
    FOREIGN KEY (notify_message_id) REFERENCES notify_message (id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chat_room
(
    id          BIGINT      AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT      NOT NULL,
    removed     TINYINT(1)  NOT NULL DEFAULT 0 CHECK (removed IN (0, 1)),
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    removed_at  TIMESTAMP   NULL,
    FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE IF NOT EXISTS chat_participant
(
    chat_room_id  BIGINT     NOT NULL,
    member_id     BIGINT     NOT NULL,
    created_at    TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (chat_room_id, member_id),
    FOREIGN KEY (chat_room_id) REFERENCES chat_room (id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE IF NOT EXISTS chat_black_list
(
    member_id   BIGINT     NOT NULL,
    target_id   BIGINT     NOT NULL,
    created_at  TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (member_id, target_id),
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE,
    FOREIGN KEY (target_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chat_message
(
    id            BIGINT        AUTO_INCREMENT PRIMARY KEY,
    chat_room_id  BIGINT        NOT NULL,
    member_id     BIGINT        NOT NULL,
    content       VARCHAR(255)  NOT NULL DEFAULT '',
    imaged        TINYINT(1)    NOT NULL DEFAULT 0 CHECK (is_photo IN (0, 1)),
    removed       TINYINT(1)    NOT NULL DEFAULT 0 CHECK (removed IN (0, 1)),
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    removed_at    TIMESTAMP     NULL,
    FOREIGN KEY (chat_room_id) REFERENCES chat_room (id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE IF NOT EXISTS chat_message_read
(
    chat_message_id  BIGINT     NOT NULL,
    member_id        BIGINT     NOT NULL,
    created_at       TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (chat_message_id, member_id),
    FOREIGN KEY (chat_message_id) REFERENCES chat_message (id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS post_category
(
    id           BIGINT        AUTO_INCREMENT PRIMARY KEY,
    member_id    BIGINT        NOT NULL,
    name         VARCHAR(100)  UNIQUE NOT NULL,
    description  VARCHAR(255)  NOT NULL DEFAULT '',
    created_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE IF NOT EXISTS post
(
    id                BIGINT       AUTO_INCREMENT PRIMARY KEY,
    member_id         BIGINT       NOT NULL,
    post_category_id  BIGINT       NOT NULL,
    title             VARCHAR(50)  NOT NULL,
    content           TEXT         NOT NULL DEFAULT '',
    removed           TINYINT(1)   NOT NULL DEFAULT 0 CHECK (removed IN (0, 1)),
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    removed_at        TIMESTAMP    NULL,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE,
    FOREIGN KEY (post_category_id) REFERENCES post_category (id) ON DELETE CASCADE,
    FULLTEXT INDEX f_idx_post_title (title),
    FULLTEXT INDEX f_idx_post_title_content (title, content)
);

CREATE TABLE IF NOT EXISTS post_metrics
(
    id          BIGINT  NOT NULL PRIMARY KEY,
    view_count  BIGINT  NOT NULL DEFAULT 0,
    like_count  BIGINT  NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS post_scrap
(
    post_id     BIGINT     NOT NULL,
    member_id   BIGINT     NOT NULL,
    created_at  TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (post_id, member_id),
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS post_likes
(
    post_id     BIGINT     NOT NULL,
    member_id   BIGINT     NOT NULL,
    created_at  TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (post_id, member_id),
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS vote_post
(
    id          BIGINT      AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT      NOT NULL,
    ended       TINYINT(1)  NOT NULL DEFAULT 0 CHECK (vote_ended IN (0, 1)),
    removed     TINYINT(1)  NOT NULL DEFAULT 0 CHECK (removed IN (0, 1)),
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    ended_at    TIMESTAMP   NOT NULL,
    removed_at  TIMESTAMP   NULL,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS vote_post_scrap
(
    vote_post_id  BIGINT     NOT NULL,
    member_id     BIGINT     NOT NULL,
    created_at    TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (vote_post_id, member_id),
    FOREIGN KEY (vote_post_id) REFERENCES vote_post (id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS vote_post_metrics
(
    id          BIGINT  NOT NULL PRIMARY KEY,
    view_count  BIGINT  NOT NULL DEFAULT 0,
    like_count  BIGINT  NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS vote_post_item
(
    id            BIGINT      AUTO_INCREMENT PRIMARY KEY,
    vote_post_id  BIGINT      NOT NULL,
    position      TINYINT(1)  NOT NULL CHECK (position IN (1, 2)),
    won           TINYINT(1)  NOT NULL DEFAULT 0 CHECK (won IN (0, 1)),
    created_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (vote_post_id) REFERENCES vote_post (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS vote_post_item_likes
(
    vote_post_item_id  BIGINT     NOT NULL,
    member_id          BIGINT     NOT NULL,
    created_at         TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (vote_post_item_id, member_id),
    FOREIGN KEY (vote_post_item_id) REFERENCES vote_post_item (id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS plan_post_city
(
    id    BIGINT       AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(50)  UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS plan_post
(
    id                 BIGINT         AUTO_INCREMENT PRIMARY KEY,
    member_id          BIGINT         NOT NULL,
    plan_post_city_id  BIGINT         NOT NULL,
    title              VARCHAR(50)    NOT NULL,
    summary            VARCHAR(255)   NOT NULL DEFAULT '',
    total_rating       DECIMAL(3, 2)  NOT NULL DEFAULT 0.00 CHECK (total_rating BETWEEN 0.00 AND 5.00),
    removed            TINYINT(1)     NOT NULL DEFAULT 0 CHECK (removed IN (0, 1)),
    created_at         TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    removed_at         TIMESTAMP      NULL,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE,
    FOREIGN KEY (plan_post_city_id) REFERENCES plan_post_city (id) ON DELETE CASCADE,
    FULLTEXT INDEX f_idx_plan_post_title (title),
    FULLTEXT INDEX f_idx_plan_post_title_summary (title, summary)
);

CREATE TABLE IF NOT EXISTS plan_post_item
(
    id            BIGINT         AUTO_INCREMENT PRIMARY KEY,
    plan_post_id  BIGINT         NOT NULL,
    place_id      VARCHAR(100)   NOT NULL,
    place_name    VARCHAR(255)   NOT NULL,
    description   VARCHAR(255)   NOT NULL DEFAULT '',
    coordinates   POINT          NOT NULL SRID 4326,
    rating        DECIMAL(3, 2)  NOT NULL DEFAULT 0.00 CHECK (rating BETWEEN 0.00 AND 5.00),
    removed       TINYINT(1)     NOT NULL DEFAULT 0 CHECK (removed IN (0, 1)),
    created_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    removed_at    TIMESTAMP      NULL,
    previous_id   BIGINT         UNSIGNED NULL,
    next_id       BIGINT         UNSIGNED NULL,
    FOREIGN KEY (plan_post_id) REFERENCES plan_post (id) ON DELETE CASCADE,
    FULLTEXT INDEX f_idx_plan_post_item_place_name (place_name),
    FULLTEXT INDEX f_idx_plan_post_item_place_name_description (place_name, description),
    SPATIAL INDEX s_idx_plan_post_coordinates (coordinates)
);

CREATE TABLE IF NOT EXISTS plan_post_scrap
(
    plan_post_id  BIGINT     NOT NULL,
    member_id     BIGINT     NOT NULL,
    created_at    TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (plan_post_id, member_id),
    FOREIGN KEY (plan_post_id) REFERENCES plan_post (id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS plan_post_likes
(
    plan_post_id  BIGINT     NOT NULL,
    member_id     BIGINT     NOT NULL,
    created_at    TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (plan_post_id, member_id),
    FOREIGN KEY (plan_post_id) REFERENCES plan_post (id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS plan_post_metrics
(
    id          BIGINT  NOT NULL PRIMARY KEY,
    view_count  BIGINT  NOT NULL DEFAULT 0,
    like_count  BIGINT  NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS photo
(
    id          BIGINT        AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT        NULL,
    uuid        VARCHAR(255)  UNIQUE NOT NULL,
    filename    VARCHAR(255)  NOT NULL,
    extension   VARCHAR(255)  NOT NULL,
    removed     TINYINT(1)    NOT NULL DEFAULT 0 CHECK (removed IN (0, 1)),
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    removed_at  TIMESTAMP     NULL,
    FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE IF NOT EXISTS photo_profile
(
    photo_id    BIGINT     NOT NULL,
    member_id   BIGINT     NOT NULL,
    created_at  TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (photo_id, member_id),
    FOREIGN KEY (photo_id) REFERENCES photo (id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE IF NOT EXISTS photo_chat_message
(
    photo_id         BIGINT     NOT NULL,
    chat_message_id  BIGINT     NOT NULL,
    created_at       TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (photo_id, chat_message_id),
    FOREIGN KEY (photo_id) REFERENCES photo (id) ON DELETE CASCADE,
    FOREIGN KEY (chat_message_id) REFERENCES chat_message (id)
);

CREATE TABLE IF NOT EXISTS photo_post
(
    photo_id    BIGINT     NOT NULL,
    post_id     BIGINT     NOT NULL,
    created_at  TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (photo_id, post_id),
    FOREIGN KEY (photo_id) REFERENCES photo (id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES post (id)
);

CREATE TABLE IF NOT EXISTS photo_vote_post
(
    photo_id      BIGINT     NOT NULL,
    vote_post_id  BIGINT     NOT NULL,
    created_at    TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (photo_id, vote_post_id),
    FOREIGN KEY (photo_id) REFERENCES photo (id) ON DELETE CASCADE,
    FOREIGN KEY (vote_post_id) REFERENCES vote_post (id)
);

CREATE TABLE IF NOT EXISTS photo_vote_post_item
(
    photo_id           BIGINT     NOT NULL,
    vote_post_item_id  BIGINT     NOT NULL,
    created_at         TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (photo_id, vote_post_item_id),
    FOREIGN KEY (photo_id) REFERENCES photo (id) ON DELETE CASCADE,
    FOREIGN KEY (vote_post_item_id) REFERENCES vote_post_item (id)
);

CREATE TABLE IF NOT EXISTS photo_plan_post
(
    photo_id      BIGINT     NOT NULL,
    plan_post_id  BIGINT     NOT NULL,
    created_at    TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (photo_id, plan_post_id),
    FOREIGN KEY (photo_id) REFERENCES photo (id) ON DELETE CASCADE,
    FOREIGN KEY (plan_post_id) REFERENCES plan_post (id)
);

CREATE TABLE IF NOT EXISTS photo_plan_post_item
(
    photo_id           BIGINT     NOT NULL,
    plan_post_item_id  BIGINT     NOT NULL,
    created_at         TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (photo_id, plan_post_item_id),
    FOREIGN KEY (photo_id) REFERENCES photo (id) ON DELETE CASCADE,
    FOREIGN KEY (plan_post_item_id) REFERENCES plan_post_item (id)
);

CREATE TABLE IF NOT EXISTS hashtag
(
    id          BIGINT        AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT        NOT NULL,
    name        VARCHAR(100)  NOT NULL UNIQUE,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member (id),
    FULLTEXT INDEX f_idx_hashtag_name (name)
);

CREATE TABLE IF NOT EXISTS hashtag_post
(
    hashtag_id  BIGINT     NOT NULL,
    post_id     BIGINT     NOT NULL,
    created_at  TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (hashtag_id, post_id),
    FOREIGN KEY (hashtag_id) REFERENCES hashtag (id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS hashtag_vote_post
(
    hashtag_id    BIGINT  NOT NULL,
    vote_post_id  BIGINT  NOT NULL,
    created_at    TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (hashtag_id, vote_post_id),
    FOREIGN KEY (hashtag_id) REFERENCES hashtag (id) ON DELETE CASCADE,
    FOREIGN KEY (vote_post_id) REFERENCES vote_post (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS hashtag_plan_post
(
    hashtag_id    BIGINT  NOT NULL,
    plan_post_id  BIGINT  NOT NULL,
    created_at    TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (hashtag_id, plan_post_id),
    FOREIGN KEY (hashtag_id) REFERENCES hashtag (id) ON DELETE CASCADE,
    FOREIGN KEY (plan_post_id) REFERENCES plan_post (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reply
(
    id               BIGINT        AUTO_INCREMENT PRIMARY KEY,
    member_id        BIGINT        NOT NULL,
    parent_reply_id  BIGINT        NULL,
    content          VARCHAR(255)  NOT NULL DEFAULT '',
    updated          TINYINT(1)    NOT NULL DEFAULT 0 CHECK (updated IN (0, 1)),
    removed          TINYINT(1)    NOT NULL DEFAULT 0 CHECK (removed IN (0, 1)),
    created_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    removed_at       TIMESTAMP     NULL,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE,
    FOREIGN KEY (parent_reply_id) REFERENCES reply (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS reply_likes
(
    reply_id    BIGINT     NOT NULL,
    member_id   BIGINT     NOT NULL,
    created_at  TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (reply_id, member_id),
    FOREIGN KEY (reply_id) REFERENCES reply (id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reply_post
(
    reply_id    BIGINT     NOT NULL,
    post_id     BIGINT     NOT NULL,
    created_at  TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (reply_id, post_id),
    FOREIGN KEY (reply_id) REFERENCES reply (id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reply_vote_post
(
    reply_id      BIGINT     NOT NULL,
    vote_post_id  BIGINT     NOT NULL,
    created_at    TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (reply_id, vote_post_id),
    FOREIGN KEY (reply_id) REFERENCES reply (id) ON DELETE CASCADE,
    FOREIGN KEY (vote_post_id) REFERENCES vote_post (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reply_plan_post
(
    reply_id      BIGINT NOT NULL,
    plan_post_id  BIGINT NOT NULL,
    created_at   TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (reply_id, plan_post_id),
    FOREIGN KEY (reply_id) REFERENCES reply (id) ON DELETE CASCADE,
    FOREIGN KEY (plan_post_id) REFERENCES plan_post (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reply_metrics
(
    id          BIGINT  NOT NULL PRIMARY KEY,
    like_count  BIGINT  NOT NULL DEFAULT 0
);