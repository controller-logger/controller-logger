import bean.User;
import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import helpers.DummyController;
import helpers.Utils;
import io.github.logger.controller.aspect.GenericControllerAspect;
import io.github.logger.controller.utils.RequestUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Method;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RequestUtil.class)
public class TestGenericControllerAspect {

    private static final ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class, RETURNS_DEEP_STUBS);

    private final GenericControllerAspect aspect = new GenericControllerAspect();

    TestLogger logger = TestLoggerFactory.getTestLogger(GenericControllerAspect.class);

    @BeforeClass
    public static void mockBehaviourSetup() throws Throwable {
        MethodSignature methodSignature = mock(MethodSignature.class, RETURNS_DEEP_STUBS);
        when(methodSignature.getReturnType()).thenReturn(User.class);
        when(methodSignature.getName()).thenReturn("getUser");
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"userId"});

        Method method = DummyController.class.getMethod("getUser", int.class);

        when(methodSignature.getMethod()).thenReturn(method);

        when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);

        User randomUser = random(User.class);
        when(proceedingJoinPoint.proceed()).thenReturn(randomUser);

        when(proceedingJoinPoint.getTarget()).thenReturn(new DummyController());

        when(proceedingJoinPoint.getArgs()).thenReturn(new Integer[]{1});

        Utils.getMockedRequestUtils();
    }

    @Test
    public void test1() throws Throwable {
        aspect.log(proceedingJoinPoint);

        logger.getAllLoggingEvents().forEach(System.out::println);
    }

}