package io.github.logger.controller.aspect;

import bean.User;
import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import com.google.common.collect.ImmutableMap;
import helpers.DummyController;
import helpers.MockUtils;
import helpers.Utils;
import io.github.logger.controller.annotation.Logging;
import io.github.logger.controller.utils.JsonUtil;
import io.github.logger.controller.utils.RequestUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static helpers.MockUtils.mockMethodSignature;
import static helpers.MockUtils.mockProceedingJoinPoint;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@RunWith(PowerMockRunner.class)
public class TestGenericControllerAspect {

    private TestLogger logger = TestLoggerFactory.getTestLogger(GenericControllerAspect.class);

    @After
    public void clearLogs() {
        logger.clearAll();
    }

    @Test
    public void baseTest() throws Throwable {
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
                        "getUser() returned: [void]")
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

    @Test
    public void when_FunctionReturnsVoid_then_returnValueIsSerializedAsVoidString() {
        // mock behavior setup
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class, RETURNS_DEEP_STUBS);

        MethodSignature methodSignature = null;
        try {
            methodSignature = mockMethodSignature(
                    "saveMemo",
                    Void.class,
                    new String[]{"text"},
                    new Class[]{String.class}
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
                        "saveMemo() called with arguments: text: [Lorem ipsum] " +
                                "called via url: [https://www.example.com], username: [Jean-Luc Picard]")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "saveMemo() took [0 ms] to complete")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "saveMemo() returned: [void]")
        );

        assertEquals(expectedLogMessages, actualLogMessages);
        assertNull(actualReturnedValue);

        verifyNoMoreInteractions(mockedJsonUtil);

        resetMock(mockedObjects);
    }

    @Test
    public void when_ErrorOccursDuringSerialization_then_ThisErrorIsLogged() throws Throwable {
        // mock behavior setup
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class, RETURNS_DEEP_STUBS);
        List<Object> mockedObjects = MockUtils.mockWorkflow(proceedingJoinPoint);

        RequestUtil mockedRequestUtil = MockUtils.mockRequestUtil();
        mockedObjects.add(mockedRequestUtil);

        JsonUtil mockedJsonUtil = mock(JsonUtil.class);
        mockedObjects.add(mockedJsonUtil);
        when(mockedJsonUtil.toJson(any(User.class))).thenThrow(new RuntimeException("Intentionally thrown error"));

        GenericControllerAspect aspect = new GenericControllerAspect(logger, mockedJsonUtil, mockedRequestUtil);

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
                        "WARN",
                        "message",
                        "Unable to serialize object of type [bean.User] for logging java.lang.RuntimeException: Intentionally thrown error")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "getUser() returned: []")
        );

        assertEquals(expectedLogMessages, actualLogMessages);

        User expectedReturnedValue = new User(1, "foobar@example.com", "password");
        assertEquals(expectedReturnedValue, actualReturnedValue);

        verify(mockedJsonUtil, times(1)).toJson(new User(1, "foobar@example.com", "password"));
        verifyNoMoreInteractions(mockedJsonUtil);

        resetMock(mockedObjects);
    }

    @Test
    public void when_FunctionArgumentsAreMockedObjects_then_TheyAreSerializedToMockedObjectStrings() {
        // mock behavior setup
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class, RETURNS_DEEP_STUBS);

        MethodSignature methodSignature = null;
        try {
            methodSignature = mockMethodSignature(
                    "createUser",
                    String.class,
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
                    new Object[]{mock(User.class), "homePage"}
            );
        } catch (Throwable throwable) {
            fail(throwable.getMessage());
        }

        List<Object> mockedObjects = new ArrayList<>();
        mockedObjects.add(methodSignature);
        mockedObjects.add(proceedingJoinPoint);

        RequestUtil mockedRequestUtil = MockUtils.mockRequestUtil();
        mockedObjects.add(mockedRequestUtil);

        GenericControllerAspect aspect = new GenericControllerAspect(logger, new JsonUtil(), mockedRequestUtil);

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
                        "createUser() called with arguments: user: [Mock Object], source: [homePage] called via " +
                                "url: [https://www.example.com], username: [Jean-Luc Picard]")
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

        resetMock(mockedObjects);
    }

    @Test
    public void when_FunctionReturnsMockedObjects_then_TheyAreSerializedToMockedObjectStrings() {
        // mock behavior setup
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class, RETURNS_DEEP_STUBS);

        MethodSignature methodSignature = null;
        try {
            methodSignature = mockMethodSignature(
                    "createUser",
                    String.class,
                    new String[]{"user", "source"},
                    new Class[]{User.class, String.class}
            );
        } catch (NoSuchMethodException e) {
            fail(e.getMessage());
        }

        try {
            mockProceedingJoinPoint(
                    proceedingJoinPoint,
                    mock(User.class),
                    methodSignature,
                    new DummyController(),
                    new Object[]{new User(1, "foobar@example.com", "password"), "homePage"}
            );
        } catch (Throwable throwable) {
            fail(throwable.getMessage());
        }

        List<Object> mockedObjects = new ArrayList<>();
        mockedObjects.add(methodSignature);
        mockedObjects.add(proceedingJoinPoint);

        RequestUtil mockedRequestUtil = MockUtils.mockRequestUtil();
        mockedObjects.add(mockedRequestUtil);

        GenericControllerAspect aspect = new GenericControllerAspect(logger, new JsonUtil(), mockedRequestUtil);

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
                        "createUser() called with arguments: user: [{\"id\":1,\"email\":\"foobar@example.com\",\"password\":\"password\"}], " +
                                "source: [homePage] called via " +
                                "url: [https://www.example.com], username: [Jean-Luc Picard]")
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
                        "createUser() returned: [Mock Object]")
        );

        assertEquals(expectedLogMessages, actualLogMessages);
        assertNotNull(actualReturnedValue);
        assertTrue(actualReturnedValue.getClass().getName().toLowerCase().contains("mock"));

        resetMock(mockedObjects);
    }

    @Test
    public void when_FunctionArgumentsAreMockAbjectsAndItReturnsMockedObjects_then_TheyAreAllSerializedToMockedObjectStrings() {
        // mock behavior setup
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class, RETURNS_DEEP_STUBS);

        MethodSignature methodSignature = null;
        try {
            methodSignature = mockMethodSignature(
                    "createUser",
                    String.class,
                    new String[]{"user", "source"},
                    new Class[]{User.class, String.class}
            );
        } catch (NoSuchMethodException e) {
            fail(e.getMessage());
        }

        try {
            mockProceedingJoinPoint(
                    proceedingJoinPoint,
                    mock(User.class),
                    methodSignature,
                    new DummyController(),
                    new Object[]{mock(User.class), "homePage"}
            );
        } catch (Throwable throwable) {
            fail(throwable.getMessage());
        }

        List<Object> mockedObjects = new ArrayList<>();
        mockedObjects.add(methodSignature);
        mockedObjects.add(proceedingJoinPoint);

        RequestUtil mockedRequestUtil = MockUtils.mockRequestUtil();
        mockedObjects.add(mockedRequestUtil);

        GenericControllerAspect aspect = new GenericControllerAspect(logger, new JsonUtil(), mockedRequestUtil);

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
                        "createUser() called with arguments: user: [Mock Object], " +
                                "source: [homePage] called via " +
                                "url: [https://www.example.com], username: [Jean-Luc Picard]")
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
                        "createUser() returned: [Mock Object]")
        );

        assertEquals(expectedLogMessages, actualLogMessages);
        assertNotNull(actualReturnedValue);
        assertTrue(actualReturnedValue.getClass().getName().toLowerCase().contains("mock"));

        resetMock(mockedObjects);
    }

    @Test
    public void when_FunctionGetsByteArrayResourceAsInput_then_ItsFileSizeIsLogged() throws Throwable {
        // mock behavior setup
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class, RETURNS_DEEP_STUBS);

        MethodSignature methodSignature = mockMethodSignature(
                "uploadFile",
                Void.class,
                new String[]{"file"},
                new Class[]{ByteArrayResource.class}
        );

        ByteArrayResource mockedFile = mock(ByteArrayResource.class);
        when(mockedFile.contentLength()).thenReturn(1024L);

        mockProceedingJoinPoint(
                proceedingJoinPoint,
                 null,
                methodSignature,
                new DummyController(),
                new Object[]{mockedFile}
        );

        List<Object> mockedObjects = new ArrayList<>();
        mockedObjects.add(methodSignature);
        mockedObjects.add(proceedingJoinPoint);
        mockedObjects.add(mockedFile);

        RequestUtil mockedRequestUtil = MockUtils.mockRequestUtil();
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
                        "INFO",
                        "message",
                        "uploadFile() called with arguments: file: [file of size:[1024 B]] called via " +
                                "url: [https://www.example.com], username: [Jean-Luc Picard]")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "uploadFile() took [0 ms] to complete")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "uploadFile() returned: [void]")
        );

        assertEquals(expectedLogMessages, actualLogMessages);
        assertNull(actualReturnedValue);
        resetMock(mockedObjects);
    }

    @Test
    public void when_FunctionReturnsByteArrayResource_then_ItsFileSizeIsLogged() throws Throwable {
        // mock behavior setup
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class, RETURNS_DEEP_STUBS);

        MethodSignature methodSignature = mockMethodSignature(
                "getFileByteArrayResource",
                ByteArrayResource.class,
                new String[]{},
                new Class[]{}
        );

        ByteArrayResource mockedFile = mock(ByteArrayResource.class);
        when(mockedFile.contentLength()).thenReturn(1024L);

        mockProceedingJoinPoint(
                proceedingJoinPoint,
                mockedFile,
                methodSignature,
                new DummyController(),
                new Object[]{}
        );

        List<Object> mockedObjects = new ArrayList<>();
        mockedObjects.add(methodSignature);
        mockedObjects.add(proceedingJoinPoint);
        mockedObjects.add(mockedFile);

        RequestUtil mockedRequestUtil = MockUtils.mockRequestUtil();
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
                        "INFO",
                        "message",
                        "getFileByteArrayResource() called via url: [https://www.example.com], username: [Jean-Luc Picard]")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "getFileByteArrayResource() took [0 ms] to complete")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "getFileByteArrayResource() returned: [file of size:[1024 B]]")
        );

        assertEquals(expectedLogMessages, actualLogMessages);
        assertNotNull(actualReturnedValue);
        resetMock(mockedObjects);
    }

    @Test
    public void when_FunctionGetsMultipartFileAsInput_then_ItsFileSizeIsLogged() throws Throwable {
        // mock behavior setup
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class, RETURNS_DEEP_STUBS);

        MethodSignature methodSignature = mockMethodSignature(
                "uploadFile",
                Void.class,
                new String[]{"file"},
                new Class[]{MultipartFile.class}
        );

        MultipartFile mockedFile = mock(MultipartFile.class);
        when(mockedFile.getSize()).thenReturn(1024L);

        mockProceedingJoinPoint(
                proceedingJoinPoint,
                null,
                methodSignature,
                new DummyController(),
                new Object[]{mockedFile}
        );

        List<Object> mockedObjects = new ArrayList<>();
        mockedObjects.add(methodSignature);
        mockedObjects.add(proceedingJoinPoint);
        mockedObjects.add(mockedFile);

        RequestUtil mockedRequestUtil = MockUtils.mockRequestUtil();
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
                        "INFO",
                        "message",
                        "uploadFile() called with arguments: file: [file of size:[1024 B]] called via " +
                                "url: [https://www.example.com], username: [Jean-Luc Picard]")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "uploadFile() took [0 ms] to complete")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "uploadFile() returned: [void]")
        );

        assertEquals(expectedLogMessages, actualLogMessages);
        assertNull(actualReturnedValue);
        resetMock(mockedObjects);
    }

    @Test
    public void when_FunctionReturnsMultipartFile_then_ItsFileSizeIsLogged() throws Throwable {
        // mock behavior setup
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class, RETURNS_DEEP_STUBS);

        MethodSignature methodSignature = mockMethodSignature(
                "getFileMultipartFile",
                MultipartFile.class,
                new String[]{},
                new Class[]{}
        );

        MultipartFile mockedFile = mock(MultipartFile.class);
        when(mockedFile.getSize()).thenReturn(1024L);

        mockProceedingJoinPoint(
                proceedingJoinPoint,
                mockedFile,
                methodSignature,
                new DummyController(),
                new Object[]{}
        );

        List<Object> mockedObjects = new ArrayList<>();
        mockedObjects.add(methodSignature);
        mockedObjects.add(proceedingJoinPoint);
        mockedObjects.add(mockedFile);

        RequestUtil mockedRequestUtil = MockUtils.mockRequestUtil();
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
                        "INFO",
                        "message",
                        "getFileMultipartFile() called via url: [https://www.example.com], username: [Jean-Luc Picard]")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "getFileMultipartFile() took [0 ms] to complete")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "getFileMultipartFile() returned: [file of size:[1024 B]]")
        );

        assertEquals(expectedLogMessages, actualLogMessages);
        assertNotNull(actualReturnedValue);
        resetMock(mockedObjects);
    }

    @Test
    public void when_ArgumentNameIsInDataScrubberBlackList_and_DefaultScrubbedValueIsUsed_then_ItsValueIsScrubbedToxxxxx() throws Throwable {
        // mock behavior setup
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class, RETURNS_DEEP_STUBS);
        MethodSignature methodSignature = mockMethodSignature(
                "savePassword",
                Void.class,
                new String[]{"password"},
                new Class[]{String.class}
        );


        mockProceedingJoinPoint(
                proceedingJoinPoint,
                null,
                methodSignature,
                new DummyController(),
                new String[]{"my_secret_password"}
        );

        List<Object> mockedObjects = new ArrayList<>();
        mockedObjects.add(methodSignature);
        mockedObjects.add(proceedingJoinPoint);

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
                        "savePassword() called with arguments: password: [xxxxx] called via " +
                                "url: [https://www.example.com], username: [Jean-Luc Picard]")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "savePassword() took [0 ms] to complete")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "savePassword() returned: [void]")
        );

        assertEquals(expectedLogMessages, actualLogMessages);
        assertNull(actualReturnedValue);
        resetMock(mockedObjects);
    }

    @Test
    public void when_ArgumentNameIsInDataScrubberBlackList_and_ScrubbedValueIsOverriden_then_ItsValueIsScrubbedToOverridenValue() throws Throwable {
        // mock behavior setup
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class, RETURNS_DEEP_STUBS);
        MethodSignature methodSignature = mockMethodSignature(
                "savePassword",
                Void.class,
                new String[]{"password"},
                new Class[]{String.class}
        );


        mockProceedingJoinPoint(
                proceedingJoinPoint,
                null,
                methodSignature,
                new DummyController(),
                new String[]{"my_secret_password"}
        );

        List<Object> mockedObjects = new ArrayList<>();
        mockedObjects.add(methodSignature);
        mockedObjects.add(proceedingJoinPoint);

        RequestUtil mockedRequestUtil = MockUtils.mockRequestUtil();
        GenericControllerAspect aspect = new GenericControllerAspect(logger, new JsonUtil(), mockedRequestUtil);
        aspect.setDefaultScrubbedValue("#####");
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
                        "savePassword() called with arguments: password: [#####] called via " +
                                "url: [https://www.example.com], username: [Jean-Luc Picard]")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "savePassword() took [0 ms] to complete")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "savePassword() returned: [void]")
        );

        assertEquals(expectedLogMessages, actualLogMessages);
        assertNull(actualReturnedValue);
        resetMock(mockedObjects);
    }

    @Test
    public void when_ArgumentNameIsNotInDataScrubberBlackList_but_MatchesBlacklistRegex_then_ItsValueIsScrubbed() throws Throwable {
        // mock behavior setup
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class, RETURNS_DEEP_STUBS);
        MethodSignature methodSignature = mockMethodSignature(
                "saveSecretStuff",
                Void.class,
                new String[]{"secret1", "secret2", "noSecret"},
                new Class[]{String.class, String.class, String.class}
        );


        mockProceedingJoinPoint(
                proceedingJoinPoint,
                null,
                methodSignature,
                new DummyController(),
                new String[]{"foo", "bar", "baz"}
        );

        List<Object> mockedObjects = new ArrayList<>();
        mockedObjects.add(methodSignature);
        mockedObjects.add(proceedingJoinPoint);

        RequestUtil mockedRequestUtil = MockUtils.mockRequestUtil();
        GenericControllerAspect aspect = new GenericControllerAspect(logger, new JsonUtil(), mockedRequestUtil);
        aspect.setParamBlacklistRegex("secret.*");
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
                        "saveSecretStuff() called with arguments: secret1: [xxxxx], secret2: [xxxxx], noSecret: [baz] " +
                                "called via url: [https://www.example.com], username: [Jean-Luc Picard]")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "saveSecretStuff() took [0 ms] to complete")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "saveSecretStuff() returned: [void]")
        );

        assertEquals(expectedLogMessages, actualLogMessages);
        assertNull(actualReturnedValue);
        resetMock(mockedObjects);
    }

    @Test
    public void when_DataScrubbingIsOff_then_NoArgumentsAreScrubbed() throws Throwable {
        // mock behavior setup
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class, RETURNS_DEEP_STUBS);
        MethodSignature methodSignature = mockMethodSignature(
                "savePassword",
                Void.class,
                new String[]{"password"},
                new Class[]{String.class}
        );


        mockProceedingJoinPoint(
                proceedingJoinPoint,
                null,
                methodSignature,
                new DummyController(),
                new String[]{"my_secret_password"}
        );

        List<Object> mockedObjects = new ArrayList<>();
        mockedObjects.add(methodSignature);
        mockedObjects.add(proceedingJoinPoint);

        RequestUtil mockedRequestUtil = MockUtils.mockRequestUtil();
        GenericControllerAspect aspect = new GenericControllerAspect(logger, new JsonUtil(), mockedRequestUtil);
        aspect.setEnableDataScrubbing(false);
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
                        "savePassword() called with arguments: password: [my_secret_password] called via " +
                                "url: [https://www.example.com], username: [Jean-Luc Picard]")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "savePassword() took [0 ms] to complete")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "savePassword() returned: [void]")
        );

        assertEquals(expectedLogMessages, actualLogMessages);
        assertNull(actualReturnedValue);
        resetMock(mockedObjects);
    }

    @Test
    // purely in the interest of code coverage
    public void when_LoggerIsSet_then_ItIsSet() {
        GenericControllerAspect aspect = new GenericControllerAspect(logger, new JsonUtil(), new RequestUtil());

        Logger logger = mock(Logger.class);
        aspect.setLOG(logger);
    }

    @Test
    // purely in the interest of code coverage
    public void when_JsonUtilIsSet_then_ItIsSet() {
        GenericControllerAspect aspect = new GenericControllerAspect(logger, new JsonUtil(), new RequestUtil());

        JsonUtil jsonUtil = mock(JsonUtil.class);
        aspect.setJsonUtil(jsonUtil);
    }

    @Test
    // purely in the interest of code coverage
    public void when_RequestUtilIsSet_then_ItIsSet() {
        GenericControllerAspect aspect = new GenericControllerAspect(logger, new JsonUtil(), new RequestUtil());

        RequestUtil requestUtil = mock(RequestUtil.class);
        aspect.setRequestUtil(requestUtil);
    }

    @Test
    // purely in the interest of code coverage
    public void testDefaultConstructor() {new GenericControllerAspect();
    }

    @Test
    public void when_MethodProducesNonJsonResponseButClassSpecifiesJsonResponse_then_ResponseIsSerialized() throws Throwable {
        // mock behavior setup
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class, RETURNS_DEEP_STUBS);
        MethodSignature methodSignature = mockMethodSignature(
                "getNote",
                String.class,
                new String[]{"noteId"},
                new Class[]{int.class}
        );
        mockProceedingJoinPoint(
                proceedingJoinPoint,
                "Hello, World!",
                methodSignature,
                new DummyController(),
                new Object[]{1}
        );

        List<Object> mockedObjects = new ArrayList<>();
        mockedObjects.add(methodSignature);
        mockedObjects.add(proceedingJoinPoint);

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
                        "getNote() called with arguments: noteId: [1] called via " +
                                "url: [https://www.example.com], username: [Jean-Luc Picard]")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "getNote() took [0 ms] to complete")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "getNote() returned: [\"Hello, World!\"]")
        );

        assertEquals(expectedLogMessages, actualLogMessages);

        String expectedReturnedValue = "Hello, World!";
        assertEquals(expectedReturnedValue, actualReturnedValue);
        resetMock(mockedObjects);
    }

    @Test
    public void when_MethodProducesJsonResponse_then_ResponseIsSerializedWithoutCheckingClasssProduces() throws Throwable {
        // mock behavior setup
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class, RETURNS_DEEP_STUBS);
        MethodSignature methodSignature = mockMethodSignature(
                "getNote",
                String.class,
                new String[]{"substring"},
                new Class[]{String.class}
        );
        mockProceedingJoinPoint(
                proceedingJoinPoint,
                "Hello, World!",
                methodSignature,
                new DummyController(),
                new Object[]{"hello"}
        );

        List<Object> mockedObjects = new ArrayList<>();
        mockedObjects.add(methodSignature);
        mockedObjects.add(proceedingJoinPoint);

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
                        "getNote() called with arguments: substring: [hello] called via " +
                                "url: [https://www.example.com], username: [Jean-Luc Picard]")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "getNote() took [0 ms] to complete")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level",
                        "INFO",
                        "message",
                        "getNote() returned: [\"Hello, World!\"]")
        );

        assertEquals(expectedLogMessages, actualLogMessages);

        String expectedReturnedValue = "Hello, World!";
        assertEquals(expectedReturnedValue, actualReturnedValue);
        resetMock(mockedObjects);
    }

    private void resetMock(List<Object> mockedObjects) {
        mockedObjects.forEach(Mockito::reset);
    }

}