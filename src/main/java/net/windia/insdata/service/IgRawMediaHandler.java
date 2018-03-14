package net.windia.insdata.service;

import net.windia.insdata.model.client.IgAPIClientMedia;
import net.windia.insdata.model.internal.IgProfile;

import java.util.Date;
import java.util.List;

public interface IgRawMediaHandler {
    boolean processRawMedia(IgProfile profile, List<IgAPIClientMedia> rawMediaList, Date capturedAt);
}
