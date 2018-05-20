package io.github.logger.controller.aspect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Base class for any logger aspect implementation.
 * It contains global configurations and settings to be used by all loggers.
 */
public abstract class LoggerAspect {

    // TODO HS 20180211 think about @Sensitive annotation
    protected Set<String> paramBlacklist = new HashSet<>(Arrays.asList(
            "password",
            "passwd",
            "secret",
            "authorization",
            "api_key",
            "apikey",
            "access_token",
            "accesstoken"
    ));

    @Nonnull
    protected String scrubbedValue = "xxxxx";

    protected boolean enableDataScrubbing = true;

    // TODO HS 20180210 investigate a faster implementation of regex
    @Nullable
    protected Pattern paramBlacklistRegex;

    public void setDefaultScrubbedValue(@Nonnull String defaultScrubbedValue) {
        scrubbedValue = defaultScrubbedValue;
    }

    public void setEnableDataScrubbing(boolean enableDataScrubbing) {
        enableDataScrubbing = enableDataScrubbing;
    }

    public void setParamBlacklistRegex(@Nonnull String paramBlacklistRegex) {
        this.paramBlacklistRegex = Pattern.compile(paramBlacklistRegex);
    }

    public void setCustomParamBlacklist(@Nonnull Set<String> customParamBlacklist) {
        customParamBlacklist.forEach(i-> paramBlacklist.add(i.toLowerCase()));
    }

}
