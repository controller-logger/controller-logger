package helpers;

import com.github.valfirst.slf4jtest.TestLogger;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static List<ImmutableMap<String, String>> getFormattedLogEvents(@Nonnull TestLogger logger) {
        return logger.getAllLoggingEvents()
                .stream()
                .map(x -> ImmutableMap.of(
                        "level", x.getLevel().toString(),
                        "message", x.getMessage() + (x.getThrowable().isPresent() ? " " + x.getThrowable().get().toString() : "")
                ))
                .collect(Collectors.toList());
    }
}
