package net.windia.insdata.model.mapper;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class ResourceMapper<S, T> {

    private Map<String, Object> extraFields;

    public void addExtraField(String key, Object value) {
        if (null == extraFields) {
            extraFields = new HashMap<>();
        }

        extraFields.put(key, value);
    }

    public Object getExtraField(String key) {
        return null == extraFields ? null : extraFields.get(key);
    }

    public abstract T map(S source);

    public List<T> map(Collection<S> sourceCollection) {
        List<T> result = new ArrayList<>();

        if (null == sourceCollection) {
            return result;
        }

        result.addAll(sourceCollection.stream().map(this::map).collect(Collectors.toList()));

        return result;
    }
}
