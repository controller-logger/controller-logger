package helpers;

import bean.User;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.logger.controller.bean.RequestContext;
import io.github.logger.controller.utils.RequestUtil;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.powermock.api.mockito.PowerMockito;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

public class MockUtils {

    public static List<Object> mockWorkflow(
            ProceedingJoinPoint proceedingJoinPoint,
            String[] methodParamNames,
            Class[] methodParamTypes,
            Object[] methodParamValues
    ) throws Throwable {
        MethodSignature methodSignature = mock(MethodSignature.class, RETURNS_DEEP_STUBS);
        when(methodSignature.getReturnType()).thenReturn(User.class);
        when(methodSignature.getName()).thenReturn("getUser");
        when(methodSignature.getParameterNames()).thenReturn(methodParamNames);

        Method method = DummyController.class.getMethod("getUser", methodParamTypes);
        when(methodSignature.getMethod()).thenReturn(method);

        User user = new User(1, "foobar@example.com", "password");
        when(proceedingJoinPoint.proceed()).thenReturn(user);
        when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);
        when(proceedingJoinPoint.getTarget()).thenReturn(new DummyController());
        when(proceedingJoinPoint.getArgs()).thenReturn(methodParamValues);

        StopWatch mockedStopwatch = mock(StopWatch.class);
        when(mockedStopwatch.getTime()).thenReturn(5L);
        PowerMockito.whenNew(StopWatch.class).withAnyArguments().thenReturn(mockedStopwatch);

        return ImmutableList.of(methodSignature, proceedingJoinPoint, mockedStopwatch);
    }

    @Nonnull
    public static RequestUtil mockRequestUtil() {
        return mockRequestUtil(ImmutableMap.of(
                "url", "https://www.example.com",
                "username", "Jean-Luc Picard")
        );
    }

    @Nonnull
    public static RequestUtil mockRequestUtil(@Nonnull Map<String, String> context) {
        RequestUtil requestUtil = mock(RequestUtil.class);
        doReturn(new RequestContext(context)).when(requestUtil).getRequestContext();

        return requestUtil;
    }
}
