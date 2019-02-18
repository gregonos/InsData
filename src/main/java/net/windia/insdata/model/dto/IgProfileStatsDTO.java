package net.windia.insdata.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Data
public class IgProfileStatsDTO implements Serializable {

    public static final String DIMENSION_TIME = "time";
    public static final String DIMENSION_DATE = "date";
    public static final String DIMENSION_HOUR = "hour";
    public static final String DIMENSION_WEEKDAY = "weekday";
    public static final String DIMENSION_COUNT = "count";
    public static final String DIMENSION_PERCENTAGE = "percentage";
    public static final String DIMENSION_POST_ID = "post_id";
    public static final String DIMENSION_POST_URL = "post_url";

    private List<String> dimensions;

    private Collection<List<Object>> data;

}
