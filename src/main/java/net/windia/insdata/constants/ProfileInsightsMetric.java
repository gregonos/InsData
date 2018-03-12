package net.windia.insdata.constants;

public enum ProfileInsightsMetric {

    IMPRESSIONS("impressions"),
    REACH("reach"),
    PROFILE_VIEWS("profile_views"),
    FOLLOWER_COUNT("follower_count");

    private String value;

    ProfileInsightsMetric(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value;
    }

}
