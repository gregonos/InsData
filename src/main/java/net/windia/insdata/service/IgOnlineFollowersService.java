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

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IgOnlineFollowersService {

    @Autowired
    private IgOnlineFollowersRepository igOnlineFollowersRepo;

    @Autowired
    private IgProfileSnapshotHourlyRepository igProfileSnapshotHourlyRepo;

    public void enrichPercentage(IgProfile igProfile, List<IgOnlineFollowers> onlineFollowersRecords, OffsetDateTime baseTime) {


        List<IgProfileSnapshotHourly> igProfileSnapshotHourlyList =
                igProfileSnapshotHourlyRepo.findFollowersByDay(igProfile, baseTime, baseTime.plusDays(1));

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

        ZonedDateTime baseTime = dataEntry.getEndTime()
                .atZoneSameInstant(myProfile.getUser().getZoneId())
                .minusDays(1);

        List<IgOnlineFollowers> onlineFollowersRecords = new ArrayList<>(24);

        Map<String, Integer> keyValuePairs = dataEntry.getValue();
        for (String hourStr : keyValuePairs.keySet()) {
            ZonedDateTime recordTime = baseTime.plusHours(Integer.parseInt(hourStr));

            IgOnlineFollowers onlineFollowers = new IgOnlineFollowers();
            onlineFollowers.setIgProfile(myProfile);
            onlineFollowers.setDateTime(recordTime.toOffsetDateTime());
            onlineFollowers.setDate(recordTime.toLocalDate());
            onlineFollowers.setHour((byte) recordTime.getHour());
            onlineFollowers.setWeekday((byte) recordTime.getDayOfWeek().getValue());

            onlineFollowers.setCount(keyValuePairs.get(hourStr));

            onlineFollowersRecords.add(onlineFollowers);
        }

        enrichPercentage(myProfile, onlineFollowersRecords, baseTime.toOffsetDateTime());
        igOnlineFollowersRepo.saveAll(onlineFollowersRecords);
    }

    public List<IgOnlineFollowers> getOnlineFollowers(Long profileId, IgOnlineFollowersGranularity granularity,
                                                      OffsetDateTime since, OffsetDateTime until) {

        if (null == granularity) {
            return null;
        } else {
            return granularity.extractData(igOnlineFollowersRepo, profileId, since, until);
        }
    }
}
