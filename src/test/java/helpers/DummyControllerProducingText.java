package helpers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(produces = MediaType.TEXT_PLAIN_VALUE)
public class DummyControllerProducingText {

    @RequestMapping(value = "/getNote")
    public String getNote(@RequestParam int noteId) {
        return "Hello, World!";
    }

}
