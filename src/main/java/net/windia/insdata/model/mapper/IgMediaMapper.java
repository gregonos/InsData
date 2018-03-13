package net.windia.insdata.model.mapper;

import net.windia.insdata.model.client.IgAPIClientMedia;
import net.windia.insdata.model.internal.IgMedia;
import net.windia.insdata.model.internal.IgProfile;

public class IgMediaMapper extends ResourceMapper<IgAPIClientMedia, IgMedia> {

    public static final String FIELD_IG_PROFILE = "igProfile";

    @Override
    public IgMedia map(IgAPIClientMedia source) {

        IgMedia media = new IgMedia();

        media.setId(source.getId());
        media.setIgProfile((IgProfile) getExtraField(FIELD_IG_PROFILE));
        media.setIgId(source.getIgId());
        media.setCaption(source.getCaption());
        media.setType(source.getMediaType());
        media.setPermalink(source.getPermalink());
        media.setShortcode(source.getShortcode());
        media.setUrl(source.getMediaUrl());
        media.setThumbnailUrl(source.getThumbnailUrl());
        media.setCreatedAt(source.getTimestamp());

        return media;
    }
}
