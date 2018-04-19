import io.github.logger.controller.bean.RequestContext;
import io.github.logger.controller.utils.RequestUtil;
import org.junit.runner.Request;
import org.powermock.api.mockito.PowerMockito;

import static org.mockito.Mockito.when;

public class Utils {
    public static void getMockedRequestUtils() {
        PowerMockito.mockStatic(RequestUtil.class);

        RequestContext context = new RequestContext()
                .add("url", "https://www.example.com")
                .add("username", "Jean-Luc Picard");

        when(RequestUtil.getRequestContext()).thenReturn(context);
    }
}
