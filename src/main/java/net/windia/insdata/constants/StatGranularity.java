package net.windia.insdata.constants;


public enum StatGranularity {

    HOURLY("hourly"),
    DAILY("daily");

    private String value;

    StatGranularity(String value) {
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
