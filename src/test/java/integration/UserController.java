package integration;

import bean.User;
import io.github.logger.controller.annotation.Logging;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;

@Controller
@Logging
public class UserController {

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/get-random-user",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public User getUser() {
        return random(User.class);
    }
}
