package net.windia.insdata.service;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.metric.IgOnlineFollowersGranularity;
import net.windia.insdata.model.client.IgAPIClientDataEntry;
import net.windia.insdata.model.client.IgAPIClientProfileAudience;
import net.windia.insdata.model.internal.IgOnlineFollowers;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.IgProfileSnapshotHourly;
import net.windia.insdata.repository.IgOnlineFollowersRepository;
import net.windia.insdata.repository.IgProfileSnapshotHourlyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IgOnlineFollowersService {

    @Autowired
    private IgOnlineFollowersRepository igOnlineFollowersRepo;

    @Autowired
    private IgProfileSnapshotHourlyRepository igProfileSnapshotHourlyRepo;

    public void enrichPercentage(IgProfile igProfile, List<IgOnlineFollowers> onlineFollowersRecords, Date baseTime) {

        Calendar endTimeCal = Calendar.getInstance(TimeZone.getTimeZone(igProfile.getUser().getTimeZone()));
        endTimeCal.setTime(baseTime);
        endTimeCal.add(Calendar.DATE, 1);

        List<IgProfileSnapshotHourly> igProfileSnapshotHourlyList =
                igProfileSnapshotHourlyRepo.findFollowersByDay(igProfile, baseTime, endTimeCal.getTime());

        Map<Byte, Integer> hourToFollowersMap =
                igProfileSnapshotHourlyList.stream().collect(
                        Collectors.toMap(IgProfileSnapshotHourly::getHour, IgProfileSnapshotHourly::getFollowers));

        for (IgOnlineFollowers onlineFollowersRecord : onlineFollowersRecords) {
            Integer followerCount = hourToFollowersMap.get(onlineFollowersRecord.getHour());

            if (null == followerCount || 0 == followerCount) {
                continue;
            }

            onlineFollowersRecord.setPercentage((float) onlineFollowersRecord.getCount() / followerCount);
        }
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

        enrichPercentage(myProfile, onlineFollowersRecords, baseTime.getTime());
        igOnlineFollowersRepo.saveAll(onlineFollowersRecords);
    }

    public List<IgOnlineFollowers> getOnlineFollowers(Long profileId, IgOnlineFollowersGranularity granularity, Date since, Date until) {

        if (null == granularity) {
            return null;
        } else {
            return granularity.extractData(igOnlineFollowersRepo, profileId, since, until);
        }
    }
}
