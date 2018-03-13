package net.windia.insdata.service;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.model.client.IgAPIClientMedia;
import net.windia.insdata.model.internal.IgMedia;
import net.windia.insdata.model.internal.IgMediaSnapshotHourly;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.mapper.IgMediaMapper;
import net.windia.insdata.model.mapper.IgMediaSnapshotHourlyMapper;
import net.windia.insdata.repository.IgMediaRepository;
import net.windia.insdata.repository.IgMediaSnapshotHourlyRepository;
import net.windia.insdata.service.restclient.IgRestClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

@Slf4j
@Service
public class IgMediaStatService {

    @Autowired
    private IgMediaRepository mediaRepo;

    @Autowired
    private IgMediaSnapshotHourlyRepository mediaSnapshotHourlyRepo;

    @Autowired
    private IgRestClientService restClientService;

    private Map<Long, Set<String>> mediaIdCache;

    private Map<Long, Map<String, IgMedia>> mediaCache;

    @PostConstruct
    public void init() {
        mediaIdCache = new HashMap<>(4);
        mediaCache = new HashMap<>(4);
    }

    public void saveMediaMeta(IgProfile profile, List<IgAPIClientMedia> mediaListRaw) {

        IgMediaMapper mapper = new IgMediaMapper();
        mapper.addExtraField(IgMediaMapper.FIELD_IG_PROFILE, profile);

        List<IgMedia> mediaList = mapper.map(mediaListRaw);

        mediaRepo.saveAll(mediaList);
    }

    public IgMedia saveMediaMeta(IgProfile profile, IgAPIClientMedia mediaRaw) {
        IgMediaMapper mapper = new IgMediaMapper();
        mapper.addExtraField(IgMediaMapper.FIELD_IG_PROFILE, profile);

        return mediaRepo.save(mapper.map(mediaRaw));
    }

    public void saveMediaHourlyStat(IgProfile profile, List<IgAPIClientMedia> mediaListRaw) {

        // Prepare media id collection
        Set<String> mediaIdSet = mediaIdCache.get(profile.getId());
        Map<String, IgMedia> mediaMap = mediaCache.get(profile.getId());
        if (null == mediaIdSet) {
            List<IgMedia> allMediaIds = mediaRepo.findIdByIgProfile(profile);
            mediaIdSet = new HashSet<>(allMediaIds.size());
            mediaIdSet.addAll(allMediaIds.stream().map(IgMedia::getId).collect(Collectors.toList()));
            mediaIdCache.put(profile.getId(), mediaIdSet);

            mediaMap = new HashMap<>(allMediaIds.size());
            for (IgMedia allMediaId : allMediaIds) {
                mediaMap.put(allMediaId.getId(), allMediaId);
            }
            mediaCache.put(profile.getId(), mediaMap);
        }

        List<IgMediaSnapshotHourly> mediaSnapshotHourlyList = new ArrayList<>(mediaListRaw.size());
        IgMediaSnapshotHourlyMapper mapper = new IgMediaSnapshotHourlyMapper();
        mapper.addExtraField(IgMediaSnapshotHourlyMapper.FIELD_IG_PROFILE, profile);

        Date now = new Date();
        mapper.addExtraField(IgMediaSnapshotHourlyMapper.FIELD_CAPTURED_AT, now);

        for (IgAPIClientMedia mediaRaw : mediaListRaw) {

            IgMedia media;

            // Check if it's existing media, otherwise there would be a foreign key problem.
            if (mediaIdSet.contains(mediaRaw.getId())) {
                media = mediaMap.get(mediaRaw.getId());
            } else {
                IgAPIClientMedia mediaMeta = restClientService.retrieveMediaMeta(mediaRaw.getId(), profile);
                if (null == mediaMeta) {
                    continue;
                }
                media = saveMediaMeta(profile, mediaMeta);
                mediaIdSet.add(mediaMeta.getId());
                mediaMap.put(media.getId(), media);
            }

            mapper.addExtraField(IgMediaSnapshotHourlyMapper.FIELD_MEDIA, media);
            mediaSnapshotHourlyList.add(mapper.map(mediaRaw));
        }

        // TODO: Prepare for diffs
        mediaSnapshotHourlyRepo.saveAll(mediaSnapshotHourlyList);
    }
}
