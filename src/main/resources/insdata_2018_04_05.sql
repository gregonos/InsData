ALTER TABLE insdata.user MODIFY time_zone VARCHAR(63) DEFAULT 'UTC';
ALTER TABLE insdata.user ADD first_day_of_week TINYINT DEFAULT 1 NOT NULL AFTER time_zone;
ALTER TABLE insdata.ig_online_followers ADD date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL AFTER ig_profile_id;

UPDATE insdata.user SET time_zone = 'Asia/Shanghai';
UPDATE insdata.ig_profile_snapshot_daily SET month = month + 1, weekday = weekday - 1;
UPDATE insdata.ig_profile_snapshot_daily SET weekday = 7 WHERE weekday = 0;
UPDATE insdata.ig_profile_diff_daily SET month = month + 1, weekday = weekday - 1;
UPDATE insdata.ig_profile_diff_daily SET weekday = 7 WHERE weekday = 0;
UPDATE insdata.ig_media_snapshot_daily SET month = month + 1, weekday = weekday - 1;
UPDATE insdata.ig_media_snapshot_daily SET weekday = 7 WHERE weekday = 0;
UPDATE insdata.ig_media_diff_daily SET month = month + 1, weekday = weekday - 1;
UPDATE insdata.ig_media_diff_daily SET weekday = 7 WHERE weekday = 0;
UPDATE insdata.ig_online_followers SET weekday = weekday - 1;
UPDATE insdata.ig_online_followers SET weekday = 7 WHERE weekday = 0;

SET @@session.time_zone = '+08:00';
UPDATE ig_online_followers SET date_time = date + INTERVAL hour HOUR;