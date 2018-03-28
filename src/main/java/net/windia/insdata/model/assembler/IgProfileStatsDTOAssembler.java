package net.windia.insdata.model.assembler;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.constants.IgDataSource;
import net.windia.insdata.constants.IgMetric;
import net.windia.insdata.constants.IgOnlineFollowersGranularity;
import net.windia.insdata.constants.StatGranularity;
import net.windia.insdata.model.dto.IgProfileStatsDTO;
import net.windia.insdata.model.internal.IgOnlineFollowers;
import net.windia.insdata.model.internal.IgStat;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class IgProfileStatsDTOAssembler {

    public IgProfileStatsDTO assemble(List<IgMetric> metrics, StatGranularity granInstance, Map<IgDataSource, List<? extends IgStat>> sourceMap)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        IgProfileStatsDTO target = new IgProfileStatsDTO();

        // Dimensions
        List<String> dimensions = new ArrayList<>(metrics.size() + 1);
        dimensions.add(IgProfileStatsDTO.DIMENSION_TIME);
        metrics.forEach(metric -> dimensions.add(metric.name().toLowerCase()));
        target.setDimensions(dimensions);

        // Data
        Map<Object, List<Object>> dataMap = new LinkedHashMap<>();
        for (IgMetric metric : metrics) {
            Map<Object, Object> metricResult = metric.calculate(granInstance, sourceMap.get(metric.getSource(granInstance)));
            for (Object key : metricResult.keySet()) {
                List<Object> dataItem = dataMap.get(key);
                if (null == dataItem) {
                    dataItem = new ArrayList<>(dimensions.size());
                    dataItem.add(key);
                    dataMap.put(key, dataItem);
                }
                dataItem.add(metricResult.get(key));
            }
        }

        target.setData(dataMap.values());

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
