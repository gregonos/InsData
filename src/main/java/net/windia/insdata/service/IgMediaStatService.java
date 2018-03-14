package net.windia.insdata.service;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.model.client.IgAPIClientMedia;
import net.windia.insdata.model.internal.IgMedia;
import net.windia.insdata.model.internal.IgMediaDiff;
import net.windia.insdata.model.internal.IgMediaSnapshot;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.mapper.IgMediaSnapshotMapper;
import net.windia.insdata.repository.IgMediaRepository;
import net.windia.insdata.service.restclient.IgRestClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;

@Slf4j
public abstract class IgMediaStatService<Snapshot extends IgMediaSnapshot> {

    @Autowired
    private IgMediaRepository mediaRepo;

    @Autowired
    private IgMediaService mediaService;

    @Autowired
    private IgRestClientService restClientService;

    private Map<Long, Set<String>> mediaIdCache;

    private Map<Long, Map<String, IgMedia>> mediaCache;

    @PostConstruct
    public void init() {
        mediaIdCache = new HashMap<>(4);
        mediaCache = new HashMap<>(4);
    }

    public void saveMediaStat(IgProfile profile, List<IgAPIClientMedia> mediaListRaw, Date capturedAt) {

        // Prepare media id collection
        Set<String> mediaIdSet = mediaIdCache.get(profile.getId());
        Map<String, IgMedia> mediaMap = mediaCache.get(profile.getId());
        if (null == mediaIdSet) {
            List<IgMedia> allMediaIds = mediaRepo.findIdByIgProfile(profile);
            mediaIdSet = new HashSet<>(allMediaIds.size());
            for (IgMedia idHolder : allMediaIds) {
                mediaIdSet.add(idHolder.getId());
            }
            mediaIdCache.put(profile.getId(), mediaIdSet);

            mediaMap = new HashMap<>(allMediaIds.size());
            for (IgMedia allMediaId : allMediaIds) {
                mediaMap.put(allMediaId.getId(), allMediaId);
            }
            mediaCache.put(profile.getId(), mediaMap);
        }

        List<Snapshot> mediaSnapshotList = new ArrayList<>(mediaListRaw.size());
        IgMediaSnapshotMapper<Snapshot> mapper = getMapper();
        mapper.addExtraField(IgMediaSnapshotMapper.FIELD_IG_PROFILE, profile);
        mapper.addExtraField(IgMediaSnapshotMapper.FIELD_CAPTURED_AT, capturedAt);

        for (IgAPIClientMedia mediaRaw : mediaListRaw) {

            IgMedia media;

            // Check if it's existing media, otherwise there would be a foreign key problem.
            if (mediaIdSet.contains(mediaRaw.getId())) {
                media = mediaMap.get(mediaRaw.getId());
            } else {
                log.debug("New media [" + mediaRaw.getId() + "] found, retrieving from facebook...");
                IgAPIClientMedia mediaMeta = restClientService.retrieveMediaMeta(mediaRaw.getId(), profile);
                if (null == mediaMeta) {
                    continue;
                }
                media = mediaService.saveMediaMeta(profile, mediaMeta);
                mediaIdSet.add(mediaMeta.getId());
                mediaMap.put(media.getId(), media);
            }

            mapper.addExtraField(IgMediaSnapshotMapper.FIELD_MEDIA, media);
            mediaSnapshotList.add(mapper.map(mediaRaw));
        }

        getMediaDiffService().calculateAndSaveDiff(profile, mediaSnapshotList);
        getMediaSnapshotRepo().saveAll(mediaSnapshotList);
    }

    protected abstract <M extends IgMediaDiffService<Snapshot, IgMediaDiff>> M getMediaDiffService();

    protected abstract IgMediaSnapshotMapper<Snapshot> getMapper();

    protected abstract CrudRepository getMediaSnapshotRepo();
}
