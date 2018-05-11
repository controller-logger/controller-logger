import bean.User;
import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import com.google.common.collect.ImmutableMap;
import helpers.MockUtils;
import io.github.logger.controller.aspect.GenericControllerAspect;
import io.github.logger.controller.utils.RequestUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RequestUtil.class})
public class TestGenericControllerAspect {

    private static final ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class, RETURNS_DEEP_STUBS);

    private final GenericControllerAspect aspect = new GenericControllerAspect();

    TestLogger logger = TestLoggerFactory.getTestLogger(GenericControllerAspect.class);

    @Test
    public void test1() throws Throwable {
        List<Object> mockedObjects = MockUtils.mockWorkflow(
                proceedingJoinPoint,
                new String[]{"userId"},
                new Class[]{int.class},
                new Object[]{1}
        );

        Object actualReturnedValue = aspect.log(proceedingJoinPoint);

        List<ImmutableMap<String, String>> actualLogMessages = logger.getAllLoggingEvents()
                .stream()
                .map(x -> ImmutableMap.of("level", x.getLevel().toString(), "message", x.getMessage()))
                .collect(Collectors.toList());

        List<Map<String, String>> expectedLogMessages = new ArrayList<>();
        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "getUser() called with arguments: userId: [1] called via " +
                                "url: [https://www.example.com], username: [Jean-Luc Picard]")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "getUser() took [0 ms] to complete")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "getUser() returned: [{\"id\":1,\"email\":\"foobar@example.com\",\"password\":\"password\"}]")
        );

        assertEquals(expectedLogMessages, actualLogMessages);

        User expectedReturnedValue = new User(1, "foobar@example.com", "password");
        assertEquals(expectedReturnedValue, actualReturnedValue);
        resetMock(mockedObjects);
    }

    private void resetMock(List<Object> mockedObjects) {
        mockedObjects.forEach(Mockito::reset);
    }

}