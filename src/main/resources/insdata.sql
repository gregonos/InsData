CREATE TABLE ig_media
(
  id            VARCHAR(20)   NOT NULL
    PRIMARY KEY,
  ig_profile_id BIGINT        NOT NULL,
  ig_id         VARCHAR(20)   NOT NULL,
  caption       VARCHAR(1024) NULL
  COLLATE utf8mb4_unicode_ci,
  type          VARCHAR(16)   NOT NULL,
  permalink     VARCHAR(256)  NULL,
  shortcode     VARCHAR(16)   NOT NULL,
  thumbnail_url VARCHAR(256)  NULL,
  url           VARCHAR(256)  NULL,
  created_at    TIMESTAMP      NOT NULL
)
  ENGINE = InnoDB
  CHARSET = utf8;

CREATE INDEX ig_media_profile_id_index
  ON ig_media (ig_profile_id);

CREATE TABLE ig_media_diff_daily
(
  id            BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  media_id      VARCHAR(20)                         NOT NULL,
  ig_profile_id BIGINT                              NOT NULL,
  media_type    VARCHAR(16)                         NOT NULL,
  compared_to   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  impressions   INT                                 NOT NULL,
  reach         INT                                 NOT NULL,
  engagement    INT                                 NOT NULL,
  saved         INT                                 NOT NULL,
  video_views   INT DEFAULT '0'                     NOT NULL,
  likes         INT                                 NOT NULL,
  comments      INT                                 NOT NULL,
  week          DATE                                NOT NULL,
  month         TINYINT                             NOT NULL,
  weekday       TINYINT                             NOT NULL,
  CONSTRAINT ig_media_diff_daily_ig_media_id_fk
  FOREIGN KEY (media_id) REFERENCES ig_media (id)
)
  ENGINE = InnoDB
  CHARSET = utf8;

CREATE INDEX ig_media_diff_daily_media_id_index
  ON ig_media_diff_daily (media_id);

CREATE INDEX ig_media_diff_daily_profile_id_index
  ON ig_media_diff_daily (ig_profile_id);

CREATE INDEX ig_media_diff_daily_week_index
  ON ig_media_diff_daily (week);

CREATE INDEX ig_media_diff_daily_month_index
  ON ig_media_diff_daily (month);

CREATE INDEX ig_media_diff_daily_weekday_index
  ON ig_media_diff_daily (weekday);

CREATE INDEX ig_media_diff_daily_media_type_index
  ON ig_media_diff_daily (media_type);

CREATE TABLE ig_media_diff_hourly
(
  id            BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  media_id      VARCHAR(20)                         NOT NULL,
  ig_profile_id BIGINT                              NOT NULL,
  media_type    VARCHAR(16)                         NOT NULL,
  compared_to   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  impressions   INT                                 NOT NULL,
  reach         INT                                 NOT NULL,
  engagement    INT                                 NOT NULL,
  saved         INT                                 NOT NULL,
  video_views   INT DEFAULT '0'                     NOT NULL,
  likes         INT                                 NOT NULL,
  comments      INT                                 NOT NULL,
  hour          TINYINT                             NOT NULL,
  CONSTRAINT ig_media_diff_hourly_ig_media_id_fk
  FOREIGN KEY (media_id) REFERENCES ig_media (id)
)
  ENGINE = InnoDB
  CHARSET = utf8;

CREATE INDEX ig_media_diff_hourly_media_id_index
  ON ig_media_diff_hourly (media_id);

CREATE INDEX ig_media_diff_hourly_profile_id_index
  ON ig_media_diff_hourly (ig_profile_id);

CREATE INDEX ig_media_diff_hourly_media_type_index
  ON ig_media_diff_hourly (media_type);

CREATE INDEX ig_media_diff_hourly_hour_index
  ON ig_media_diff_hourly (hour);

CREATE TABLE ig_media_snapshot_daily
(
  id            BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  media_id      VARCHAR(20)                         NOT NULL,
  ig_profile_id BIGINT                              NOT NULL,
  media_type    VARCHAR(16)                         NOT NULL,
  captured_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  impressions   INT                                 NOT NULL,
  reach         INT                                 NOT NULL,
  engagement    INT                                 NOT NULL,
  saved         INT                                 NOT NULL,
  video_views   INT DEFAULT '0'                     NOT NULL,
  likes         INT                                 NOT NULL,
  comments      INT                                 NOT NULL,
  week          DATE                                NOT NULL,
  month         TINYINT                             NOT NULL,
  weekday       TINYINT                             NOT NULL,
  CONSTRAINT ig_media_snapshot_daily_ig_media_id_fk
  FOREIGN KEY (media_id) REFERENCES ig_media (id)
)
  ENGINE = InnoDB
  CHARSET = utf8;

CREATE INDEX ig_media_snapshot_daily_media_id_index
  ON ig_media_snapshot_daily (media_id);

CREATE INDEX ig_media_snapshot_daily_profile_id_index
  ON ig_media_snapshot_daily (ig_profile_id);

CREATE INDEX ig_media_snapshot_daily_week_index
  ON ig_media_snapshot_daily (week);

CREATE INDEX ig_media_snapshot_daily_month_index
  ON ig_media_snapshot_daily (month);

CREATE INDEX ig_media_snapshot_daily_weekday_index
  ON ig_media_snapshot_daily (weekday);

CREATE INDEX ig_media_snapshot_daily_media_type_index
  ON ig_media_snapshot_daily (media_type);

CREATE TABLE ig_media_snapshot_hourly
(
  id            BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  media_id      VARCHAR(20)                         NOT NULL,
  ig_profile_id BIGINT                              NOT NULL,
  media_type    VARCHAR(16)                         NOT NULL,
  captured_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  impressions   INT                                 NOT NULL,
  reach         INT                                 NOT NULL,
  engagement    INT                                 NOT NULL,
  saved         INT                                 NOT NULL,
  video_views   INT DEFAULT '0'                     NOT NULL,
  likes         INT                                 NOT NULL,
  comments      INT                                 NOT NULL,
  hour          TINYINT                             NOT NULL,
  CONSTRAINT ig_media_snapshot_hourly_ig_media_id_fk
  FOREIGN KEY (media_id) REFERENCES ig_media (id)
)
  ENGINE = InnoDB
  CHARSET = utf8;

CREATE INDEX ig_media_snapshot_hourly_media_id_index
  ON ig_media_snapshot_hourly (media_id);

CREATE INDEX ig_media_snapshot_hourly_profile_id_index
  ON ig_media_snapshot_hourly (ig_profile_id);

CREATE INDEX ig_media_snapshot_hourly_media_type_index
  ON ig_media_snapshot_hourly (media_type);

CREATE INDEX ig_media_snapshot_hourly_hour_index
  ON ig_media_snapshot_hourly (hour);

CREATE TABLE ig_online_followers
(
  id            BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  ig_profile_id BIGINT  NOT NULL,
  date          DATE    NOT NULL,
  hour          TINYINT NOT NULL,
  count         INT     NOT NULL,
  weekday       TINYINT NOT NULL,
  percentage    FLOAT   NOT NULL
)
  ENGINE = InnoDB
  CHARSET = utf8;

CREATE INDEX ig_online_followers_profile_id_date_index
  ON ig_online_followers (ig_profile_id, date);

CREATE INDEX ig_online_followers_profile_id_hour_index
  ON ig_online_followers (ig_profile_id, hour);

CREATE INDEX ig_online_followers_profile_id_weekday_index
  ON ig_online_followers (ig_profile_id, weekday);

CREATE TABLE ig_profile
(
  id                  BIGINT       NOT NULL
    PRIMARY KEY,
  business_account_id VARCHAR(20)  NULL,
  ig_id               VARCHAR(10)  NULL,
  biography           VARCHAR(256) NULL,
  username            VARCHAR(32)  NULL,
  name                VARCHAR(256) NULL,
  picture_url         VARCHAR(256) NULL,
  website             VARCHAR(256) NULL
)
  COMMENT 'The Instagram extension of a profile'
  ENGINE = InnoDB;

ALTER TABLE ig_media
  ADD CONSTRAINT ig_media_ig_profile_id_fk
FOREIGN KEY (ig_profile_id) REFERENCES ig_profile (id);

ALTER TABLE ig_media_diff_daily
  ADD CONSTRAINT ig_media_diff_daily_ig_profile_id_fk
FOREIGN KEY (ig_profile_id) REFERENCES ig_profile (id);

ALTER TABLE ig_media_diff_hourly
  ADD CONSTRAINT ig_media_diff_hourly_ig_profile_id_fk
FOREIGN KEY (ig_profile_id) REFERENCES ig_profile (id);

ALTER TABLE ig_media_snapshot_daily
  ADD CONSTRAINT ig_media_snapshot_daily_ig_profile_id_fk
FOREIGN KEY (ig_profile_id) REFERENCES ig_profile (id);

ALTER TABLE ig_media_snapshot_hourly
  ADD CONSTRAINT ig_media_snapshot_hourly_ig_profile_id_fk
FOREIGN KEY (ig_profile_id) REFERENCES ig_profile (id);

ALTER TABLE ig_online_followers
  ADD CONSTRAINT ig_online_followers_ig_profile_id_fk
FOREIGN KEY (ig_profile_id) REFERENCES ig_profile (id);

CREATE TABLE ig_profile_audience_daily
(
  id            BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  ig_profile_id BIGINT                              NOT NULL,
  captured_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  type          VARCHAR(16)                         NOT NULL,
  section       VARCHAR(64)                         NOT NULL,
  count         INT                                 NOT NULL,
  diff          INT                                 NULL,
  CONSTRAINT ig_profile_audience_daily_ig_profile_id_fk
  FOREIGN KEY (ig_profile_id) REFERENCES ig_profile (id)
)
  ENGINE = InnoDB
  CHARSET = utf8;

CREATE INDEX ig_profile_audience_daily_profile_id_type_index
  ON ig_profile_audience_daily (ig_profile_id, type);

CREATE TABLE ig_profile_diff_daily
(
  id                    BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  ig_profile_id         BIGINT                              NOT NULL,
  compared_to           TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  media_count           INT                                 NOT NULL,
  followers             INT                                 NOT NULL,
  follows               INT                                 NOT NULL,
  new_followers         INT                                 NOT NULL,
  impressions           INT                                 NOT NULL,
  reach                 INT                                 NOT NULL,
  profile_views         INT                                 NOT NULL,
  email_contacts        INT                                 NOT NULL,
  phone_call_clicks     INT                                 NOT NULL,
  get_directions_clicks INT                                 NOT NULL,
  website_clicks        INT                                 NOT NULL,
  week                  DATE                                NOT NULL,
  month                 TINYINT                             NOT NULL,
  weekday               TINYINT                             NOT NULL,
  CONSTRAINT ig_profile_diff_daily_ig_profile_id_fk
  FOREIGN KEY (ig_profile_id) REFERENCES ig_profile (id)
)
  ENGINE = InnoDB
  CHARSET = utf8;

CREATE INDEX ig_profile_diff_daily_profile_id_week_index
  ON ig_profile_diff_daily (ig_profile_id, week);

CREATE INDEX ig_profile_diff_daily_profile_id_month_index
  ON ig_profile_diff_daily (ig_profile_id, month);

CREATE INDEX ig_profile_diff_daily_profile_id_weekday_index
  ON ig_profile_diff_daily (ig_profile_id, weekday);

CREATE TABLE ig_profile_diff_hourly
(
  id                    BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  ig_profile_id         BIGINT                              NOT NULL,
  compared_to           TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  media_count           INT                                 NOT NULL,
  followers             INT                                 NOT NULL,
  follows               INT                                 NOT NULL,
  new_followers         INT                                 NOT NULL,
  impressions           INT                                 NOT NULL,
  reach                 INT                                 NOT NULL,
  profile_views         INT                                 NOT NULL,
  website_clicks        INT                                 NOT NULL,
  get_directions_clicks INT                                 NOT NULL,
  phone_call_clicks     INT                                 NOT NULL,
  email_contacts        INT                                 NOT NULL,
  hour                  TINYINT                             NOT NULL,
  CONSTRAINT ig_profile_diff_hourly_ig_profile_id_fk
  FOREIGN KEY (ig_profile_id) REFERENCES ig_profile (id)
)
  ENGINE = InnoDB
  CHARSET = utf8;

CREATE INDEX ig_profile_diff_hourly_profile_id_hour_index
  ON ig_profile_diff_hourly (ig_profile_id, hour);

CREATE TABLE ig_profile_snapshot_daily
(
  id                    BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  ig_profile_id         BIGINT                              NOT NULL,
  captured_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  media_count           INT                                 NOT NULL,
  followers             INT                                 NOT NULL,
  follows               INT                                 NOT NULL,
  new_followers         INT                                 NOT NULL,
  impressions           INT                                 NOT NULL,
  reach                 INT                                 NOT NULL,
  profile_views         INT                                 NOT NULL,
  email_contacts        INT                                 NOT NULL,
  phone_call_clicks     INT                                 NOT NULL,
  get_directions_clicks INT                                 NOT NULL,
  website_clicks        INT                                 NOT NULL,
  week                  DATE                                NOT NULL,
  month                 TINYINT                             NOT NULL,
  weekday               TINYINT                             NOT NULL,
  CONSTRAINT ig_profile_snapshot_daily_ig_profile_id_fk
  FOREIGN KEY (ig_profile_id) REFERENCES ig_profile (id)
)
  ENGINE = InnoDB
  CHARSET = utf8;

CREATE INDEX ig_profile_snapshot_daily_profile_id_week_index
  ON ig_profile_snapshot_daily (ig_profile_id, week);

CREATE INDEX ig_profile_snapshot_daily_profile_id_month_index
  ON ig_profile_snapshot_daily (ig_profile_id, month);

CREATE INDEX ig_profile_snapshot_daily_profile_id_weekday_index
  ON ig_profile_snapshot_daily (ig_profile_id, weekday);

CREATE TABLE ig_profile_snapshot_hourly
(
  id                    BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  ig_profile_id         BIGINT                              NOT NULL,
  captured_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  media_count           INT                                 NOT NULL,
  followers             INT                                 NOT NULL,
  follows               INT                                 NOT NULL,
  new_followers         INT                                 NOT NULL,
  impressions           INT                                 NOT NULL,
  reach                 INT                                 NOT NULL,
  profile_views         INT                                 NOT NULL,
  website_clicks        INT                                 NOT NULL,
  get_directions_clicks INT                                 NOT NULL,
  phone_call_clicks     INT                                 NOT NULL,
  email_contacts        INT                                 NOT NULL,
  hour                  TINYINT                             NOT NULL,
  CONSTRAINT ig_profile_snapshot_hourly_ig_profile_id_fk
  FOREIGN KEY (ig_profile_id) REFERENCES ig_profile (id)
)
  ENGINE = InnoDB
  CHARSET = utf8;

CREATE INDEX ig_profile_snapshot_hourly_profile_id_hour_index
  ON ig_profile_snapshot_hourly (ig_profile_id, hour);

CREATE TABLE profile
(
  id              BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  user_id         BIGINT       NULL,
  type            VARCHAR(16)  NOT NULL,
  token           VARCHAR(256) NULL,
  created_at      TIMESTAMP     NOT NULL,
  last_updated_at TIMESTAMP     NOT NULL
)
  COMMENT 'A social media profile belonging to the user'
  ENGINE = InnoDB
  CHARSET = utf8;

CREATE INDEX profile_user_id_fk
  ON profile (user_id);

CREATE TABLE user
(
  id         BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  email      VARCHAR(255)                NOT NULL,
  first_name VARCHAR(128)                NULL,
  last_name  VARCHAR(128)                NULL,
  time_zone  VARCHAR(8) DEFAULT '+00:00' NULL,
  password   VARCHAR(64)                 NOT NULL,
  CONSTRAINT user_email_uindex
  UNIQUE (email)
)
  COMMENT 'User of InsData service'
  ENGINE = InnoDB
  CHARSET = utf8;

ALTER TABLE profile
  ADD CONSTRAINT profile_user_id_fk
FOREIGN KEY (user_id) REFERENCES user (id);

