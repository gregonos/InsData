package net.windia.insdata.constants;

import org.apache.commons.lang3.ArrayUtils;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static net.windia.insdata.constants.IgDataSource.*;
import static net.windia.insdata.constants.StatGranularity.DAILY;
import static net.windia.insdata.constants.StatGranularity.HOURLY;
import static net.windia.insdata.constants.StatGranularity.HOURLY_AND_DAILY;

public enum IgMetric {

    FOLLOWERS("followers", HOURLY_AND_DAILY, SNAPSHOT, "followers"),
    FOLLOWERS_DIFF("followers_diff", HOURLY_AND_DAILY, DIFF, "followers"),
    FOLLOWINGS("followings", HOURLY_AND_DAILY, SNAPSHOT, "follows"),
    FOLLOWINGS_DIFF("followings_diff", HOURLY_AND_DAILY, DIFF, "follows"),

    NEW_FOLLOWERS("new_followers", HOURLY_AND_DAILY),

    POSTS("posts", HOURLY_AND_DAILY, SNAPSHOT, "mediaCount"),
    POSTS_DIFF("posts_diff", HOURLY_AND_DAILY, DIFF, "mediaCount"),

    IMPRESSIONS("impressions", HOURLY_AND_DAILY),
    REACH("reach", HOURLY_AND_DAILY),
    IMPRESSIONS_PER_REACH("impressions_per_reach", HOURLY_AND_DAILY),
    PROFILE_VIEWS("profile_views", HOURLY_AND_DAILY),

    IMPRESSIONS_POST_NEW("impressions_post_new", HOURLY_AND_DAILY),
    IMPRESSIONS_POST_EXISTING("impressions_post_existing", HOURLY_AND_DAILY),

    EMAIL_CONTACTS("email_contacts", HOURLY_AND_DAILY),
    PHONE_CALL_CLICKS("phone_call_clicks", HOURLY_AND_DAILY),
    GET_DIRECTIONS_CLICKS("get_directions_clicks", HOURLY_AND_DAILY),
    WEBSITE_CLICKS("website_clicks", HOURLY_AND_DAILY),
    ;

    private static final Map<String, IgMetric> nameToInstanceMap = new HashMap<>();

    private String name;
    private StatGranularity[] granularities;
    private IgMetricSettings settings;
    private Map<StatGranularity, IgMetricSettings> settingsPerGranularity = new HashMap<>();

    static {
        NEW_FOLLOWERS.setSettings(HOURLY, DIFF, "newFollowers");
        NEW_FOLLOWERS.setSettings(DAILY, SNAPSHOT, "newFollowers");

        IMPRESSIONS.setSettings(HOURLY, DIFF, "impressions");
        IMPRESSIONS.setSettings(DAILY, SNAPSHOT, "impressions");

        REACH.setSettings(HOURLY, DIFF, "reach");
        REACH.setSettings(DAILY, SNAPSHOT, "reach");

        IMPRESSIONS_PER_REACH.setSettings(HOURLY, DIFF, "impressionsPerReach");
        IMPRESSIONS_PER_REACH.setSettings(DAILY, SNAPSHOT, "impressionsPerReach");

        PROFILE_VIEWS.setSettings(HOURLY, DIFF, "profileViews");
        PROFILE_VIEWS.setSettings(DAILY, SNAPSHOT, "profileViews");

        IMPRESSIONS_POST_NEW.setSettings(HOURLY, POST_DIFF, "impressions");
        IMPRESSIONS_POST_NEW.setSettings(DAILY, POST_DIFF_BY_DATE, "impressions");

        IMPRESSIONS_POST_EXISTING.setSettings(HOURLY, POST_DIFF, "impressions");
        IMPRESSIONS_POST_EXISTING.setSettings(DAILY, POST_DIFF_BY_DATE, "impressions");

        EMAIL_CONTACTS.setSettings(HOURLY, DIFF, "emailContacts");
        EMAIL_CONTACTS.setSettings(DAILY, SNAPSHOT, "emailContacts");

        PHONE_CALL_CLICKS.setSettings(HOURLY, DIFF, "phoneCallClicks");
        PHONE_CALL_CLICKS.setSettings(DAILY, SNAPSHOT, "phoneCallClicks");

        GET_DIRECTIONS_CLICKS.setSettings(HOURLY, DIFF, "getDirectionsClicks");
        GET_DIRECTIONS_CLICKS.setSettings(DAILY, SNAPSHOT, "getDirectionsClicks");

        WEBSITE_CLICKS.setSettings(HOURLY, DIFF, "websiteClicks");
        WEBSITE_CLICKS.setSettings(DAILY, SNAPSHOT, "websiteClicks");

        for (IgMetric metric : EnumSet.allOf(IgMetric.class)) {
            nameToInstanceMap.put(metric.getName(), metric);
        }

    }

    IgMetric(String name, StatGranularity[] granularities) {
        this.name = name;
        this.granularities = granularities;
    }

    IgMetric(String name, StatGranularity[] granularities, IgDataSource source, String field) {
        this(name, granularities);
        this.setSettings(source, field);
    }

    public String getName() {
        return name;
    }

    public boolean supports(StatGranularity granularity) {
        return ArrayUtils.contains(this.granularities, granularity);
    }

    public void setSettings(StatGranularity gran, IgDataSource source, String field) {
        settingsPerGranularity.put(gran, new IgMetricSettings(source, field));
    }

    public void setSettings(IgDataSource source, String field) {
        this.settings = new IgMetricSettings(source, field);
    }

    public IgMetricSettings getSettings(StatGranularity granularity) {
        IgMetricSettings settingsForGran = settingsPerGranularity.get(granularity);

        return null == settingsForGran ? this.settings : settingsForGran;
    }

    public IgDataSource getSource(StatGranularity gran) {
        return getSettings(gran).source;
    }

    public String getField(StatGranularity gran) {
        return getSettings(gran).field;
    }

    public static IgMetric forName(String name) {
        return nameToInstanceMap.get(name);
    }

    public StatGranularity[] getGranularities() {
        return granularities;
    }

    class IgMetricSettings {
        IgDataSource source;
        String field;

        IgMetricSettings(IgDataSource source, String field) {
            this.source = source;
            this.field = field;
        }
    }
}
