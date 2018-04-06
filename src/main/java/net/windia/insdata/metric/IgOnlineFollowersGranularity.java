package net.windia.insdata.metric;

import net.windia.insdata.model.internal.IgOnlineFollowers;
import net.windia.insdata.repository.IgOnlineFollowersRepository;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum IgOnlineFollowersGranularity {

    HOURLY("hourly"),
    DAILY("daily"),
    AGGREGATE_HOUR("aggregate_hour"),
    AGGREGATE_WEEKDAY("aggregate_weekday"),
    AGGREGATE_HOUR_WEEKDAY("aggregate_hour_weekday");

    private final String value;

    private DataExtractor extractor;

    private static final Map<String, IgOnlineFollowersGranularity> valueToInstanceMap =
            EnumSet.allOf(IgOnlineFollowersGranularity.class).stream().collect(
                        Collectors.toMap(IgOnlineFollowersGranularity::getValue, Function.identity()));

    static {
        HOURLY.extractor = IgOnlineFollowersRepository::findByIgProfileIdAndDateTimeBetweenOrderByDateTimeAsc;
        DAILY.extractor = IgOnlineFollowersRepository::findDailyByIgProfileIdAndDateRange;
        AGGREGATE_HOUR.extractor = (repo, profileId, since, until) -> repo.findAggregateHourByProfileId(profileId);
        AGGREGATE_WEEKDAY.extractor = (repo, profileId, since, until) -> repo.findAggregateWeekdayByProfileId(profileId);
        AGGREGATE_HOUR_WEEKDAY.extractor = (repo, profileId, since, until) -> repo.findAggregateHourAndWeekdayByProfileId(profileId);
    }

    IgOnlineFollowersGranularity(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public List<IgOnlineFollowers> extractData(IgOnlineFollowersRepository repo, Long igProfileId, OffsetDateTime since, OffsetDateTime until) {
        return extractor.extract(repo, igProfileId, since, until);
    }

    public static boolean accepts(String value) {
        return valueToInstanceMap.keySet().contains(value);
    }

    public static IgOnlineFollowersGranularity forName(String value) {
        return valueToInstanceMap.get(value);
    }

    interface DataExtractor {
        List<IgOnlineFollowers> extract(IgOnlineFollowersRepository repo, Long igProfileId, OffsetDateTime since, OffsetDateTime until);
    }
}
