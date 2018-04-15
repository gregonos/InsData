package net.windia.insdata.model.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class IgProfileAudienceStatsDTO extends IgProfileStatsDTO {

    private String type;

    private  ZonedDateTime seenAt;
}
