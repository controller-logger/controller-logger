package io.github.logger.controller.aspect;

import javax.annotation.Nonnull;
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
    protected static Set<String> paramBlacklist = new HashSet<>(Arrays.asList(
            "password",
            "passwd",
            "secret",
            "authorization",
            "api_key",
            "apikey",
            "access_token",
            "accesstoken"
    ));

    protected static String scrubbedValue = "xxxxx";

    protected static boolean enableDataScrubbing = true;

    // TODO HS 20180210 investigate a faster implementation of regex
    protected static Pattern paramBlacklistRegex;

    public void setDefaultScrubbedValue(@Nonnull String defaultScrubbedValue) {
        LoggerAspect.scrubbedValue = defaultScrubbedValue;
    }

    public void setEnableDataScrubbing(boolean enableDataScrubbing) {
        LoggerAspect.enableDataScrubbing = enableDataScrubbing;
    }

    public void setParamBlacklistRegex(@Nonnull String paramBlacklistRegex) {
        LoggerAspect.paramBlacklistRegex = Pattern.compile(paramBlacklistRegex);
    }

    public void setCustomParamBlacklist(@Nonnull Set<String> customParamBlacklist) {
        customParamBlacklist.forEach(i-> paramBlacklist.add(i.toLowerCase()));
    }

}
