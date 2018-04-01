package net.windia.insdata.controller;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.constants.*;
import net.windia.insdata.exception.UnsupportedGranularityException;
import net.windia.insdata.exception.UnsupportedMetricException;
import net.windia.insdata.metric.IgDataSource;
import net.windia.insdata.metric.IgMetric;
import net.windia.insdata.metric.IgOnlineFollowersGranularity;
import net.windia.insdata.metric.StatGranularity;
import net.windia.insdata.model.assembler.IgProfileStatsDTOAssembler;
import net.windia.insdata.model.dto.IgProfileStatsDTO;
import net.windia.insdata.model.internal.IgOnlineFollowers;
import net.windia.insdata.model.internal.IgStat;
import net.windia.insdata.service.IgOnlineFollowersService;
import net.windia.insdata.service.IgProfileDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

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
                                               @RequestParam("until") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date until)

            throws UnsupportedGranularityException, UnsupportedMetricException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        StatGranularity gran = StatGranularity.forName(granularity);
        if (null == gran) {
            throw new UnsupportedGranularityException(granularity);
        }

        String[] metricsInStr = metricsStr.split(",");
        List<IgMetric> metrics = new ArrayList<>(metricsInStr.length);
        List<String> illegalMetrics = new ArrayList<>();
        Set<IgDataSource> requiredSources = new HashSet<>();
        for (String s : metricsInStr) {
            IgMetric metric;

            metric = IgMetric.forName(s.toUpperCase());
            if (null == metric) {
                illegalMetrics.add(s);
                continue;
            }

            metrics.add(metric);

            if (!metric.supports(gran)) {
                throw new UnsupportedGranularityException(metric, gran);
            }

            requiredSources.addAll(metric.getSources(gran));
        }

        if (illegalMetrics.size() > 0) {
            throw new UnsupportedMetricException(illegalMetrics);
        }

        Map<IgDataSource, List<? extends IgStat>> sourceMap = new EnumMap<>(IgDataSource.class);
        for (IgDataSource source : requiredSources) {
            Method sourceGetter = igProfileDataService.getClass().getMethod(
                    "get" + source.getName(), Long.class, StatGranularity.class, Date.class, Date.class);

            StatGranularity sourceGran = source.getGranularity();
            if (null == sourceGran) {
                sourceGran = gran;
            }

            @SuppressWarnings("unchecked")
            List<? extends IgStat> sourceCollection =
                    (List<? extends IgStat>) sourceGetter.invoke(igProfileDataService, profileId, sourceGran, since, until);

            sourceMap.put(source, sourceCollection);
        }

        return igProfileStatsAssembler.assemble(metrics, gran, sourceMap);
    }

    @RequestMapping(value = "/{profileId}/stats/ig/online-followers", method = RequestMethod.GET)
    public IgProfileStatsDTO getIgProfileOnlineFollowers(@PathVariable("profileId") Long profileId,
                                                         @RequestParam("granularity") String granularity,
                                                         @RequestParam(value = "since", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date since,
                                                         @RequestParam(value = "until", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date until) {

        List<IgOnlineFollowers> onlineFollowers =
                igOnlineFollowersService.getOnlineFollowers(
                        profileId,
                        IgOnlineFollowersGranularity.forName(granularity),
                        since, until);

        return igProfileStatsAssembler.assemble(granularity, onlineFollowers);
    }
}
