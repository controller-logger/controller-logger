package io.github.logger.controller.aspect;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Base class for any logger aspect implementation.
 * It contains global configurations and settings to be used by all loggers.
 */
public abstract class BaseLoggerAspect {

    // TODO HS 20180211 think about @Sensitive annotation
    private static final List<String> DEFAULT_BLACKLISTED_PARAMS = Arrays.asList(
            "password",
            "passwd",
            "secret",
            "authorization",
            "api_key",
            "apikey",
            "access_token"
    );

    protected static String scrubbedValue = "xxxxx";

    protected static boolean enableDataScrubbing = true;

    // TODO HS 20180210 investigate a faster implementation of regex
    protected static String paramBlacklistRegex;

    protected static Set<String> paramBlacklist = new HashSet<>(DEFAULT_BLACKLISTED_PARAMS);

    public void setDefaultScrubbedValue(@Nonnull String defaultScrubbedValue) {
        BaseLoggerAspect.scrubbedValue = defaultScrubbedValue;
    }

    public void setEnableDataScrubbing(@Nonnull boolean enableDataScrubbing) {
        BaseLoggerAspect.enableDataScrubbing = enableDataScrubbing;
    }

    public void setParamBlacklistRegex(@Nonnull String paramBlacklistRegex) {
        BaseLoggerAspect.paramBlacklistRegex = paramBlacklistRegex;
    }

    protected void setCustomParamBlacklist(@Nonnull Set<String> customParamBlacklist) {
        paramBlacklist.addAll(customParamBlacklist);
    }

}
