package net.windia.insdata.service;

import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.model.client.IgAPIClientDataEntry;
import net.windia.insdata.model.client.IgAPIClientIgProfile;
import net.windia.insdata.model.client.IgAPIClientInsight;
import net.windia.insdata.model.client.IgAPIClientProfileAudience;
import net.windia.insdata.model.internal.IgOnlineFollowers;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.IgProfileAudienceDaily;
import net.windia.insdata.model.internal.IgProfileBasicStat;
import net.windia.insdata.model.internal.IgProfileSnapshotDaily;
import net.windia.insdata.model.internal.IgProfileSnapshotHourly;
import net.windia.insdata.repository.IgOnlineFollowersRepository;
import net.windia.insdata.repository.IgProfileProfileAudienceDailyRepository;
import net.windia.insdata.repository.IgProfileRepository;
import net.windia.insdata.repository.IgProfileSnapshotDailyRepository;
import net.windia.insdata.repository.IgProfileSnapshotHourlyRepository;
import net.windia.insdata.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
    private IgProfileProfileAudienceDailyRepository igProfileProfileAudienceRepo;

    @Autowired
    private IgOnlineFollowersRepository igOnlineFollowersRepo;

    @Autowired
    private IgProfileDiffHourlyService igProfileDiffHourlyService;

    @Autowired
    private IgProfileDiffDailyService igProfileDiffDailyService;

    @Autowired
    private IgProfileDiffAudienceService igProfileDiffAudienceService;

    @Autowired
    private IgOnlineFollowersService igOnlineFollowersService;

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
        if (null == statRaw) {
            return false;
        }

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

        if (null == igProfileAudienceRaw) {
            return;
        }

        List<IgProfileAudienceDaily> audienceRecords = new ArrayList<>(32);
        Date now = new Date();

        for (IgAPIClientInsight<Map<String, Integer>> insightRaw : igProfileAudienceRaw.getData()) {
            String type = insightRaw.getName().substring(9);

            Map<String, Integer> valueKeyPairs = insightRaw.getValues().get(0).getValue();

            for (String section : valueKeyPairs.keySet()) {

                // Create new instance of IgProfileAudienceDaily
                IgProfileAudienceDaily audienceDaily = new IgProfileAudienceDaily();
                audienceDaily.setIgProfile(myProfile);
                audienceDaily.setCapturedAt(now);
                audienceDaily.setType(type);

                audienceDaily.setSection(section);
                audienceDaily.setCount(valueKeyPairs.get(section));

                audienceRecords.add(audienceDaily);
            }
        }

        igProfileDiffAudienceService.enrichWithDiff(audienceRecords);
        igProfileProfileAudienceRepo.saveAll(audienceRecords);
    }

    public void saveOnlineFollowers(IgProfile myProfile, IgAPIClientProfileAudience igOnlineFollowersRaw) {

        if (null == igOnlineFollowersRaw) {
            return;
        }

        IgAPIClientDataEntry<Map<String, Integer>> dataEntry = igOnlineFollowersRaw.getData().get(0).getValues().get(0);

        Calendar baseTime = Calendar.getInstance(TimeZone.getTimeZone(myProfile.getUser().getTimeZone()));
        baseTime.setTime(dataEntry.getEndTime());
        baseTime.add(Calendar.DATE, -1);

        List<IgOnlineFollowers> onlineFollowersRecords = new ArrayList<>(24);

        Map<String, Integer> keyValuePairs = dataEntry.getValue();
        for (String hourStr : keyValuePairs.keySet()) {
            Integer hour = Integer.parseInt(hourStr);

            Calendar recordTime = (Calendar) baseTime.clone();
            recordTime.add(Calendar.HOUR, hour);

            IgOnlineFollowers onlineFollowers = new IgOnlineFollowers();
            onlineFollowers.setIgProfile(myProfile);
            onlineFollowers.setDate(recordTime.getTime());
            onlineFollowers.setHour((byte) recordTime.get(Calendar.HOUR_OF_DAY));
            onlineFollowers.setWeekday((byte) recordTime.get(Calendar.DAY_OF_WEEK));

            onlineFollowers.setCount(keyValuePairs.get(hourStr));

            onlineFollowersRecords.add(onlineFollowers);
        }

        igOnlineFollowersService.enrichPercentage(myProfile, onlineFollowersRecords, baseTime.getTime());
        igOnlineFollowersRepo.saveAll(onlineFollowersRecords);
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