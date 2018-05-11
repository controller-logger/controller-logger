package io.github.logger.controller.bean;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RequestContext {

    private final Map<String, String> context = new LinkedHashMap<>(4);

    public RequestContext add(@Nonnull String key, @Nullable String value) {
        context.put(key, value != null ? value : "null");
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RequestContext that = (RequestContext) o;

        return context != null ? context.equals(that.context) : that.context == null;
    }

    @Override
    public int hashCode() {
        return context != null ? context.hashCode() : 0;
    }

    @Override
    public String toString() {
        return context.entrySet().stream()
                .map(e -> e.getKey() + ": [" + e.getValue() + "]")
                .collect(Collectors.joining(", "));
    }
}
