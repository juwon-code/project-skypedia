USE skypedia;

CREATE TABLE IF NOT EXISTS member
(
    id                 BIGINT        AUTO_INCREMENT PRIMARY KEY,
    oauth_id           VARCHAR(255)  UNIQUE NOT NULL,
    name               VARCHAR(255)  NOT NULL,
    username           VARCHAR(20)   UNIQUE NOT NULL,
    email              VARCHAR(50)   UNIQUE NOT NULL,
    profile_image_url  VARCHAR(255)  NULL,
    withdrawn          TINYINT(1)    NOT NULL DEFAULT 0 CHECK (withdrawn IN (0, 1)),
    created_at         TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    withdrawn_at       TIMESTAMP     NULL,
    FULLTEXT INDEX f_idx_member_username (username)
);

CREATE TABLE IF NOT EXISTS member_role
(
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT       NOT NULL,
    role        VARCHAR(20)  NOT NULL CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN')),
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS notify_message
(
    id           BIGINT        AUTO_INCREMENT PRIMARY KEY,
    receiver_id  BIGINT        NULL,
    notify_type  VARCHAR(20)   NOT NULL,
    content      VARCHAR(255)  NOT NULL,
    move_url     VARCHAR(255)  NOT NULL,
    viewed       TINYINT(1)    NOT NULL DEFAULT 0 CHECK (viewed IN (0, 1)),
    sent_at      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    viewed_at    TIMESTAMP     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CHECK (notify_type IN ('REPLY', 'CHAT', 'NOTICE')),
    FOREIGN KEY (receiver_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chat_room
(
    id          BIGINT     AUTO_INCREMENT PRIMARY KEY,
    creator_id  BIGINT     NOT NULL,
    created_at  TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP  NULL,
    FOREIGN KEY (creator_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chat_participant
(
    chat_room_id    BIGINT     NOT NULL,
    participant_id  BIGINT     NOT NULL,
    joined_at       TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (chat_room_id, participant_id),
    FOREIGN KEY (chat_room_id) REFERENCES chat_room (id) ON DELETE CASCADE,
    FOREIGN KEY (participant_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chat_black_list
(
    blocker_id  BIGINT     NOT NULL,
    blocked_id  BIGINT     NOT NULL,
    joined_at   TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (blocker_id, blocked_id),
    FOREIGN KEY (blocker_id) REFERENCES member (id) ON DELETE CASCADE,
    FOREIGN KEY (blocked_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chat_message
(
    id            BIGINT        AUTO_INCREMENT PRIMARY KEY,
    chat_room_id  BIGINT        NOT NULL,
    sender_id     BIGINT        NOT NULL,
    content       VARCHAR(255)  NOT NULL DEFAULT '',
    is_photo      TINYINT(1)    NOT NULL DEFAULT 0 CHECK (is_photo IN (0, 1)),
    deleted       TINYINT(1)    NOT NULL DEFAULT 0 CHECK (deleted IN (0, 1)),
    sent_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP     NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    deleted_at    TIMESTAMP     NULL,
    FOREIGN KEY (chat_room_id) REFERENCES chat_room (id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chat_message_read
(
    message_id  BIGINT     NOT NULL,
    reader_id   BIGINT     NOT NULL,
    read_at     TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (message_id, reader_id),
    FOREIGN KEY (message_id) REFERENCES chat_message (id) ON DELETE CASCADE,
    FOREIGN KEY (reader_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS post_category
(
    id           BIGINT         AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100)   UNIQUE NOT NULL,
    description  VARCHAR(1000)  NOT NULL DEFAULT '',
    created_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS post
(
    id           BIGINT         AUTO_INCREMENT PRIMARY KEY,
    writer_id    BIGINT         NOT NULL,
    category_id  BIGINT         NOT NULL,
    title        VARCHAR(50)    NOT NULL,
    content      TEXT           NOT NULL DEFAULT '',
    deleted      TINYINT(1)     NOT NULL DEFAULT 0 CHECK (deleted IN (0, 1)),
    rating       DECIMAL(3, 2)  NULL CHECK (rating BETWEEN 0.00 AND 5.00),
    written_at   TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at   TIMESTAMP      NULL,
    FOREIGN KEY (writer_id) REFERENCES member (id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES post_category (id) ON DELETE CASCADE,
    FULLTEXT INDEX f_idx_post_title (title),
    FULLTEXT INDEX f_idx_post_title_content (title, content)
);

CREATE TABLE IF NOT EXISTS post_metrics
(
    post_id     BIGINT  NOT NULL PRIMARY KEY,
    view_count  BIGINT  NOT NULL DEFAULT '0',
    like_count  BIGINT  NOT NULL DEFAULT '0',
);

CREATE TABLE IF NOT EXISTS post_scrap
(
    post_id      BIGINT NOT NULL,
    scrapper_id  BIGINT NOT NULL,
    PRIMARY KEY (post_id, scrapper_id),
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE,
    FOREIGN KEY (scrapper_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS post_likes
(
    post_id   BIGINT NOT NULL,
    liker_id  BIGINT NOT NULL,
    PRIMARY KEY (post_id, liker_id),
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE,
    FOREIGN KEY (liker_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS select_post
(
    id               BIGINT      AUTO_INCREMENT PRIMARY KEY,
    writer_id        BIGINT      NOT NULL,
    vote_ended       TINYINT(1)  NOT NULL DEFAULT 0 CHECK (vote_ended IN (0, 1)),
    deleted          TINYINT(1)  NOT NULL DEFAULT 0 CHECK (deleted IN (0, 1)),
    created_at       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    vote_started_at  TIMESTAMP   NOT NULL,
    vote_ended_at    TIMESTAMP   NOT NULL,
    deleted_at       TIMESTAMP   NULL,
    FOREIGN KEY (writer_id) REFERENCES member (id) ON DELETE CASCADE
 );

CREATE TABLE IF NOT EXISTS select_post_item
(
    id              BIGINT      AUTO_INCREMENT NOT NULL,
    select_post_id  BIGINT      NOT NULL,
    position        TINYINT(1)  NOT NULL CHECK (position IN (1, 2)),
    is_winner       TINYINT(1)  NOT NULL DEFAULT 0 CHECK (is_winner IN (0, 1)),
    like_count      BIGINT      NOT NULL DEFAULT 0,
    FOREIGN KEY (select_post_id) REFERENCES select_post (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS select_post_scrap
(
    select_post_id  BIGINT NOT NULL,
    scrapper_id     BIGINT NOT NULL,
    PRIMARY KEY (select_post_id, scrapper_id),
    FOREIGN KEY (select_post_id) REFERENCES select_post (id) ON DELETE CASCADE,
    FOREIGN KEY (scrapper_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS select_post_likes
(
    select_post_id  BIGINT NOT NULL,
    liker_id        BIGINT NOT NULL,
    PRIMARY KEY (select_post_id, liker_id),
    FOREIGN KEY (select_post_id) REFERENCES select_post (id) ON DELETE CASCADE,
    FOREIGN KEY (liker_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS select_post_metrics
(
    select_post_id    BIGINT  NOT NULL PRIMARY KEY,
    view_count        BIGINT  NOT NULL,
    total_like_count  BIGINT  NOT NULL
);

CREATE TABLE IF NOT EXISTS plan_post_city
(
    id    BIGINT       AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(50)  UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS plan_post
(
    id            BIGINT         AUTO_INCREMENT PRIMARY KEY,
    writer_id     BIGINT         NOT NULL,
    city_id       BIGINT         NOT NULL,
    title         VARCHAR(50)    NOT NULL,
    summary       VARCHAR(255)   NOT NULL DEFAULT '',
    total_rating  DECIMAL(3, 2)  NOT NULL DEFAULT '0.00' CHECK (total_rating BETWEEN 0.00 AND 5.00),
    deleted       TINYINT(1)     NOT NULL DEFAULT 0 CHECK (deleted IN (0, 1)),
    created_at    TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at    TIMESTAMP      NULL,
    FOREIGN KEY (writer_id) REFERENCES member (id) ON DELETE CASCADE,
    FOREIGN KEY (city_id) REFERENCES plan_post_city (id) ON DELETE CASCADE,
    FULLTEXT INDEX f_idx_plan_post_title (title),
    FULLTEXT INDEX f_idx_plan_post_title_summary (title, summary)
);

CREATE TABLE IF NOT EXISTS plan_post_item
(
    id               BIGINT         AUTO_INCREMENT PRIMARY KEY,
    plan_post_id     BIGINT         NOT NULL,
    google_place_id  VARCHAR(100)   NOT NULL UNIQUE,
    place_name       VARCHAR(255)   NOT NULL,
    description      VARCHAR(255)   NOT NULL DEFAULT '',
    coordinates      GEOMETRY       NOT NULL,
    item_rating      DECIMAL(3, 2)  NOT NULL DEFAULT '0.00' CHECK (rating BETWEEN 0.00 AND 5.00),
    deleted          TINYINT(1)     NOT NULL DEFAULT 0 CHECK (deleted IN (0, 1)),
    written_at       TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at       TIMESTAMP      NULL,
    FOREIGN KEY (plan_post_id) REFERENCES plan_post (id) ON DELETE CASCADE,
    FULLTEXT INDEX f_idx_plan_post_item_place_name (place_name),
    FULLTEXT INDEX f_idx_plan_post_item_place_name_description (place_name, description),
    SPATIAL INDEX s_idx_plan_post_coordinates (coordinates)
);

CREATE TABLE IF NOT EXISTS plan_post_scrap
(
    plan_post_id  BIGINT NOT NULL,
    scrapper_id   BIGINT NOT NULL,
    PRIMARY KEY (plan_post_id, scrapper_id),
    FOREIGN KEY (plan_post_id) REFERENCES plan_post (id) ON DELETE CASCADE,
    FOREIGN KEY (scrapper_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS plan_post_likes
(
    plan_post_id  BIGINT NOT NULL,
    liker_id BIGINT NOT NULL,
    PRIMARY KEY (plan_post_id, liker_id),
    FOREIGN KEY (plan_post_id) REFERENCES plan_post (id) ON DELETE CASCADE,
    FOREIGN KEY (liker_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS plan_post_metrics
(
    plan_post_id  BIGINT  NOT NULL PRIMARY KEY,
    view_count    BIGINT  NOT NULL DEFAULT '0',
    like_count    BIGINT  NOT NULL DEFAULT '0'
);

CREATE TABLE IF NOT EXISTS photo
(
    id               BIGINT        AUTO_INCREMENT PRIMARY KEY,
    uploader_id      BIGINT        NULL,
    uuid             VARCHAR(255)  UNIQUE NOT NULL,
    filename         VARCHAR(255)  NOT NULL,
    extension        VARCHAR(255)  NOT NULL,
    deleted          TINYINT(1)    NOT NULL DEFAULT 0 CHECK (deleted IN (0, 1)),
    uploaded_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at       TIMESTAMP     NULL,
    FOREIGN KEY (uploader_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS photo_profile
(
    photo_id   BIGINT  NOT NULL,
    member_id  BIGINT  NOT NULL,
    PRIMARY KEY (photo_id, member_id),
    FOREIGN KEY (photo_id) REFERENCES photo (id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS photo_chat_message
(
    photo_id    BIGINT  NOT NULL,
    message_id  BIGINT  NOT NULL,
    PRIMARY KEY (photo_id, message_id),
    FOREIGN KEY (photo_id) REFERENCES photo (id) ON DELETE CASCADE,
    FOREIGN KEY (message_id) REFERENCES chat_message (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS photo_post
(
    photo_id  BIGINT  NOT NULL,
    post_id   BIGINT  NOT NULL,
    PRIMARY KEY (photo_id, post_id),
    FOREIGN KEY (photo_id) REFERENCES photo (id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS photo_select_post
(
    photo_id        BIGINT  NOT NULL,
    select_post_id  BIGINT  NOT NULL,
    PRIMARY KEY (photo_id, select_post_id),
    FOREIGN KEY (photo_id) REFERENCES photo (id) ON DELETE CASCADE,
    FOREIGN KEY (select_post_id) REFERENCES select_post (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS photo_select_post_item
(
    photo_id             BIGINT  NOT NULL,
    select_post_item_id  BIGINT  NOT NULL,
    PRIMARY KEY (photo_id, select_post_item_id),
    FOREIGN KEY (photo_id) REFERENCES photo (id) ON DELETE CASCADE,
    FOREIGN KEY (select_post_item_id) REFERENCES select_post_item (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS photo_plan_post
(
    photo_id            BIGINT  NOT NULL,
    photo_plan_post_id  BIGINT  NOT NULL,
    PRIMARY KEY (photo_id, photo_plan_post_id),
    FOREIGN KEY (photo_id) REFERENCES photo (id) ON DELETE CASCADE,
    FOREIGN KEY (photo_plan_post_id) REFERENCES plan_post (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS photo_plan_post_item
(
    photo_id                 BIGINT  NOT NULL,
    photo_plan_post_item_id  BIGINT  NOT NULL,
    PRIMARY KEY (photo_id, photo_plan_post_id),
    FOREIGN KEY (photo_id) REFERENCES photo (id) ON DELETE CASCADE,
    FOREIGN KEY (photo_plan_post_item_id) REFERENCES plan_post_item (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS hashtag
(
    id    BIGINT        AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(100)  NOT NULL UNIQUE,
    FULLTEXT INDEX f_idx_hashtag_name (name)
);

CREATE TABLE IF NOT EXISTS hashtag_post
(
    hashtag_id  BIGINT  NOT NULL,
    post_id     BIGINT  NOT NULL,
    PRIMARY KEY (hashtag_id, post_id),
    FOREIGN KEY (hashtag_id) REFERENCES hashtag (id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS hashtag_select_post
(
    hashtag_id      BIGINT  NOT NULL,
    select_post_id  BIGINT  NOT NULL,
    PRIMARY KEY (hashtag_id, select_post_id),
    FOREIGN KEY (hashtag_id) REFERENCES hashtag (id) ON DELETE CASCADE,
    FOREIGN KEY (select_post_id) REFERENCES select_post (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS hashtag_plan_post
(
    hashtag_id    BIGINT  NOT NULL,
    plan_post_id  BIGINT  NOT NULL,
    PRIMARY KEY (hashtag_id, plan_post_id),
    FOREIGN KEY (hashtag_id) REFERENCES hashtag (id) ON DELETE CASCADE,
    FOREIGN KEY (plan_post_id) REFERENCES plan_post (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reply
(
    id               BIGINT        AUTO_INCREMENT PRIMARY KEY,
    replier_id       BIGINT        NOT NULL,
    parent_reply_id  BIGINT        NULL,
    content          VARCHAR(255)  NOT NULL DEFAULT '',
    likes            BIGINT        NOT NULL DEFAULT '0',
    deleted          TINYINT(1)    NOT NULL DEFAULT 0 CHECK (deleted IN (0, 1)),
    replied_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at       TIMESTAMP     NULL,
    FOREIGN KEY (replier_id) REFERENCES member (id) ON DELETE CASCADE,
    FOREIGN KEY (parent_reply_id) REFERENCES reply (id) ON DELETE SET NULL,
    FULLTEXT INDEX f_idx_reply_content (content)
);

CREATE TABLE IF NOT EXISTS reply_likes
(
    reply_id  BIGINT NOT NULL,
    liker_id  BIGINT NOT NULL,
    PRIMARY KEY (reply_id, liker_id),
    FOREIGN KEY (reply_id) REFERENCES reply (id) ON DELETE CASCADE,
    FOREIGN KEY (liker_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reply_post
(
    reply_id  BIGINT NOT NULL,
    post_id   BIGINT NOT NULL,
    PRIMARY KEY (reply_id, post_id),
    FOREIGN KEY (reply_id) REFERENCES reply (id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reply_select_post
(
    reply_id        BIGINT NOT NULL,
    select_post_id  BIGINT NOT NULL,
    PRIMARY KEY (reply_id, select_post_id),
    FOREIGN KEY (reply_id) REFERENCES reply (id) ON DELETE CASCADE,
    FOREIGN KEY (select_post_id) REFERENCES select_post (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reply_plan_post
(
    reply_id      BIGINT NOT NULL,
    plan_post_id  BIGINT NOT NULL,
    PRIMARY KEY (reply_id, plan_post_id),
    FOREIGN KEY (reply_id) REFERENCES reply (id) ON DELETE CASCADE,
    FOREIGN KEY (plan_post_id) REFERENCES plan_post (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS refresh_token
(
    member_id    BIGINT        NOT NULL,
    token        VARCHAR(255)  NOT NULL UNIQUE,
    expiry_date  TIMESTAMP     NOT NULL,
    created_at   TIMESTAMP     NOT NULL,
    updated_at   TIMESTAMP     NOT NULL,
    PRIMARY KEY (member_id),
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);