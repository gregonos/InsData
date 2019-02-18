package net.windia.insdata.model.assembler;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.metric.IgDataSource;
import net.windia.insdata.metric.IgMetric;
import net.windia.insdata.metric.IgOnlineFollowersGranularity;
import net.windia.insdata.metric.StatGranularity;
import net.windia.insdata.model.dto.IgProfileStatsDTO;
import net.windia.insdata.model.internal.IgMedia;
import net.windia.insdata.model.internal.IgOnlineFollowers;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.IgStat;
import net.windia.insdata.model.internal.InsDataUser;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IgProfileStatsDTOAssembler {

    public IgProfileStatsDTO assemble(IgProfile igProfile,
                                      List<IgMetric> metrics,
                                      StatGranularity granInstance,
                                      Map<IgDataSource, List<? extends IgStat>> sourceMap) {

        IgProfileStatsDTO target = new IgProfileStatsDTO();

        // Dimensions
        List<String> dimensions = new ArrayList<>(metrics.size() + 1);
        dimensions.add(IgProfileStatsDTO.DIMENSION_TIME);
        metrics.forEach(metric -> dimensions.add(metric.getName().toLowerCase()));
        target.setDimensions(dimensions);

        // Data
        Map<Object, Map<String, Object>> dataMap = new LinkedHashMap<>();
        for (IgMetric metric : metrics) {

            @SuppressWarnings("unchecked")
            Map<ZonedDateTime, Object> metricResult = metric.calculate(granInstance,
                    // Fetch the metric's IgDataSource list, get the data source from sourceMap by name, and return them as a list
                    (List<List<? extends IgStat>>) metric.getSources(granInstance).stream().map(sourceMap::get).collect(Collectors.toList()));
            for (ZonedDateTime key : metricResult.keySet()) {
                // a map for each data item from dimension name to the calculated value
                Map<String, Object> dataItem = dataMap.get(key);
                if (null == dataItem) {
                    dataItem = new HashMap<>(dimensions.size() - 1);
                    dataItem.put(IgProfileStatsDTO.DIMENSION_TIME,
                            key.withZoneSameInstant(igProfile.getUser().getZoneId()).toInstant().toEpochMilli());
                    dataMap.put(key, dataItem);
                }
                dataItem.put(metric.getName().toLowerCase(), metricResult.get(key));
            }
        }

        List<List<Object>> resultDataList = new ArrayList<>(dataMap.size());
        for (Map<String, Object> dataEntry : dataMap.values()) {
            // Shrink each data item map to a list, by the order of dimensions.
            List<Object> resultDataItem = dimensions.stream().map(dataEntry::get).collect(Collectors.toList());
            resultDataList.add(resultDataItem);
        }

        // sort the result list by dimension "time"
        // Date)dataSet1.get(0)).getTime() - ((Date)dataSet2.get(0)).getTime()
        resultDataList.sort(
                (dataSet1, dataSet2) -> (int)((((long) dataSet1.get(0) / 1000) - ((long) dataSet2.get(0) / 1000))));

        target.setData(resultDataList);

        return target;
    }

    public IgProfileStatsDTO assemble(IgProfile profile, IgOnlineFollowersGranularity granularity, List<IgOnlineFollowers> onlineFollowers) {
        IgProfileStatsDTO target = new IgProfileStatsDTO();

        // Dimensions
        List<String> dimensions = new ArrayList<>(4);
        dimensions.add(IgProfileStatsDTO.DIMENSION_DATE);
        dimensions.add(IgProfileStatsDTO.DIMENSION_WEEKDAY);
        dimensions.add(IgProfileStatsDTO.DIMENSION_COUNT);
        dimensions.add(IgProfileStatsDTO.DIMENSION_PERCENTAGE);

        List<List<Object>> data = new ArrayList<>(onlineFollowers.size());
        InsDataUser user = profile.getUser();
        for (IgOnlineFollowers onlineFollower : onlineFollowers) {
            List<Object> row = new ArrayList<>(4);

            if (IgOnlineFollowersGranularity.AGGREGATE_HOUR == granularity ||
                    IgOnlineFollowersGranularity.AGGREGATE_HOUR_WEEKDAY == granularity) {
                row.add(onlineFollower.getHour());
            } else if (IgOnlineFollowersGranularity.DAILY == granularity ||
                    IgOnlineFollowersGranularity.AGGREGATE_WEEKDAY == granularity) {
                row.add(onlineFollower.getDate().format(DateTimeFormatter.ISO_DATE));
            } else {
                row.add(onlineFollower.getDateTime().atZoneSameInstant(user.getZoneId()).toInstant().toEpochMilli());
            }
            row.add(onlineFollower.getWeekday());
            row.add(onlineFollower.getCount());
            row.add(onlineFollower.getPercentage());

            data.add(row);
        }

        target.setDimensions(dimensions);
        target.setData(data);

        return target;
    }

    public IgProfileStatsDTO assemblePostStats(IgProfile profile,
                                               List<IgMetric> metrics,
                                               StatGranularity gran,
                                               Map<IgDataSource, List<? extends IgStat>> sourceMap) {

        IgProfileStatsDTO target = new IgProfileStatsDTO();

        // Dimensions
        List<String> dimensions = new ArrayList<>(3 + metrics.size());
        dimensions.add(IgProfileStatsDTO.DIMENSION_TIME);
        dimensions.add(IgProfileStatsDTO.DIMENSION_POST_ID);
        dimensions.add(IgProfileStatsDTO.DIMENSION_POST_URL);
        metrics.forEach(metric -> dimensions.add(metric.getName().toLowerCase()));
        target.setDimensions(dimensions);

        // Data
        Map<Object, Map<String, Object>> dataMap = new LinkedHashMap<>();

        // Prepare post map
        @SuppressWarnings("unchecked")
        Map<String, IgMedia> postsMap = ((List<IgMedia>) sourceMap.get(IgDataSource.POSTS)).stream()
                .collect(Collectors.toMap(IgMedia::getId, Function.identity()));

        for (IgMetric metric : metrics) {

            @SuppressWarnings("unchecked")
            Map<String, Object> metricResult = metric.calculate(gran,
                    // Fetch the metric's IgDataSource list, get the data source from sourceMap by name, and return them as a list
                    (List<List<? extends IgStat>>) metric.getSources(gran).stream().map(sourceMap::get).collect(Collectors.toList()));
            for (String postId : metricResult.keySet()) {
                // a map for each data item from dimension name to the calculated value
                Map<String, Object> dataItem = dataMap.get(postId);
                if (null == dataItem) {
                    dataItem = new HashMap<>(dimensions.size());
                    IgMedia post = postsMap.get(postId);
                    dataItem.put(IgProfileStatsDTO.DIMENSION_TIME,
                            post.getCreatedAt().atZoneSameInstant(profile.getUser().getZoneId()).toInstant().toEpochMilli());
                    dataItem.put(IgProfileStatsDTO.DIMENSION_POST_ID, postId);
                    dataItem.put(IgProfileStatsDTO.DIMENSION_POST_URL, post.getUrl());
                    dataMap.put(postId, dataItem);
                }
                dataItem.put(metric.getName().toLowerCase(), metricResult.get(postId));
            }
        }

        List<List<Object>> resultDataList = new ArrayList<>(dataMap.size());
        for (Map<String, Object> dataEntry : dataMap.values()) {
            // Shrink each data item map to a list, by the order of dimensions.
            List<Object> resultDataItem = dimensions.stream().map(dataEntry::get).collect(Collectors.toList());
            resultDataList.add(resultDataItem);
        }

        // sort the result list by dimension "time"
        // Date)dataSet1.get(0)).getTime() - ((Date)dataSet2.get(0)).getTime()
        resultDataList.sort(
                (dataSet1, dataSet2) -> (int)((((long) dataSet1.get(0) / 1000) - ((long) dataSet2.get(0) / 1000))));

        target.setData(resultDataList);

        return target;
    }
}
