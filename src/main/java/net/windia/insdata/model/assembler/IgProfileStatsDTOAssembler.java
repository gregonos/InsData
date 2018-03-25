package net.windia.insdata.model.assembler;

import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.constants.IgDataSource;
import net.windia.insdata.constants.IgMetric;
import net.windia.insdata.constants.IgOnlineFollowersGranularity;
import net.windia.insdata.constants.StatGranularity;
import net.windia.insdata.model.dto.IgProfileStatsDTO;
import net.windia.insdata.model.internal.*;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

@Slf4j
@Service
public class IgProfileStatsDTOAssembler {

    public IgProfileStatsDTO assemble(List<IgMetric> metrics, StatGranularity granInstance, Map<IgDataSource, List<? extends IgStat>> sourceMap)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        IgProfileStatsDTO target = new IgProfileStatsDTO();

        // Dimensions
        List<String> dimensions = new ArrayList<>(metrics.size());
        dimensions.add(IgProfileStatsDTO.DIMENSION_TIME);
        metrics.forEach(metric -> dimensions.add(metric.getName()));
        target.setDimensions(dimensions);

        // Data
        Map<Date, List<Object>> dataMap = new LinkedHashMap<>();
        for (IgMetric metric : metrics) {
            String calcMethodName = "calc" + CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, metric.getName());

            Method calculator = this.getClass().getMethod(calcMethodName, Map.class, StatGranularity.class, List.class);
            calculator.invoke(this, dataMap, granInstance, sourceMap.get(metric.getSource(granInstance)));
        }

        target.setData(dataMap.values());

        return target;
    }

    private void directlyRetrieveFromSource(Map<Date, List<Object>> dataMap, List<? extends IgStat> source, Function<IgStat, Object> retriever) {
        source.forEach(igStat -> getDataItem(dataMap, igStat.getIndicativeDate()).add(retriever.apply(igStat)));
    }

    private void retrieveHourlyFromDiffAndDailyFromSnapshot(Map<Date, List<Object>> dataMap, StatGranularity gran, List<? extends IgStat> source, Function<IgStat, Object> retriever) {
        for (IgStat igStat : source) {
            if (StatGranularity.HOURLY == gran) {
                IgProfileDiffHourly diff = (IgProfileDiffHourly) igStat;
                getDataItem(dataMap, diff.getComparedTo()).add(retriever.apply(diff));
            } else if (StatGranularity.DAILY == gran) {
                IgProfileSnapshotDaily snapshot = (IgProfileSnapshotDaily) igStat;
                getDataItem(dataMap, snapshot.getCapturedAt()).add(retriever.apply(snapshot));
            }
        }
    }

    public void calcFollowers(Map<Date, List<Object>> dataMap, StatGranularity gran, List<IgProfileSnapshot> snapshots) {
        directlyRetrieveFromSource(dataMap, snapshots, igStat -> ((IgProfileSnapshot) igStat).getFollowers());
    }

    public void calcFollowersDiff(Map<Date, List<Object>> dataMap, StatGranularity gran, List<IgProfileDiff> diffs) {
        directlyRetrieveFromSource(dataMap, diffs, igStat -> ((IgProfileDiff) igStat).getFollowers());
    }

    public void calcFollowings(Map<Date, List<Object>> dataMap, StatGranularity gran, List<IgProfileSnapshot> snapshots) {
        directlyRetrieveFromSource(dataMap, snapshots, igStat -> ((IgProfileSnapshot) igStat).getFollows());
    }

    public void calcFollowingsDiff(Map<Date, List<Object>> dataMap, StatGranularity gran, List<IgProfileDiff> diffs) {
        directlyRetrieveFromSource(dataMap, diffs, igStat -> ((IgProfileDiff) igStat).getFollows());
    }

    public void calcImpressions(Map<Date, List<Object>> dataMap, StatGranularity gran, List<? extends IgStat> stats) {
        retrieveHourlyFromDiffAndDailyFromSnapshot(dataMap, gran, stats, igStat -> ((IgProfileStat) igStat).getImpressions());
    }

    public void calcReach(Map<Date, List<Object>> dataMap, StatGranularity gran, List<? extends IgStat> stats) {
        retrieveHourlyFromDiffAndDailyFromSnapshot(dataMap, gran, stats, igStat -> ((IgProfileStat) igStat).getReach());
    }

    public void calcProfileViews(Map<Date, List<Object>> dataMap, StatGranularity gran, List<? extends IgStat> stats) {
        retrieveHourlyFromDiffAndDailyFromSnapshot(dataMap, gran, stats, igStat -> ((IgProfileStat) igStat).getProfileViews());
    }

    public void calcImpressionsPerReach(Map<Date, List<Object>> dataMap, StatGranularity gran, List<? extends IgStat> stats) {
        retrieveHourlyFromDiffAndDailyFromSnapshot(dataMap, gran, stats, igStat -> {
            IgProfileStat stat = (IgProfileStat) igStat;
            return 0 == stat.getReach() ? 0F : stat.getImpressions().floatValue() / stat.getReach();
        });
    }

    private static List<Object> getDataItem(Map<Date, List<Object>> dataMap, Date key) {
        List<Object> dataItem = dataMap.get(key);
        if (null == dataItem) {
            dataItem = new ArrayList<>();
            dataItem.add(key);
            dataMap.put(key, dataItem);
        }

        return dataItem;
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
