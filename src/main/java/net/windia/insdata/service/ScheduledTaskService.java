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

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
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
        public boolean processRawMedia(IgProfile profile, List<IgAPIClientMedia> rawMediaList, OffsetDateTime capturedAt) {

            mediaService.saveMediaMeta(profile, rawMediaList);
            return true;
        }
    }

    private class IgRawMediaStatHandler implements IgRawMediaHandler {

        @Override
        public boolean processRawMedia(IgProfile profile, List<IgAPIClientMedia> rawMediaList, OffsetDateTime capturedAt) {

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
    @Scheduled(cron = "0 15 * * * *")
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

        int hourOfFacebookServer = DateTimeUtils.hourOfFacebookServer();
        if (0 == hourOfFacebookServer) {
//        if (true) {

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
        } else if (1 == hourOfFacebookServer) {

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

//    @Scheduled(initialDelay = 2000, fixedRate = 3600000)
    public void retrieveInsightsHistory() {
        IgProfile myProfile = igProfileRepo.findById(1L).get();

        long since = 1518307200 - 86400 * 2;
        long until = 1520812800 - 86400 * 2;

        log.info("Starting to download insights history for account ");

        IgAPIClientIgProfile profileRaw = igRestClientService.retrieveProfileInsightHistory(myProfile, since, until);
        log.debug("Insight history data received successfully!");

        igProfileProfileDailyService.saveStatSeries(myProfile, profileRaw);
        log.info("Insight history saved successfully! ");
    }

//    @Scheduled(initialDelay = 6000, fixedRate = 3600000)
    public void retrieveOnlineFollowersHistory() {
        IgProfile myProfile = igProfileRepo.findById(1L).get();

        log.debug("Starting to download historical online followers data...");

        long since = 1611532800;
        Date now = new Date();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        while (since < now.getTime() / 1000) {
//        while (since < 1611532800 + 86400 * 2) {
            log.debug("Retrieving online followers data based on time: " + formatter.format(new Date(since * 1000)));
            IgAPIClientProfileAudience igOnlineFollowersRaw = igRestClientService.retrieveProfileOnlineFollowers(myProfile, since, since + 86400);
            igOnlineFollowersService.saveOnlineFollowers(myProfile, igOnlineFollowersRaw);

            since += 86400;
        }
    }

    @Scheduled(cron = "0 16 * * * *")
//    @Scheduled(initialDelay = 12000, fixedRate = 3600000)
    public void retrieveMediaStat() {
        IgProfile myProfile = igProfileRepo.findById(1L).get();

        log.info("Starting to download hourly media stat for account [" + myProfile.getBusinessAccountId() + "/" + myProfile.getUsername() + "]...");

        int count = igRestClientService.retrieveAllMedia(myProfile, IgRestClientService.PARAM_FIELDS_MEDIA_STAT, rawMediaStatHandler, instagramPostBatchesLimit);

        log.info(count + " media stat entries are parsed and stored.");
    }

}
