package net.windia.insdata.metric;

public enum IgDataSource {

    SNAPSHOT("Snapshots"),
    SNAPSHOT_HOURLY("Snapshots", StatGranularity.HOURLY),
    DIFF("Diffs"),
    DIFF_HOURLY("Diffs", StatGranularity.HOURLY),
    POST_SNAPSHOT("PostSnapshots"),
    POST_DIFF("PostDiffs"),
    POST_DIFF_BY_DATE("PostDiffsByDate"),
    ;

    private String name;
    private StatGranularity granularity;

    IgDataSource(String name) {
        this.name = name;
    }

    IgDataSource(String name, StatGranularity granularity) {
        this.name = name;
        this.granularity = granularity;
    }

    public String getName() {
        return name;
    }

    public StatGranularity getGranularity() {
        return granularity;
    }
}
