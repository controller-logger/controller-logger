import bean.User;
import io.github.logger.controller.aspect.GenericControllerAspect;
import io.github.logger.controller.utils.RequestUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;

import java.lang.reflect.Method;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RequestUtil.class)
public class TestGenericControllerAspect {

    private static final ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class, RETURNS_DEEP_STUBS);

    private static final Logger logger = mock(Logger.class);

    private final GenericControllerAspect aspect = new GenericControllerAspect();

    @BeforeClass
    public static void mockBehaviourSetup() throws Throwable {
        GenericControllerAspect.setLogger(logger);

        MethodSignature methodSignature = mock(MethodSignature.class, RETURNS_DEEP_STUBS);
        when(methodSignature.getReturnType()).thenReturn(User.class);
        when(methodSignature.getName()).thenReturn("getUser");
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"userId"});

        Method method = LOL.class.getMethod("getUser", int.class);

        when(methodSignature.getMethod()).thenReturn(method);

        when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);

        User randomUser = random(User.class);
        when(proceedingJoinPoint.proceed()).thenReturn(randomUser);

        when(proceedingJoinPoint.getTarget()).thenReturn(new LOL());

        when(proceedingJoinPoint.getArgs()).thenReturn(new Integer[]{1});

        Utils.getMockedRequestUtils();
    }

    @Test
    public void test1() throws Throwable {
        aspect.log(proceedingJoinPoint);

        
    }

}