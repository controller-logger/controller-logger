package io.github.logger.controller.aspect.integration.spring_boot_application;

import bean.User;
import io.github.logger.controller.annotation.Logging;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static io.github.benas.randombeans.api.EnhancedRandom.random;

@Controller
@Logging
public class UserController {

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/getUser",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public User getUser() {
        return new User(1, "foobar@example.com", "secretpassword");
    }
}
