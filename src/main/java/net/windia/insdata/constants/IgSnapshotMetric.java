package net.windia.insdata.constants;


import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum IgSnapshotMetric {

    FOLLOWERS("followers"),
    FOLLOWINGS("followings", "follows"),
    POSTS("posts", "mediaCount"),
    NEW_FOLLOWERS("new_followers", "newFollowers"),
    IMPRESSIONS("impressions"),
    REACH("reach"),
    PROFILE_VIEWS("profile_views", "profileViews"),
    WEBSITE_CLICKS("website_clicks", "websiteClicks"),
    PHONE_CALL_CLICKS("phone_call_clicks", "phoneCallClicks"),
    GET_DIRECTIONS_CLICKS("get_directions_clicks", "getDirectionClicks"),
    EMAIL_CONTACTS("email_contacts", "emailContacts");

    private final String value;
    private final String fieldName;

    private static final Map<String, IgSnapshotMetric> valueToInstanceMap = new HashMap<>();

    static {
        for (IgSnapshotMetric metric : EnumSet.allOf(IgSnapshotMetric.class)) {
            valueToInstanceMap.put(metric.getValue(), metric);
        }
    }

    IgSnapshotMetric(String value, String fieldName) {
        this.value = value;
        this.fieldName = fieldName;
    }

    IgSnapshotMetric(String value) {
        this(value, value);
    }


    public String getValue() {
        return value;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static boolean accepts(String value) {
        return valueToInstanceMap.keySet().contains(value);
    }

    public static IgSnapshotMetric forName(String value) {
        return valueToInstanceMap.get(value);
    }
}
