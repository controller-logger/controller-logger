package helpers;

import bean.User;
import com.google.common.collect.ImmutableMap;
import io.github.logger.controller.bean.RequestContext;
import io.github.logger.controller.utils.RequestUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

public class MockUtils {

    public static List<Object> mockWorkflow(@Nonnull  ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = mockMethodSignature();
        mockProceedingJoinPoint(proceedingJoinPoint, methodSignature);

        List<Object> mockedObjects = new ArrayList<>();
        mockedObjects.add(methodSignature);
        mockedObjects.add(proceedingJoinPoint);
        return mockedObjects;
    }

    public static void mockProceedingJoinPoint(
            @Nonnull ProceedingJoinPoint joinPoint,
            @Nonnull MethodSignature signature
    ) throws Throwable {
        mockProceedingJoinPoint(
                joinPoint,
                new User(1, "foobar@example.com", "password"),
                signature,
                new DummyController(),
                new Object[]{1});
    }

    public static void mockProceedingJoinPoint(
            @Nonnull ProceedingJoinPoint joinPoint,
            @Nonnull Object returnValue,
            @Nonnull MethodSignature signature,
            @Nonnull Object target,
            @Nonnull Object[] methodParamValues
    ) throws Throwable {
        when(joinPoint.proceed()).thenReturn(returnValue);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getTarget()).thenReturn(target);
        when(joinPoint.getArgs()).thenReturn(methodParamValues);
    }

    public static MethodSignature mockMethodSignature() throws NoSuchMethodException {
        return mockMethodSignature(
                "getUser",
                User.class,
                new String[]{"userId"},
                new Class[]{int.class}
        );
    }

    public static MethodSignature mockMethodSignature(
            @Nonnull String methodName,
            @Nonnull Class returnType,
            @Nonnull String[] parameterNames,
            @Nonnull Class[] parameterTypes
    ) throws NoSuchMethodException {
        MethodSignature methodSignature = mock(MethodSignature.class, RETURNS_DEEP_STUBS);
        when(methodSignature.getName()).thenReturn(methodName);
        when(methodSignature.getReturnType()).thenReturn(returnType);
        when(methodSignature.getParameterNames()).thenReturn(parameterNames);

        Method method = DummyController.class.getMethod(methodName, parameterTypes);
        when(methodSignature.getMethod()).thenReturn(method);

        return methodSignature;
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
