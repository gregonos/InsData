package net.windia.insdata.constants;

public enum IgDataSource {

    SNAPSHOT("Snapshots"),
    DIFF("Diffs"),
    POST_SNAPSHOT("PostSnapshots"),
    POST_DIFF("PostDiffs"),
    POST_DIFF_BY_DATE("PostDiffsByDate"),
    ;

    private String name;

    IgDataSource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
