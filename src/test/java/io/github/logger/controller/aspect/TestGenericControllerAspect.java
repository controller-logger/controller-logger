package io.github.logger.controller.aspect;

import bean.User;
import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import com.google.common.collect.ImmutableMap;
import helpers.DummyController;
import helpers.MockUtils;
import helpers.Utils;
import io.github.logger.controller.utils.JsonUtil;
import io.github.logger.controller.utils.RequestUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static helpers.MockUtils.mockMethodSignature;
import static helpers.MockUtils.mockProceedingJoinPoint;
import static org.junit.Assert.*;
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

    @Test
    public void when_ControllerMethodNeitherProducesNorConsumesJson_then_NoArgumentsAreSerializedForLogging() {
        // mock behavior setup
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class, RETURNS_DEEP_STUBS);

        MethodSignature methodSignature = null;
        try {
            methodSignature = mockMethodSignature(
                    "nonRestApiMethodWithArgs",
                    String.class,
                    new String[]{"arg"},
                    new Class[]{String.class}
            );
        } catch (NoSuchMethodException e) {
            fail(e.getMessage());
        }

        try {
            mockProceedingJoinPoint(
                    proceedingJoinPoint,
                    "Hello, World!",
                    methodSignature,
                    new DummyController(),
                    new Object[]{"Hello, World!"}
            );
        } catch (Throwable throwable) {
            fail(throwable.getMessage());
        }

        List<Object> mockedObjects = new ArrayList<>();
        mockedObjects.add(methodSignature);
        mockedObjects.add(proceedingJoinPoint);

        RequestUtil mockedRequestUtil = MockUtils.mockRequestUtil();
        JsonUtil mockedJsonUtil = spy(JsonUtil.class);
        GenericControllerAspect aspect = new GenericControllerAspect(logger, mockedJsonUtil, mockedRequestUtil);

        mockedObjects.add(mockedRequestUtil);
        mockedObjects.add(mockedJsonUtil);

        // calling logic to be tested
        Object actualReturnedValue = null;
        try {
            actualReturnedValue = aspect.log(proceedingJoinPoint);
        } catch (Throwable throwable) {
            fail(throwable.getMessage());
        }

        // preparing actual output
        List<ImmutableMap<String, String>> actualLogMessages = Utils.getFormattedLogEvents(logger);

        // preparing expected output
        List<Map<String, String>> expectedLogMessages = new ArrayList<>();
        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "nonRestApiMethodWithArgs() called with arguments: arg: [Hello, World!] called via " +
                                "url: [https://www.example.com], username: [Jean-Luc Picard]")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "nonRestApiMethodWithArgs() took [0 ms] to complete")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "nonRestApiMethodWithArgs() returned: [\"Hello, World!\"]")
        );

        assertEquals(expectedLogMessages, actualLogMessages);

        String expectedReturnedValue = "Hello, World!";
        assertEquals(expectedReturnedValue, actualReturnedValue);

        verify(mockedJsonUtil, times(1)).toJson("Hello, World!");
        verifyNoMoreInteractions(mockedJsonUtil);

        resetMock(mockedObjects);
    }

    @Test
    public void when_ControllerMethodAcceptsJson_then_InputArgsWithRequestBodyAnnotationAreSerialized() {
        // mock behavior setup
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class, RETURNS_DEEP_STUBS);

        MethodSignature methodSignature = null;
        try {
            methodSignature = mockMethodSignature(
                    "createUser",
                    User.class,
                    new String[]{"user", "source"},
                    new Class[]{User.class, String.class}
            );
        } catch (NoSuchMethodException e) {
            fail(e.getMessage());
        }

        User user = new User(1, "foobar@example.com", "password");
        try {
            mockProceedingJoinPoint(
                    proceedingJoinPoint,
                    user,
                    methodSignature,
                    new DummyController(),
                    new Object[]{user, "homePage"}
            );
        } catch (Throwable throwable) {
            fail(throwable.getMessage());
        }

        List<Object> mockedObjects = new ArrayList<>();
        mockedObjects.add(methodSignature);
        mockedObjects.add(proceedingJoinPoint);

        RequestUtil mockedRequestUtil = MockUtils.mockRequestUtil();
        mockedObjects.add(mockedRequestUtil);

        JsonUtil mockedJsonUtil = spy(JsonUtil.class);
        mockedObjects.add(mockedJsonUtil);

        GenericControllerAspect aspect = new GenericControllerAspect(logger, mockedJsonUtil, mockedRequestUtil);

        // calling logic to be tested
        Object actualReturnedValue = null;
        try {
            actualReturnedValue = aspect.log(proceedingJoinPoint);
        } catch (Throwable throwable) {
            fail(throwable.getMessage());
        }

        // preparing actual output
        List<ImmutableMap<String, String>> actualLogMessages = Utils.getFormattedLogEvents(logger);

        // preparing expected output
        List<Map<String, String>> expectedLogMessages = new ArrayList<>();
        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "createUser() called with arguments: user: " +
                                "[{\"id\":1,\"email\":\"foobar@example.com\",\"password\":\"password\"}]" +
                                ", source: [homePage] " +
                                "called via url: [https://www.example.com], username: [Jean-Luc Picard]")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "createUser() took [0 ms] to complete")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "createUser() returned: [{\"id\":1,\"email\":\"foobar@example.com\",\"password\":\"password\"}]")
        );

        assertEquals(expectedLogMessages, actualLogMessages);

        User expectedReturnedValue = new User(1, "foobar@example.com", "password");
        assertEquals(expectedReturnedValue, actualReturnedValue);

        // once for input and once for output user object
        verify(mockedJsonUtil, times(2)).toJson(user);
        verifyNoMoreInteractions(mockedJsonUtil);

        resetMock(mockedObjects);
    }

    @Test
    public void when_FunctionAcceptsNonJsonType_then_InputArgumentsAreNotSerialized() {
        // mock behavior setup
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class, RETURNS_DEEP_STUBS);

        MethodSignature methodSignature = null;
        try {
            methodSignature = mockMethodSignature(
                    "saveNote",
                    Boolean.class,
                    new String[]{"text"},
                    new Class[]{String.class}
            );
        } catch (NoSuchMethodException e) {
            fail(e.getMessage());
        }

        try {
            mockProceedingJoinPoint(
                    proceedingJoinPoint,
                    true,
                    methodSignature,
                    new DummyController(),
                    new Object[]{"Lorem ipsum"}
            );
        } catch (Throwable throwable) {
            fail(throwable.getMessage());
        }

        List<Object> mockedObjects = new ArrayList<>();
        mockedObjects.add(methodSignature);
        mockedObjects.add(proceedingJoinPoint);

        RequestUtil mockedRequestUtil = MockUtils.mockRequestUtil();
        mockedObjects.add(mockedRequestUtil);

        JsonUtil mockedJsonUtil = spy(JsonUtil.class);
        mockedObjects.add(mockedJsonUtil);

        GenericControllerAspect aspect = new GenericControllerAspect(logger, mockedJsonUtil, mockedRequestUtil);

        // calling logic to be tested
        Object actualReturnedValue = null;
        try {
            actualReturnedValue = aspect.log(proceedingJoinPoint);
        } catch (Throwable throwable) {
            fail(throwable.getMessage());
        }

        // preparing actual output
        List<ImmutableMap<String, String>> actualLogMessages = Utils.getFormattedLogEvents(logger);

        // preparing expected output
        List<Map<String, String>> expectedLogMessages = new ArrayList<>();
        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "saveNote() called with arguments: text: [Lorem ipsum] " +
                                "called via url: [https://www.example.com], username: [Jean-Luc Picard]")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "saveNote() took [0 ms] to complete")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "saveNote() returned: [true]")
        );

        assertEquals(expectedLogMessages, actualLogMessages);

        assertEquals(true, actualReturnedValue);

        // once for input and once for output user object
        verify(mockedJsonUtil, times(1)).toJson(true);
        verifyNoMoreInteractions(mockedJsonUtil);

        resetMock(mockedObjects);
    }

    @Test
    public void when_ArgIsNull_then_ArgValuIsLoggedAsNULL() {
        // mock behavior setup
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class, RETURNS_DEEP_STUBS);

        MethodSignature methodSignature = null;
        try {
            methodSignature = mockMethodSignature(
                    "createUser",
                    User.class,
                    new String[]{"user", "source"},
                    new Class[]{User.class, String.class}
            );
        } catch (NoSuchMethodException e) {
            fail(e.getMessage());
        }

        try {
            mockProceedingJoinPoint(
                    proceedingJoinPoint,
                    new User(1, "foobar@example.com", "password"),
                    methodSignature,
                    new DummyController(),
                    new Object[]{new User(1, "foobar@example.com", "password"), null}
            );
        } catch (Throwable throwable) {
            fail(throwable.getMessage());
        }

        List<Object> mockedObjects = new ArrayList<>();
        mockedObjects.add(methodSignature);
        mockedObjects.add(proceedingJoinPoint);

        RequestUtil mockedRequestUtil = MockUtils.mockRequestUtil();
        JsonUtil mockedJsonUtil = spy(JsonUtil.class);
        mockedObjects.add(mockedJsonUtil);
        GenericControllerAspect aspect = new GenericControllerAspect(logger, mockedJsonUtil, mockedRequestUtil);
        mockedObjects.add(mockedRequestUtil);

        // calling logic to be tested
        Object actualReturnedValue = null;
        try {
            actualReturnedValue = aspect.log(proceedingJoinPoint);
        } catch (Throwable throwable) {
            fail(throwable.getMessage());
        }

        // preparing actual output
        List<ImmutableMap<String, String>> actualLogMessages = Utils.getFormattedLogEvents(logger);

        // preparing expected output
        List<Map<String, String>> expectedLogMessages = new ArrayList<>();
        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "createUser() called with arguments: user: [{\"id\":1,\"email\":\"foobar@example.com\",\"password\":\"password\"}]," +
                                " source: [null]" +
                                " called via" +
                                " url: [https://www.example.com], username: [Jean-Luc Picard]")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "createUser() took [0 ms] to complete")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "createUser() returned: [{\"id\":1,\"email\":\"foobar@example.com\",\"password\":\"password\"}]")
        );

        assertEquals(expectedLogMessages, actualLogMessages);

        User expectedReturnedValue = new User(1, "foobar@example.com", "password");
        assertEquals(expectedReturnedValue, actualReturnedValue);

        verify(mockedJsonUtil, times(2)).toJson(new User(1, "foobar@example.com", "password"));
        verifyNoMoreInteractions(mockedJsonUtil);

        resetMock(mockedObjects);
    }

    @Test
    public void when_ArgRequiringSerializationIsNull_then_ArgIsSerializedToNULLString() {
        // mock behavior setup
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class, RETURNS_DEEP_STUBS);

        MethodSignature methodSignature = null;
        try {
            methodSignature = mockMethodSignature(
                    "createUser",
                    User.class,
                    new String[]{"user", "source"},
                    new Class[]{User.class, String.class}
            );
        } catch (NoSuchMethodException e) {
            fail(e.getMessage());
        }

        try {
            mockProceedingJoinPoint(
                    proceedingJoinPoint,
                    null,
                    methodSignature,
                    new DummyController(),
                    new Object[]{null, null}
            );
        } catch (Throwable throwable) {
            fail(throwable.getMessage());
        }

        List<Object> mockedObjects = new ArrayList<>();
        mockedObjects.add(methodSignature);
        mockedObjects.add(proceedingJoinPoint);

        RequestUtil mockedRequestUtil = MockUtils.mockRequestUtil();
        JsonUtil mockedJsonUtil = spy(JsonUtil.class);
        mockedObjects.add(mockedJsonUtil);
        GenericControllerAspect aspect = new GenericControllerAspect(logger, mockedJsonUtil, mockedRequestUtil);
        mockedObjects.add(mockedRequestUtil);

        // calling logic to be tested
        Object actualReturnedValue = null;
        try {
            actualReturnedValue = aspect.log(proceedingJoinPoint);
        } catch (Throwable throwable) {
            fail(throwable.getMessage());
        }

        // preparing actual output
        List<ImmutableMap<String, String>> actualLogMessages = Utils.getFormattedLogEvents(logger);

        // preparing expected output
        List<Map<String, String>> expectedLogMessages = new ArrayList<>();
        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "createUser() called with arguments: user: [null], source: [null]" +
                                " called via" +
                                " url: [https://www.example.com], username: [Jean-Luc Picard]")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "createUser() took [0 ms] to complete")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "createUser() returned: [null]")
        );

        assertEquals(expectedLogMessages, actualLogMessages);

        assertNull(actualReturnedValue);

        // once for input user arg and once for output
        verify(mockedJsonUtil, times(2)).toJson(null);
        verifyNoMoreInteractions(mockedJsonUtil);

        resetMock(mockedObjects);
    }

    private void resetMock(List<Object> mockedObjects) {
        mockedObjects.forEach(Mockito::reset);
    }

}