import bean.User;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Nonnull;

@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
public class LOL {

    @RequestMapping(value = "/getUser")
    @ResponseBody
    public User getUser(@Nonnull int userId) {
        return null;
    }

}
