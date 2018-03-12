package net.windia.insdata.service;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Iterables;
import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.constants.ProfileInsightsMetric;
import net.windia.insdata.model.client.IgAPIClientDataEntry;
import net.windia.insdata.model.client.IgAPIClientIgProfile;
import net.windia.insdata.model.client.IgAPIClientInsight;
import net.windia.insdata.model.client.IgAPIClientProfileAudience;
import net.windia.insdata.model.internal.*;
import net.windia.insdata.repository.IgProfileRepository;
import net.windia.insdata.repository.IgProfileSnapshotDailyRepository;
import net.windia.insdata.repository.IgProfileSnapshotHourlyRepository;
import net.windia.insdata.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Convert;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
@Service
public class IgProfileStatService {

    @Autowired
    private IgProfileRepository igProfileRepo;

    @Autowired
    private IgProfileSnapshotHourlyRepository igProfileSnapshotHourlyRepo;

    @Autowired
    private IgProfileSnapshotDailyRepository igProfileSnapshotDailyRepo;

    @Autowired
    private IgProfileDiffHourlyService igProfileDiffHourlyService;

    @Autowired
    private IgProfileDiffDailyService igProfileDiffDailyService;

    private ConvertMeta convertToInternal(IgAPIClientIgProfile raw, IgProfileBasicStat internal, Date now) {

        ConvertMeta meta = new ConvertMeta();

        internal.setFollowers(raw.getFollowersCount());
        internal.setFollows(raw.getFollowsCount());
        internal.setMediaCount(raw.getMediaCount());

        for (IgAPIClientInsight<Integer> insightRaw : raw.getInsights().getData()) {

            List<IgAPIClientDataEntry<Integer>> insightEntries = insightRaw.getValues();
            Date sinceTime = insightEntries.get(1).getEndTime();

            IgAPIClientDataEntry<Integer> insightEntry;

            if (DateTimeUtils.passedWithinOneHour(sinceTime, now)) {
                insightEntry = insightEntries.get(0);
                meta.setNewDay(true);
            } else {
                insightEntry = insightEntries.get(1);
            }

            Integer value = insightEntry.getValue();
            meta.setSinceTime(insightEntry.getEndTime());

            String insightName = insightRaw.getName();
            if (IgAPIClientInsight.NAME_FOLLOWER_COUNT.equals(insightName)) {
                insightName = "new_followers";
            }

            String setterMethodName = "set" + CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, insightName);

            try {
                Method setter = internal.getClass().getMethod(setterMethodName, Integer.class);
                setter.invoke(internal, value);
            } catch (NoSuchMethodException e) {
                log.error("Failed to get setter method. ", e);
            } catch (IllegalAccessException|InvocationTargetException e) {
                log.error("Failed to call setter method. ", e);
            }
        }

        return meta;
    }

    public boolean saveHourlyStat(IgProfile owningProfile, IgAPIClientIgProfile statRaw) {
        IgProfileSnapshotHourly snapshotHourly = new IgProfileSnapshotHourly();

        snapshotHourly.setIgProfile(owningProfile);

        Date now = new Date();
        snapshotHourly.setCapturedAt(now);
        snapshotHourly.setHour((byte) DateTimeUtils.getHourInTimeZone(owningProfile.getUser().getTimeZone(), now));

        ConvertMeta meta = convertToInternal(statRaw, snapshotHourly, now);

        igProfileDiffHourlyService.calculateAndSaveDiff(snapshotHourly, meta.getSinceTime());
        igProfileSnapshotHourlyRepo.save(snapshotHourly);

        return meta.isNewDay();
    }

    public void saveDailyStat(IgProfile owningProfile, IgAPIClientIgProfile statRaw) {
        IgProfileSnapshotDaily snapshotDaily = new IgProfileSnapshotDaily();

        snapshotDaily.setIgProfile(owningProfile);

        Date now = new Date();
        Calendar nowCal = Calendar.getInstance();
        nowCal.setTime(now);

        snapshotDaily.setCapturedAt(now);

        snapshotDaily.setWeek(DateTimeUtils.getWeekStartingDate(now));
        snapshotDaily.setMonth((byte) nowCal.get(Calendar.MONTH));
        snapshotDaily.setWeekday((byte) nowCal.get(Calendar.DAY_OF_WEEK));

        ConvertMeta meta = convertToInternal(statRaw, snapshotDaily, now);

        igProfileDiffDailyService.calculateAndSaveDiff(snapshotDaily, meta.getSinceTime());
        igProfileSnapshotDailyRepo.save(snapshotDaily);
    }


    public void saveAudience(IgProfile myProfile, IgAPIClientProfileAudience igProfileAudienceRaw) {

        List<IgProfileAudienceDaily> audienceRecords = new ArrayList<>(32);

        for (IgAPIClientInsight<Map<String, Integer>> insightRaw : igProfileAudienceRaw.getData()) {
            String type = insightRaw.getName().substring(9);

            Map<String, Integer> valueKeyPairs = insightRaw.getValues().get(0).getValue();
        }
    }
}

class ConvertMeta {
    private Date sinceTime;
    private boolean newDay;

    public Date getSinceTime() {
        return sinceTime;
    }

    public void setSinceTime(Date sinceTime) {
        this.sinceTime = sinceTime;
    }

    public boolean isNewDay() {
        return newDay;
    }

    public void setNewDay(boolean newDay) {
        this.newDay = newDay;
    }
}