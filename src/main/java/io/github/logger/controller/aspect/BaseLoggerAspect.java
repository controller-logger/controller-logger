package io.github.logger.controller.aspect;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Base class for any logger aspect implementation.
 * It contains global configurations and settings to be used by all loggers.
 */
public abstract class BaseLoggerAspect {

    // TODO HS 20180211 think about @Sensitive annotation
    protected static Set<String> paramBlacklist = new HashSet<>(Arrays.asList(
            "password",
            "passwd",
            "secret",
            "authorization",
            "api_key",
            "apikey",
            "access_token"
    ));

    protected static String scrubbedValue = "xxxxx";

    protected static boolean enableDataScrubbing = true;

    // TODO HS 20180210 investigate a faster implementation of regex
    protected static Pattern paramBlacklistRegex;

    public void setDefaultScrubbedValue(@Nonnull String defaultScrubbedValue) {
        BaseLoggerAspect.scrubbedValue = defaultScrubbedValue;
    }

    public void setEnableDataScrubbing(boolean enableDataScrubbing) {
        BaseLoggerAspect.enableDataScrubbing = enableDataScrubbing;
    }

    public void setParamBlacklistRegex(@Nonnull String paramBlacklistRegex) {
        BaseLoggerAspect.paramBlacklistRegex = Pattern.compile(paramBlacklistRegex);
    }

    protected void setCustomParamBlacklist(@Nonnull Set<String> customParamBlacklist) {
        paramBlacklist.addAll(customParamBlacklist);
    }

}
