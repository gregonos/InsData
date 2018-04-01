ALTER TABLE insdata.ig_profile_snapshot_daily CHANGE COLUMN captured_at
  captured_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE insdata.ig_profile_diff_daily CHANGE COLUMN compared_to
  compared_to TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE insdata.ig_profile_snapshot_hourly CHANGE COLUMN captured_at
  captured_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE insdata.ig_profile_diff_hourly CHANGE COLUMN compared_to
  compared_to TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE insdata.ig_profile_audience_daily CHANGE COLUMN captured_at
  captured_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE insdata.ig_media_snapshot_daily CHANGE COLUMN captured_at
  captured_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE insdata.ig_media_diff_daily CHANGE COLUMN compared_to
  compared_to TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE insdata.ig_media_snapshot_hourly CHANGE COLUMN captured_at
  captured_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE insdata.ig_media_diff_hourly CHANGE COLUMN compared_to
  compared_to TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;


ALTER TABLE insdata.ig_profile_diff_daily ADD captured_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL AFTER compared_to;

UPDATE ig_profile_diff_daily d
SET d.captured_at = (SELECT s.captured_at FROM ig_profile_snapshot_daily s WHERE s.captured_at > d.compared_to ORDER BY s.captured_at ASC LIMIT 0, 1);

UPDATE ig_profile_diff_daily d, ig_profile_snapshot_daily s
SET
  d.new_followers = s.new_followers,
  d.impressions = s.impressions,
  d.reach = s.reach,
  d.profile_views = s.profile_views,
  d.email_contacts = s.email_contacts,
  d.phone_call_clicks = s.phone_call_clicks,
  d.get_directions_clicks = s.get_directions_clicks,
  d.website_clicks = s.website_clicks
WHERE d.captured_at = s.captured_at;


ALTER TABLE insdata.ig_profile_diff_hourly ADD captured_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL AFTER compared_to;

UPDATE ig_profile_diff_hourly d
SET d.captured_at = (SELECT s.captured_at FROM ig_profile_snapshot_hourly s WHERE s.captured_at > d.compared_to ORDER BY s.captured_at ASC LIMIT 0, 1);

ALTER TABLE insdata.ig_media_diff_daily ADD captured_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL AFTER compared_to;

UPDATE ig_media_diff_daily d
SET d.captured_at = (SELECT s.captured_at FROM ig_media_snapshot_daily s WHERE s.captured_at > d.compared_to ORDER BY s.captured_at ASC LIMIT 0, 1);

ALTER TABLE insdata.ig_media_diff_hourly ADD captured_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL AFTER compared_to;

UPDATE ig_media_diff_hourly d
SET d.captured_at = (SELECT s.captured_at FROM ig_media_snapshot_hourly s WHERE s.captured_at > d.compared_to ORDER BY s.captured_at ASC LIMIT 0, 1);
