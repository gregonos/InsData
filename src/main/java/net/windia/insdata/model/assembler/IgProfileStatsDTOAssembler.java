package net.windia.insdata.model.assembler;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.metric.IgDataSource;
import net.windia.insdata.metric.IgMetric;
import net.windia.insdata.metric.IgOnlineFollowersGranularity;
import net.windia.insdata.metric.StatGranularity;
import net.windia.insdata.model.dto.IgProfileStatsDTO;
import net.windia.insdata.model.internal.IgOnlineFollowers;
import net.windia.insdata.model.internal.IgStat;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IgProfileStatsDTOAssembler {

    public IgProfileStatsDTO assemble(List<IgMetric> metrics, StatGranularity granInstance, Map<IgDataSource, List<? extends IgStat>> sourceMap) {

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
            Map metricResult = metric.calculate(granInstance, (List<List<? extends IgStat>>) metric.getSources(granInstance).stream().map(sourceMap::get).collect(Collectors.toList()));
            for (Object key : metricResult.keySet()) {
                Map<String, Object> dataItem = dataMap.get(key);
                if (null == dataItem) {
                    dataItem = new HashMap<>(dimensions.size() - 1);
                    dataItem.put(IgProfileStatsDTO.DIMENSION_TIME, key);
                    dataMap.put(key, dataItem);
                }
                dataItem.put(metric.getName().toLowerCase(), metricResult.get(key));
            }
        }

        List<List<Object>> resultDataSet = new ArrayList<>(dataMap.size());
        for (Map<String, Object> dataEntry : dataMap.values()) {
            List<Object> resultDataItem = dimensions.stream().map(dataEntry::get).collect(Collectors.toList());
            resultDataSet.add(resultDataItem);
        }

        resultDataSet.sort(
                (dataSet1, dataSet2) -> (int)(((Date)dataSet1.get(0)).getTime() - ((Date)dataSet2.get(0)).getTime()));

        target.setData(resultDataSet);

        return target;
    }

    public IgProfileStatsDTO assemble(String granularity, List<IgOnlineFollowers> onlineFollowers) {
        IgProfileStatsDTO target = new IgProfileStatsDTO();

        // Dimensions
        List<String> dimensions = new ArrayList<>(4);
        dimensions.add(IgProfileStatsDTO.DIMENSION_DATE);
        dimensions.add(IgProfileStatsDTO.DIMENSION_WEEKDAY);
        dimensions.add(IgProfileStatsDTO.DIMENSION_COUNT);
        dimensions.add(IgProfileStatsDTO.DIMENSION_PERCENTAGE);

        List<List<Object>> data = new ArrayList<>(onlineFollowers.size());
        for (IgOnlineFollowers onlineFollower : onlineFollowers) {
            List<Object> row = new ArrayList<>(4);

            if (IgOnlineFollowersGranularity.AGGREGATE_HOUR.getValue().equals(granularity) ||
                    IgOnlineFollowersGranularity.AGGREGATE_HOUR_WEEKDAY.getValue().equals(granularity)) {
                row.add(onlineFollower.getHour());
            } else {
                row.add(onlineFollower.getDate().getTime() + 3600000 * onlineFollower.getHour());
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
}
