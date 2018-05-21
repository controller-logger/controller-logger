package helpers;

import bean.User;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DummyControllerWithoutClassMapping {

    @RequestMapping(value = "/getUser", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public User getUser(@Nonnull int userId) {
        return new User(1, "foo@example.com", "password");
    }

    public String nonRestApiMethodWithArgs(@Nonnull String arg) {
        return arg;
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/getUser",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public User createUser(@Nullable @RequestBody User user, @Nullable @RequestParam String source) {
        return user;
    }

    @RequestMapping(value = "/saveNote", consumes = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public boolean saveNote(@RequestBody String text) {
        return true;
    }

    @RequestMapping(value = "/saveMemo", consumes = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public void saveMemo(@RequestBody String text) {
        // no-op
    }

    @RequestMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadFile(@RequestBody ByteArrayResource file) {
        //no-op
    }

    @RequestMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadFile(@RequestBody MultipartFile file) {
        //no-op
    }

    @RequestMapping(value = "/getFile", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ByteArrayResource getFileByteArrayResource() {
        return null; // no-op
    }

    @RequestMapping(value = "/getFile", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public MultipartFile getFileMultipartFile() {
        return null; // no-op
    }

    @RequestMapping(value = "/savePassword")
    public void savePassword(@RequestParam String password) {
        // no-op
    }

    @RequestMapping(value = "/saveBunchOfSecretStuff")
    public void saveSecretStuff(@RequestParam String secret1, @RequestParam String secret2, @RequestParam String noSecret) {
        // no-op
    }

    @RequestMapping(value = "/getNote", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getNote(@RequestParam int noteId) {
        return "Hello, World!";
    }

    @RequestMapping(value = "/searchNote", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getNote(@RequestParam String substring) {
        return "Hello, World!";
    }
}
