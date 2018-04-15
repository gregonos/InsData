package net.windia.insdata.model.assembler;

import net.windia.insdata.metric.IgAudienceStatType;
import net.windia.insdata.model.dto.IgProfileAudienceStatsDTO;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.IgProfileAudienceDaily;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class IgProfileAudienceDTOAssembler {

    private static final String DIMENSION_SECTION = "section";
    private static final String DIMENSION_COUNT = "count";
    private static final String DIMENSION_DIFF = "diff";

    public IgProfileAudienceStatsDTO assemble(IgProfile profile, IgAudienceStatType type, List<IgProfileAudienceDaily> rawRecs) {
        IgProfileAudienceStatsDTO target = new IgProfileAudienceStatsDTO();

        target.setType(type.name().toLowerCase());
        List<String> dimensions = new ArrayList<>(3);
        dimensions.add(DIMENSION_SECTION);
        dimensions.add(DIMENSION_COUNT);
        dimensions.add(DIMENSION_DIFF);
        target.setDimensions(dimensions);

        List<List<Object>> dataList = new ArrayList<>(rawRecs.size());
        if (rawRecs.size() > 0) {
            target.setSeenAt(rawRecs.get(0).getCapturedAt().atZoneSameInstant(profile.getUser().getZoneId()));

            for (IgProfileAudienceDaily rec : rawRecs) {
                List<Object> dataItem = new ArrayList<>(3);
                dataItem.add(getDisplayValue(rec.getSection(), type));
                dataItem.add(rec.getCount());
                dataItem.add(rec.getDiff());
                dataList.add(dataItem);
            }
        }

        target.setData(dataList);

        return target;
    }

    private String getDisplayValue(String section, IgAudienceStatType type) {
        if (IgAudienceStatType.COUNTRY == type) {
            return translateToCountry(section);
        } else if (IgAudienceStatType.LOCALE == type) {
            return translateToLocale(section);
        } else {
            return section;
        }
    }

    private String translateToLocale(String section) {
        if ("es_LA".equals(section)) {
            return "Spanish (Latin America)";
        }
        String[] parts = section.split("_");
        return new Locale(parts[0], parts[1]).getDisplayName();
    }

    private String translateToCountry(String section) {
        return new Locale("", section).getDisplayCountry();
    }
}
