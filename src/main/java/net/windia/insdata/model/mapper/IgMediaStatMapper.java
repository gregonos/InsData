package net.windia.insdata.model.mapper;

import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.model.client.IgAPIClientInsight;
import net.windia.insdata.model.client.IgAPIClientMedia;
import net.windia.insdata.model.internal.IgMedia;
import net.windia.insdata.model.internal.IgMediaStat;
import net.windia.insdata.model.internal.IgProfile;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public abstract class IgMediaStatMapper<T extends IgMediaStat> extends ResourceMapper<IgAPIClientMedia, IgMediaStat> {

    public static final String FIELD_MEDIA = "media";
    public static final String FIELD_IG_PROFILE = "igProfile";

    public abstract T getInstance();

    @Override
    public T map(IgAPIClientMedia source) {

        T mediaStat = getInstance();

        mediaStat.setMedia((IgMedia) getExtraField(FIELD_MEDIA));
        mediaStat.setIgProfile((IgProfile) getExtraField(FIELD_IG_PROFILE));

        mediaStat.setMediaType(source.getMediaType());
        mediaStat.setLikes(source.getLikeCount());
        mediaStat.setComments(source.getCommentsCount());

        for (IgAPIClientInsight<Integer> insightEntry : source.getInsights().getData()) {
            String name = insightEntry.getName();

            try {
                Method fieldSetter = mediaStat.getClass().getMethod(
                        "set" + CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name), Integer.class);
                fieldSetter.invoke(mediaStat, insightEntry.getValues().get(0).getValue());
            } catch (NoSuchMethodException e) {
                log.error("Failed to find setter method for stat field.", e);
            } catch (InvocationTargetException | IllegalAccessException e) {
                log.error("Failed to invoke setter method", e);
            }
        }

        return mediaStat;
    }
}
