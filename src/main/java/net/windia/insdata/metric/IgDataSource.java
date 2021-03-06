package net.windia.insdata.metric;

public enum IgDataSource {

    SNAPSHOT("Snapshots"),
    SNAPSHOT_HOURLY("Snapshots", StatGranularity.HOURLY),
    DIFF("Diffs"),
    DIFF_HOURLY("Diffs", StatGranularity.HOURLY),
    POST_SNAPSHOT("PostSnapshots"),
    LATEST_SNAPSHOT_OF_POSTS("LatestSnapshotOfPosts", StatGranularity.HOURLY),
    POST_DIFF("PostDiffs"),
    POST_DIFF_HOURLY("PostDiffs", StatGranularity.HOURLY),
    DIFFS_OF_POSTS("DiffsOfPosts", StatGranularity.HOURLY),
    POST_DIFF_BY_DATE("PostDiffsByDate"),
    POSTS("Posts"),
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
