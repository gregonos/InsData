package net.windia.insdata.service;

import net.windia.insdata.model.client.IgAPIClientMedia;
import net.windia.insdata.model.internal.IgMedia;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.mapper.IgMediaMapper;
import net.windia.insdata.repository.IgMediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IgMediaService {

    @Autowired
    private IgMediaRepository mediaRepo;

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
}
