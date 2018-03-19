package net.windia.insdata.constants;


import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum IgDiffMetric {

    FOLLOWERS("followers_diff", "followers"),
    FOLLOWINGS("followings_diff", "follows"),
    POSTS("posts_diff", "mediaCount"),
    NEW_FOLLOWERS("new_followers_diff", "newFollowers"),
    IMPRESSIONS("impressions_diff", "impressions"),
    REACH("reach_diff", "reach"),
    PROFILE_VIEWS("profile_views_diff", "profileViews"),
    WEBSITE_CLICKS("website_clicks_diff", "websiteClicks"),
    PHONE_CALL_CLICKS("phone_call_clicks_diff", "phoneCallClicks"),
    GET_DIRECTIONS_CLICKS("get_directions_clicks_diff", "getDirectionsClicks"),
    EMAIL_CONTACTS("email_contacts_diff", "emailContracts");

    private final String value;
    private final String fieldName;

    private static final Map<String, IgDiffMetric> valueToInstanceMap = new HashMap<>();

    static {
        for (IgDiffMetric metric : EnumSet.allOf(IgDiffMetric.class)) {
            valueToInstanceMap.put(metric.getValue(), metric);
        }
    }

    IgDiffMetric(String value, String fieldName) {
        this.value = value;
        this.fieldName = fieldName;
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

    public static IgDiffMetric forName(String value) {
        return valueToInstanceMap.get(value);
    }
}
