package net.windia.insdata.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class IgProfileStatsDTO implements Serializable {

    public static final String DIMENSION_TIME = "time";

    private List<String> dimensions;

    private List<List<Object>> data;

}
