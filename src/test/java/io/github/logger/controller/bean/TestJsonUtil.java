package io.github.logger.controller.bean;

import bean.User;
import io.github.logger.controller.utils.JsonUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestJsonUtil {

    @Test
    public void compareSerializzedDeserializedObject() {
        JsonUtil jsonUtil = new JsonUtil();

        User user = new User(1, "foobar@example.com", "password");
        String serializedUser = jsonUtil.toJson(user);
        User deserialziedUser = jsonUtil.fromJson(serializedUser, User.class);

        assertEquals(user, deserialziedUser);
    }

}
