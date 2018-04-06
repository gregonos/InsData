package net.windia.insdata.service;

import net.windia.insdata.model.client.IgAPIClientMedia;
import net.windia.insdata.model.internal.IgProfile;

import java.time.OffsetDateTime;
import java.util.List;

public interface IgRawMediaHandler {
    boolean processRawMedia(IgProfile profile, List<IgAPIClientMedia> rawMediaList, OffsetDateTime capturedAt);
}
