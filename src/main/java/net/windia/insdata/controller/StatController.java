package net.windia.insdata.controller;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.constants.IgDiffMetric;
import net.windia.insdata.constants.IgSnapshotMetric;
import net.windia.insdata.constants.InsDataConstants;
import net.windia.insdata.model.assembler.IgProfileStatsDTOAssembler;
import net.windia.insdata.model.dto.IgProfileStatsDTO;
import net.windia.insdata.model.internal.IgOnlineFollowers;
import net.windia.insdata.model.internal.IgProfileDiff;
import net.windia.insdata.model.internal.IgProfileSnapshot;
import net.windia.insdata.service.IgOnlineFollowersService;
import net.windia.insdata.service.IgProfileDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/profiles", produces = {InsDataConstants.SERVER_CONTENT_TYPE_JSON_UTF8})
@ResponseBody
public class StatController {

    @Autowired
    private IgProfileDataService igProfileDataService;

    @Autowired
    private IgOnlineFollowersService igOnlineFollowersService;

    @Autowired
    private IgProfileStatsDTOAssembler igProfileStatsAssembler;


    @RequestMapping(value = "/{profileId}/stats/ig", method = RequestMethod.GET)
    public IgProfileStatsDTO getIgProfileStats(@PathVariable("profileId") Long profileId,
                                               @RequestParam("metrics") String metricsStr,
                                               @RequestParam("granularity") String granularity,
                                               @RequestParam("since") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date since,
                                               @RequestParam("until") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date until) {

        List<IgSnapshotMetric> snapshotFields = new ArrayList<>();
        List<IgDiffMetric> diffFields = new ArrayList<>();
        List<String> calcFields = new ArrayList<>();

        for (String field : metricsStr.split(",")) {
            if (IgSnapshotMetric.accepts(field)) {
                snapshotFields.add(IgSnapshotMetric.forName(field));
            } else if (IgDiffMetric.accepts(field)) {
                diffFields.add(IgDiffMetric.forName(field));
            } else {
                calcFields.add(field);
            }
        }

        List<? extends IgProfileSnapshot> snapshots = null;

        if (snapshotFields.size() > 0) {
            snapshots = igProfileDataService.getSnapshots(profileId, granularity, since, until);
        }

        List<? extends IgProfileDiff> diffs = null;

        if (diffFields.size() > 0) {
            diffs = igProfileDataService.getDiffs(profileId, granularity, since, until);
        }

        return igProfileStatsAssembler.assemble(snapshotFields, diffFields, calcFields, snapshots, diffs);
    }

    @RequestMapping(value = "/{profileId}/stats/ig/online-followers", method = RequestMethod.GET)
    public IgProfileStatsDTO getIgProfileOnlineFollowers(@PathVariable("profileId") Long profileId,
                                                         @RequestParam("granularity") String granularity,
                                                         @RequestParam(value = "since", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date since,
                                                         @RequestParam(value = "until", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date until) {

        List<IgOnlineFollowers> onlineFollowers = igOnlineFollowersService.getOnlineFollowers(profileId, granularity, since, until);

        return igProfileStatsAssembler.assemble(granularity, onlineFollowers);
    }
}
