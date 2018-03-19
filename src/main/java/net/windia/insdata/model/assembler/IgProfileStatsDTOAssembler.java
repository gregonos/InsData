package net.windia.insdata.model.assembler;

import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.constants.IgDiffMetric;
import net.windia.insdata.constants.IgSnapshotMetric;
import net.windia.insdata.model.dto.IgProfileStatsDTO;
import net.windia.insdata.model.internal.IgProfileDiff;
import net.windia.insdata.model.internal.IgProfileSnapshot;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IgProfileStatsDTOAssembler {

    public IgProfileStatsDTO assemble(List<IgSnapshotMetric> snapshotFields, List<IgDiffMetric> diffFields,
                                      List<? extends IgProfileSnapshot> snapshots, List<? extends IgProfileDiff> diffs) {
        IgProfileStatsDTO target = new IgProfileStatsDTO();

        // Dimensions
        int fieldSize = 1 + snapshotFields.size() + diffFields.size();
        List<String> dimensions = new ArrayList<>(fieldSize);
        dimensions.add(IgProfileStatsDTO.DIMENSION_TIME);

        dimensions.addAll(snapshotFields.stream().map(IgSnapshotMetric::getValue).collect(Collectors.toList()));
        dimensions.addAll(diffFields.stream().map(IgDiffMetric::getValue).collect(Collectors.toList()));

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

            data.add(dataItem);
        }

        target.setDimensions(dimensions);
        target.setData(data);

        return target;
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
}
