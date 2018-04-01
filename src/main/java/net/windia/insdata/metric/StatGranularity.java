package net.windia.insdata.metric;


import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum StatGranularity {

    HOURLY("hourly"),
    DAILY("daily");

    private static final Map<String, StatGranularity> valueToInstanceMap = new HashMap<>();

    private String value;

    static {
        for (StatGranularity granularity : EnumSet.allOf(StatGranularity.class)) {
            valueToInstanceMap.put(granularity.getValue(), granularity);
        }
    }

    StatGranularity(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static StatGranularity forName(String value) {
        return valueToInstanceMap.get(value);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
