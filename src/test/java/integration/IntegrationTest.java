package integration;

import bean.User;
import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import io.github.logger.controller.aspect.GenericControllerAspect;
import io.github.logger.controller.utils.JsonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = {ApiSecurityConfig.class, UserController.class, BeanConfig.class, ControllerLoggerConsumerApplication.class})
@AutoConfigureMockMvc
public class IntegrationTest {

    @Autowired
    private MockMvc mvc;

    public static TestLogger logger = TestLoggerFactory.getTestLogger(IntegrationTest.class);

    @Test
    public void givenEmployees_whenGetEmployees_thenStatus200() throws Exception {
        MvcResult result =  mvc.perform(
                get("/get-random-user")
                .header("Authorization", "Basic dXNlcm5hbWU6cGFzc3dvcmQ=")
                .header("Accept", "application/json")
        ).andReturn();

        User user = new JsonUtil().fromJson(result.getResponse().getContentAsString(), User.class);
        System.out.println(user);

        logger.getAllLoggingEvents().forEach(x -> System.out.println(x.getMessage()));
    }

}
