package io.github.logger.controller.aspect.integration;

import bean.User;
import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import com.google.common.collect.ImmutableMap;
import helpers.Utils;
import io.github.logger.controller.utils.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
public class IntegrationTest extends BaseIntegrationTest {

    public static TestLogger logger = TestLoggerFactory.getTestLogger(IntegrationTest.class);
    @Autowired
    private MockMvc mvc;

    @Before
    public void before() {
        logger.clearAll();
    }

    @Test
    public void givenEmployees_whenGetEmployees_thenStatus200() throws Exception {
        MvcResult result = mvc.perform(
                get("/getUser")
                        .header("Authorization", Utils.generateBasicAuthToken("username", "password"))
                        .header("Accept", "application/json")
        ).andReturn();

        User actualUser = new JsonUtil().fromJson(result.getResponse().getContentAsString(), User.class);

        List<Map<String, String>> expectedLogMessages = new ArrayList<>();
        expectedLogMessages.add(
                ImmutableMap.of(
                        "level", "INFO",
                        "message", "getUser() called via url: [http://localhost/getUser], username: [username]")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level", "INFO",
                        "message", "getUser\\(\\) took \\[\\d+ ms\\] to complete",
                        "type", "regex")
        );

        expectedLogMessages.add(
                ImmutableMap.of(
                        "level", "INFO",
                        "message", "getUser() returned: [{\"id\":1,\"email\":\"foobar@example.com\",\"password\":\"secretpassword\"}]")
        );

        List<Map<String, String>> actualLogMessages = Utils.getFormattedLogEvents(logger);

        validateLogs(expectedLogMessages, actualLogMessages);

        User expectedUser = new User(1, "foobar@example.com", "secretpassword");
        assertEquals(expectedUser, actualUser);
    }

    private void validateLogs(List<Map<String, String>> expectedLogMessages, List<Map<String, String>> actualLogMessages) {
        for (int i = 0; i < expectedLogMessages.size(); ++i) {
            assertEquals(expectedLogMessages.get(i).get("level"), actualLogMessages.get(i).get("level"));

            String messageType = expectedLogMessages.get(i).get("type");

            if (messageType != null && messageType.equals("regex")) {
                String expectedPattern = expectedLogMessages.get(i).get("message");
                String actualLogMessage = actualLogMessages.get(i).get("message");

                assertTrue(Pattern.compile(expectedPattern).matcher(actualLogMessage).matches());
            } else {
                assertEquals(expectedLogMessages.get(i).get("message"), actualLogMessages.get(i).get("message"));
            }
        }
    }

}
