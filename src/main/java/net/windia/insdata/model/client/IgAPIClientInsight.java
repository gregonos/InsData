package net.windia.insdata.model.client;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class IgAPIClientInsight<DataType> implements Serializable {

    public static final String NAME_IMPRESSIONS = "impressions";
    public static final String NAME_REACH = "reach";
    public static final String NAME_PROFILE_VIEWS = "profile_views";
    public static final String NAME_FOLLOWER_COUNT = "follower_count";

    private String id;

    private String name;

    private String period;

    private String title;

    private String description;

    private List<IgAPIClientDataEntry<DataType>> values;
}
