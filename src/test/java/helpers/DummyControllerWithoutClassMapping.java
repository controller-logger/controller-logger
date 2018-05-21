package helpers;

import javax.annotation.Nonnull;

public class DummyControllerWithoutClassMapping {

    public String nonRestApiMethodWithArgs(@Nonnull String arg) {
        return arg;
    }

}
