package io.github.logger.controller.aspect;

import bean.User;
import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import com.google.common.collect.ImmutableMap;
import helpers.MockUtils;
import helpers.Utils;
import io.github.logger.controller.aspect.GenericControllerAspect;
import io.github.logger.controller.utils.JsonUtil;
import io.github.logger.controller.utils.RequestUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.DoesNothing;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.doThrow;

@RunWith(PowerMockRunner.class)
public class TestGenericControllerAspect {

    private TestLogger logger = TestLoggerFactory.getTestLogger(GenericControllerAspect.class);

    @After
    public void clearLogs() {
        logger.clearAll();
    }

    @Test
    public void basicTest() throws Throwable {
        // mock behavior setup
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class, RETURNS_DEEP_STUBS);
        List<Object> mockedObjects = MockUtils.mockWorkflow(proceedingJoinPoint);

        RequestUtil mockedRequestUtil = MockUtils.mockRequestUtil();
        GenericControllerAspect aspect = new GenericControllerAspect(logger, new JsonUtil(), mockedRequestUtil);
        mockedObjects.add(mockedRequestUtil);

        // calling logic to be tested
        Object actualReturnedValue = aspect.log(proceedingJoinPoint);

        // preparing actual output
        List<ImmutableMap<String, String>> actualLogMessages = Utils.getFormattedLogEvents(logger);

        // preparing expected output
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

    @Test
    public void when_ErrorInPreExecLogic_then_ErrorLogged_but_JoinpointAndPostExecLogicStillExecute() throws Throwable {
        // mock behavior setup
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class, RETURNS_DEEP_STUBS);
        List<Object> mockedObjects = MockUtils.mockWorkflow(proceedingJoinPoint);

        RequestUtil mockedRequestUtil = MockUtils.mockRequestUtil();
        doThrow(new RuntimeException("Intentionally thrown error")).when(mockedRequestUtil).getRequestContext();
        mockedObjects.add(mockedRequestUtil);

        GenericControllerAspect aspect = new GenericControllerAspect(logger, new JsonUtil(), mockedRequestUtil);

        // calling logic to be tested
        Object actualReturnedValue = aspect.log(proceedingJoinPoint);

        // preparing actual output
        List<ImmutableMap<String, String>> actualLogMessages = Utils.getFormattedLogEvents(logger);

        // preparing expected output
        List<Map<String, String>> expectedLogMessages = new ArrayList<>();
        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "ERROR",
                        "message",
                        "Exception occurred in pre-proceed logic java.lang.RuntimeException: Intentionally thrown error")
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

    @Test
    public void when_MethodReturnTypeIsNull_then_PreLogicAndExecutionTimeIsStillLogged() throws Throwable {
        // mock behavior setup
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class, RETURNS_DEEP_STUBS);
        List<Object> mockedObjects = MockUtils.mockWorkflow(proceedingJoinPoint);

        MethodSignature signature = (MethodSignature) mockedObjects.get(0);
        when(signature.getReturnType()).thenReturn(Void.class);
        when(proceedingJoinPoint.proceed()).thenReturn(null);

        RequestUtil mockedRequestUtil = MockUtils.mockRequestUtil();
        GenericControllerAspect aspect = new GenericControllerAspect(logger, new JsonUtil(), mockedRequestUtil);
        mockedObjects.add(mockedRequestUtil);

        // calling logic to be tested
        Object actualReturnedValue = aspect.log(proceedingJoinPoint);

        // preparing actual output
        List<ImmutableMap<String, String>> actualLogMessages = Utils.getFormattedLogEvents(logger);

        // preparing expected output
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
                        "getUser() returned: [null]")
        );

        assertEquals(expectedLogMessages, actualLogMessages);
        assertNull(actualReturnedValue);
        resetMock(mockedObjects);
    }

    private void resetMock(List<Object> mockedObjects) {
        mockedObjects.forEach(Mockito::reset);
    }

}