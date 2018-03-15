package net.windia.insdata.service;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.model.client.IgAPIClientIgProfile;
import net.windia.insdata.model.client.IgAPIClientMedia;
import net.windia.insdata.model.client.IgAPIClientProfileAudience;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.repository.IgProfileRepository;
import net.windia.insdata.service.restclient.IgRestClientService;
import net.windia.insdata.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ScheduledTaskService {

    @Autowired
    private IgRestClientService igRestClientService;

    @Autowired
    private IgProfileRepository igProfileRepo;

    @Autowired
    private IgProfileSnapshotHourlyService igProfileProfileHourlyService;

    @Autowired
    private IgProfileSnapshotDailyService igProfileProfileDailyService;

    @Autowired
    private IgProfileAudienceService igProfileAudienceService;

    @Autowired
    private IgOnlineFollowersService igOnlineFollowersService;

    @Autowired
    private IgMediaService mediaService;

    @Autowired
    private IgMediaSnapshotHourlyService igMediaSnapshotHourlyService;

    @Autowired
    private IgMediaSnapshotDailyService igMediaSnapshotDailyService;

    @Value("${insdata.facebook.batches-of-posts}")
    private int instagramPostBatchesLimit;

    private class IgRawMediaMetaHandler implements IgRawMediaHandler {

        @Override
        public boolean processRawMedia(IgProfile profile, List<IgAPIClientMedia> rawMediaList, Date capturedAt) {

            mediaService.saveMediaMeta(profile, rawMediaList);
            return true;
        }
    }

    private class IgRawMediaStatHandler implements IgRawMediaHandler {

        @Override
        public boolean processRawMedia(IgProfile profile, List<IgAPIClientMedia> rawMediaList, Date capturedAt) {

            igMediaSnapshotHourlyService.saveMediaStat(profile, rawMediaList, capturedAt);

            if (0 == DateTimeUtils.hourOfFacebookServer()) {
                igMediaSnapshotDailyService.saveMediaStat(profile, rawMediaList, capturedAt);
            }
            return true;
        }
    }

    private IgRawMediaMetaHandler rawMediaMetaHandler = new IgRawMediaMetaHandler();

    private IgRawMediaStatHandler rawMediaStatHandler = new IgRawMediaStatHandler();

//    @Scheduled(initialDelay = 2000, fixedRate = 3600000)
    @Scheduled(cron = "0 5 * * * *")
    public void retrieveProfile() {

        IgProfile myProfile = igProfileRepo.findById(1L).get();

        log.info("Starting hourly job for account [" + myProfile.getBusinessAccountId() + "/" + myProfile.getUsername() + "]...");

        log.debug("Fetching profile basic snapshot...");

        IgAPIClientIgProfile igProfileRaw = igRestClientService.retrieveProfileStat(myProfile);
        if (null != igProfileRaw) {
            log.debug("Response payload retrieved successfully from Facebook");

            igProfileProfileHourlyService.saveStat(myProfile, igProfileRaw);
            log.debug("Profile basic snapshot hourly data parsed and stored successfully!");
        }

        if (0 == DateTimeUtils.hourOfFacebookServer()) {

            log.debug("A new reporting day detected. Start to process daily stat...");

            if (null != igProfileRaw) {
                igProfileProfileDailyService.saveStat(myProfile, igProfileRaw);

                log.debug("Profile basic snapshot daily data parsed and stored successfully!");
            }

            // Audience
            log.debug("Fetching profile audience snapshot...");

            IgAPIClientProfileAudience igProfileAudienceRaw = igRestClientService.retrieveProfileAudienceStat(myProfile);
            log.debug("Response payload of audience retrieved successfully from Facebook");

            igProfileAudienceService.saveAudience(myProfile, igProfileAudienceRaw);
            log.debug("Profile audience data parsed and stored successfully!");

            // Online Followers
            log.debug("Fetching profile online followers data... ");

            IgAPIClientProfileAudience igOnlineFollowersRaw = igRestClientService.retrieveProfileOnlineFollowers(myProfile);
            log.debug("Response payload of online followers retrieved successfully from Facebook");

            igOnlineFollowersService.saveOnlineFollowers(myProfile, igOnlineFollowersRaw);
            log.debug("Profile online followers data parsed and stored successfully!");
        }
    }

//    @Scheduled(initialDelay = 2000, fixedRate = 3600000)
    public void retrieveMediaMeta() {
        IgProfile myProfile = igProfileRepo.findById(1L).get();

        log.info("Starting to download media for account [" + myProfile.getBusinessAccountId() + "/" + myProfile.getUsername() + "]...");

        int count = igRestClientService.retrieveAllMedia(myProfile, IgRestClientService.PARAM_FIELDS_MEDIA_META, rawMediaMetaHandler, 0);

        log.info(count + " media meta entries are parsed and stored.");
    }

    @Scheduled(cron = "0 6 * * * *")
//    @Scheduled(initialDelay = 12000, fixedRate = 3600000)
    public void retrieveMediaStat() {
        IgProfile myProfile = igProfileRepo.findById(1L).get();

        log.info("Starting to download hourly media stat for account [" + myProfile.getBusinessAccountId() + "/" + myProfile.getUsername() + "]...");

        int count = igRestClientService.retrieveAllMedia(myProfile, IgRestClientService.PARAM_FIELDS_MEDIA_STAT, rawMediaStatHandler, instagramPostBatchesLimit);

        log.info(count + " media stat entries are parsed and stored.");
    }

}
