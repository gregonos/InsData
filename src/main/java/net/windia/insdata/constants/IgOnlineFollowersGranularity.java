package net.windia.insdata.constants;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum IgOnlineFollowersGranularity {

    HOURLY("hourly"),
    DAILY("daily"),
    AGGREGATE_HOUR("aggregate_hour"),
    AGGREGATE_WEEKDAY("aggregate_weekday"),
    AGGREGATE_HOUR_WEEKDAY("aggregate_hour_weekday");

    private final String value;

    private static final Map<String, IgOnlineFollowersGranularity> valueToInstanceMap = new HashMap<>();

    static {
        for (IgOnlineFollowersGranularity metric : EnumSet.allOf(IgOnlineFollowersGranularity.class)) {
            valueToInstanceMap.put(metric.getValue(), metric);
        }
    }

    IgOnlineFollowersGranularity(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean accepts(String value) {
        return valueToInstanceMap.keySet().contains(value);
    }

    public static IgOnlineFollowersGranularity forName(String value) {
        return valueToInstanceMap.get(value);
    }
}
