CREATE TABLE IF NOT EXISTS member
(
    id           BIGSERIAL    PRIMARY KEY,
    oauth_id     TEXT         UNIQUE NOT NULL,
    name         TEXT         NOT NULL,
    nickname     VARCHAR(20)  UNIQUE NOT NULL,
    email        VARCHAR(50)  UNIQUE NOT NULL,
    social_type  VARCHAR(6)   NOT NULL CHECK (social_type IN ('NAVER', 'KAKAO', 'GOOGLE')),
    removed      BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    removed_at   TIMESTAMP    NULL
);

CREATE TABLE IF NOT EXISTS member_role
(
    id          BIGSERIAL    PRIMARY KEY,
    member_id   BIGINT       NOT NULL,
    role_type   VARCHAR(10)  NOT NULL CHECK (role_type IN ('ROLE_USER', 'ROLE_ADMIN')),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS notify_message
(
    id           BIGSERIAL   PRIMARY KEY,
    notify_type  VARCHAR(6)  NOT NULL CHECK (notify_type IN ('NOTICE', 'CHAT', 'REPLY', 'LIKES')),
    content      TEXT        NOT NULL,
    url          TEXT        NOT NULL,
    activated    BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expired_at   TIMESTAMP   NULL
);

CREATE TABLE IF NOT EXISTS notify_viewer
(
    notify_message_id  BIGINT     NOT NULL,
    member_id          BIGINT     NOT NULL,
    viewed             BOOLEAN    NOT NULL DEFAULT FALSE,
    created_at         TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    viewed_at          TIMESTAMP  NULL,
    PRIMARY KEY (notify_message_id, member_id),
    FOREIGN KEY (notify_message_id) REFERENCES notify_message (id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chat_room
(
    id          BIGSERIAL  PRIMARY KEY,
    member_id   BIGINT     NOT NULL,
    removed     BOOLEAN    NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    removed_at  TIMESTAMP  NULL,
    FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE IF NOT EXISTS chat_participant
(
    chat_room_id  BIGINT     NOT NULL,
    member_id     BIGINT     NOT NULL,
    created_at    TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (chat_room_id, member_id),
    FOREIGN KEY (chat_room_id) REFERENCES chat_room (id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
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
    id            BIGSERIAL  PRIMARY KEY,
    chat_room_id  BIGINT     NOT NULL,
    member_id     BIGINT     NOT NULL,
    content       TEXT       NOT NULL DEFAULT '',
    imaged        BOOLEAN    NOT NULL DEFAULT FALSE,
    removed       BOOLEAN    NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    removed_at    TIMESTAMP  NULL,
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
    id           BIGSERIAL     PRIMARY KEY,
    name         VARCHAR(100)  UNIQUE NOT NULL,
    description  TEXT          NOT NULL DEFAULT '',
    created_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS post
(
    id                BIGSERIAL    PRIMARY KEY,
    member_id         BIGINT       NOT NULL,
    post_category_id  BIGINT       NOT NULL,
    title             VARCHAR(50)  NOT NULL,
    content           TEXT         NOT NULL DEFAULT '',
    removed           BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    removed_at        TIMESTAMP    NULL,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE,
    FOREIGN KEY (post_category_id) REFERENCES post_category (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS post_metrics
(
    post_id     BIGINT  PRIMARY KEY,
    view_count  BIGINT  NOT NULL DEFAULT 0,
    like_count  BIGINT  NOT NULL DEFAULT 0,
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE
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
    id          BIGSERIAL   PRIMARY KEY,
    member_id   BIGINT      NOT NULL,
    ended       BOOLEAN     NOT NULL DEFAULT FALSE,
    removed     BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
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
    vote_post_id  BIGINT  PRIMARY KEY,
    view_count    BIGINT  NOT NULL DEFAULT 0,
    like_count    BIGINT  NOT NULL DEFAULT 0,
    FOREIGN KEY (vote_post_id) REFERENCES vote_post (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS vote_post_item
(
    id            BIGSERIAL  PRIMARY KEY,
    vote_post_id  BIGINT     NOT NULL,
    position      SMALLINT   NOT NULL CHECK (position IN (1, 2)),
    won           BOOLEAN    NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
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
    id    BIGSERIAL    PRIMARY KEY,
    name  VARCHAR(50)  UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS plan_post
(
    id                 BIGSERIAL      PRIMARY KEY,
    member_id          BIGINT         NOT NULL,
    plan_post_city_id  BIGINT         NOT NULL,
    title              VARCHAR(50)    NOT NULL,
    summary            TEXT           NOT NULL DEFAULT '',
    total_rating       NUMERIC(3, 2)  NOT NULL DEFAULT 0.00 CHECK (total_rating >= 0.00 AND total_rating <= 5.00),
    removed            BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at         TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    removed_at         TIMESTAMP      NULL,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE,
    FOREIGN KEY (plan_post_city_id) REFERENCES plan_post_city (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS plan_post_item
(
    id            BIGSERIAL              PRIMARY KEY,
    plan_post_id  BIGINT                 NOT NULL,
    place_id      TEXT                   NOT NULL,
    place_name    TEXT                   NOT NULL,
    description   TEXT                   NOT NULL DEFAULT '',
    coordinates   GEOMETRY(POINT, 4326)  NOT NULL,
    rating        NUMERIC(3, 2)          NOT NULL DEFAULT 0.00 CHECK (rating >= 0.00 AND rating <= 5.00),
    removed       BOOLEAN                NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP              NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP              NOT NULL DEFAULT CURRENT_TIMESTAMP,
    removed_at    TIMESTAMP              NULL,
    previous_id   BIGINT                 NULL,
    next_id       BIGINT                 NULL,
    FOREIGN KEY (plan_post_id) REFERENCES plan_post (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_plan_post_item_coordinates_gist ON plan_post_item USING GIST (coordinates);

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
    plan_post_id  BIGINT  PRIMARY KEY,
    view_count    BIGINT  NOT NULL DEFAULT 0,
    like_count    BIGINT  NOT NULL DEFAULT 0,
    FOREIGN KEY (plan_post_id) REFERENCES plan_post (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS photo
(
    id          BIGSERIAL     PRIMARY KEY,
    member_id   BIGINT        NULL,
    uuid        VARCHAR(255)  UNIQUE NOT NULL,
    filename    VARCHAR(255)  NOT NULL,
    extension   VARCHAR(255)  NOT NULL,
    removed     BOOLEAN       NOT NULL DEFAULT FALSE,
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
    id          BIGSERIAL     PRIMARY KEY,
    member_id   BIGINT        NOT NULL,
    name        VARCHAR(100)  NOT NULL UNIQUE,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member (id)
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
    id               BIGSERIAL     PRIMARY KEY,
    member_id        BIGINT        NOT NULL,
    parent_reply_id  BIGINT        NULL,
    content          TEXT          NOT NULL DEFAULT '',
    updated          BOOLEAN       NOT NULL DEFAULT FALSE,
    removed          BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
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

