package net.windia.insdata.model.mapper;

import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.model.client.IgAPIClientDataEntry;
import net.windia.insdata.model.client.IgAPIClientIgProfile;
import net.windia.insdata.model.client.IgAPIClientInsight;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.IgProfileSnapshotDaily;
import net.windia.insdata.model.internal.InsDataUser;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class IgProfileDailyInsightsMapper extends ResourceMapper<IgAPIClientIgProfile, List<IgProfileSnapshotDaily>> {

    @Override
    public List<IgProfileSnapshotDaily> map(IgAPIClientIgProfile source) {

        List<IgAPIClientInsight<Integer>> insight = source.getInsights().getData();

        List<IgAPIClientDataEntry<Integer>> insightEntries = insight.get(0).getValues();
        int insightDaysCount = insightEntries.size();
        List<IgProfileSnapshotDaily> snapshots = new ArrayList<>(insightDaysCount);

        for (int i = 0; i < insightDaysCount; i++) {
            IgAPIClientDataEntry<Integer> insightEntrySample = insightEntries.get(i);

            IgProfileSnapshotDaily snapshot = new IgProfileSnapshotDaily();
            IgProfile profile = (IgProfile) getExtraField(IgProfileSnapshotMapper.FIELD_IG_PROFILE);
            OffsetDateTime endTime = insightEntrySample.getEndTime();
            InsDataUser user = profile.getUser();
            snapshot.realizeCapturedAt(endTime, user.getZoneId(), user.getFirstDayOfWeekInstance());
            snapshot.setIgProfile(profile);
            snapshot.setFollowers(0);
            snapshot.setFollows(0);
            snapshot.setMediaCount(0);

            for (IgAPIClientInsight<Integer> insightType : source.getInsights().getData()) {
                String insightName = insightType.getName();
                if (IgAPIClientInsight.NAME_FOLLOWER_COUNT.equals(insightName)) {
                    insightName = "new_followers";
                }

                IgAPIClientDataEntry<Integer> insightEntry = insightType.getValues().get(i);

                String setterMethodName = "set" + CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, insightName);

                try {
                    Method setter = snapshot.getClass().getMethod(setterMethodName, Integer.class);
                    setter.invoke(snapshot, insightEntry.getValue());
                } catch (NoSuchMethodException e) {
                    log.error("Failed to find setter method for stat field.", e);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    log.error("Failed to invoke setter method", e);
                }
            }

            snapshots.add(snapshot);
        }

        return snapshots;
    }
}
