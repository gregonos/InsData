package net.windia.insdata.model.mapper;

import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.model.client.IgAPIClientDataEntry;
import net.windia.insdata.model.client.IgAPIClientIgProfile;
import net.windia.insdata.model.client.IgAPIClientInsight;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.IgProfileSnapshot;
import net.windia.insdata.util.DateTimeUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public abstract class IgProfileSnapshotMapper<T extends IgProfileSnapshot> extends ResourceMapper<IgAPIClientIgProfile, T> {

    public static final String FIELD_IG_PROFILE = "igProfile";
    public static final String FIELD_CAPTURED_AT = "capturedAt";

    protected abstract T newInstance();

    @Override
    public T map(IgAPIClientIgProfile source) {

        T instance = newInstance();

        instance.setIgProfile((IgProfile) getExtraField(FIELD_IG_PROFILE));
        instance.setMediaCount(source.getMediaCount());
        instance.setFollowers(source.getFollowersCount());
        instance.setFollows(source.getFollowsCount());

        for (IgAPIClientInsight<Integer> profileInsight : source.getInsights().getData()) {
            String insightName = profileInsight.getName();
            if (IgAPIClientInsight.NAME_FOLLOWER_COUNT.equals(insightName)) {
                insightName = "new_followers";
            }

            boolean isNewDay = 0 == DateTimeUtils.hourOfFacebookServer();
            IgAPIClientDataEntry<Integer> insightEntry = profileInsight.getValues().get(isNewDay ? 0 : 1);

            String setterMethodName = "set" + CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, insightName);

            try {
                Method setter = instance.getClass().getMethod(setterMethodName, Integer.class);
                setter.invoke(instance, insightEntry.getValue());
            } catch (NoSuchMethodException e) {
                log.error("Failed to find setter method for stat field.", e);
            } catch (InvocationTargetException | IllegalAccessException e) {
                log.error("Failed to invoke setter method", e);
            }
        }

        return instance;
    }
}
