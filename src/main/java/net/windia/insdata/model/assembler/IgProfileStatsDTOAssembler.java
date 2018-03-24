package net.windia.insdata.model.assembler;

import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.constants.IgDiffMetric;
import net.windia.insdata.constants.IgOnlineFollowersGranularity;
import net.windia.insdata.constants.IgSnapshotMetric;
import net.windia.insdata.model.dto.IgProfileStatsDTO;
import net.windia.insdata.model.internal.IgOnlineFollowers;
import net.windia.insdata.model.internal.IgProfileDiff;
import net.windia.insdata.model.internal.IgProfileSnapshot;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IgProfileStatsDTOAssembler {

    public IgProfileStatsDTO assemble(List<IgSnapshotMetric> snapshotFields, List<IgDiffMetric> diffFields, List<String> calcFields,
                                      List<? extends IgProfileSnapshot> snapshots, List<? extends IgProfileDiff> diffs) {
        IgProfileStatsDTO target = new IgProfileStatsDTO();

        // Dimensions
        int fieldSize = 1 + snapshotFields.size() + diffFields.size();
        List<String> dimensions = new ArrayList<>(fieldSize);
        dimensions.add(IgProfileStatsDTO.DIMENSION_TIME);

        dimensions.addAll(snapshotFields.stream().map(IgSnapshotMetric::getValue).collect(Collectors.toList()));
        dimensions.addAll(diffFields.stream().map(IgDiffMetric::getValue).collect(Collectors.toList()));
        dimensions.addAll(calcFields);

        // Data
        int dataSize = 0;
        dataSize += null == snapshots ? 0 : snapshots.size();
        List<List<Object>> data = new ArrayList<>(dataSize);

        Map<Long, IgProfileDiff> diffMap = new HashMap<>();
        if (null != diffs) {
            for (IgProfileDiff diff : diffs) {
                diffMap.put(diff.getComparedTo().getTime(), diff);
            }
        }

        for (IgProfileSnapshot snapshot : snapshots) {
            List<Object> dataItem = new ArrayList<>(fieldSize);

            // Time
            Date time = snapshot.getCapturedAt();
            dataItem.add(time);

            dataItem.addAll(snapshotFields.stream().map(snapshotField -> getValueByPropertyName(snapshot, snapshotField.getFieldName())).collect(Collectors.toList()));

            IgProfileDiff diff = diffMap.get(time.getTime());
            dataItem.addAll(diffFields.stream().map(diffField -> getValueByPropertyName(diff, diffField.getFieldName())).collect(Collectors.toList()));

            for (String calcField : calcFields) {
                String calcMethodName = "calc" + CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, calcField);
                try {
                    Method calcMethod = this.getClass().getMethod(calcMethodName, IgProfileSnapshot.class, IgProfileDiff.class);
                    Float rslt = (Float) calcMethod.invoke(this, snapshot, diff);
                    dataItem.add(rslt);
                } catch (NoSuchMethodException e) {
                    log.error("Failed to find getter method [" + calcMethodName + "]", e);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    log.error("Failed to call getter method [" + calcMethodName + "]", e);
                }
            }

            data.add(dataItem);
        }

        target.setDimensions(dimensions);
        target.setData(data);

        return target;
    }

    public Float calcImpressionsPerReach(IgProfileSnapshot snapshot, IgProfileDiff diff) {
        return 0 == snapshot.getReach() ? 0 : snapshot.getImpressions().floatValue() / snapshot.getReach();
    }

    public Float calcImpressionsPerReachDiff(IgProfileSnapshot snapshot, IgProfileDiff diff) {
        if (null == diff) {
            return 0F;
        }
        return 0 == diff.getReach() ? 0 : diff.getImpressions().floatValue() / diff.getReach();
    }

    private Object getValueByPropertyName(Object obj, String field) {
        if (null == obj) {
            return null;
        }

        String getterName = "get" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, field);

        try {
            Method getter = obj.getClass().getMethod(getterName);
            return getter.invoke(obj);
        } catch (NoSuchMethodException e) {
            log.error("Failed to find getter method [" + getterName + "]", e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            log.error("Failed to call getter method [" + getterName + "]", e);
        }

        return null;
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
