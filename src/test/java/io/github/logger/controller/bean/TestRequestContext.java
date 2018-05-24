package io.github.logger.controller.bean;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TestRequestContext {

    @Test
    public void when_RequestContextCreatedWithDefaultConstructor_then_ContextIsEmpty() {
        RequestContext context = new RequestContext();
        assertEquals("", context.toString());
    }

    @Test
    public void when_ElementsAddedToRequestContext_then_TheyAreReturnedAsFormattedStringInToString() {
        RequestContext context = new RequestContext()
                .add("key1", "value1")
                .add("key2", "value2");

        String expectedResult = "key1: [value1], key2: [value2]";
        assertEquals(expectedResult, context.toString());
    }

    @Test
    public void when_NullValueIsAddedToContext_then_ItIsStoredAsStringContainingNull() {
        RequestContext context = new RequestContext()
                .add("key1", null);

        String expectedResult = "key1: [null]";
        assertEquals(expectedResult, context.toString());
    }

    @Test
    public void when_RequestContextInitializedFromMap_then_ItsValuesAreSetInContext() {
        Map<String, String> contextData = new HashMap<>();
        contextData.put("key1", "value1");
        contextData.put("key2", "value2");

        RequestContext context = new RequestContext(contextData);

        String expectedResult = "key1: [value1], key2: [value2]";
        assertEquals(expectedResult, context.toString());
    }

    @Test
    public void when_RequestContextInitializedFromEmptyMap_then_ContextIsEmpty() {
        Map<String, String> contextData = new HashMap<>();
        RequestContext context = new RequestContext(contextData);

        String expectedResult = "";
        assertEquals(expectedResult, context.toString());
    }

    @Test
    public void when_TwoRequestContextHaveSameKeys_then_TheyAreEqual() {
        Map<String, String> contextData = new HashMap<>();
        contextData.put("key1", "value1");
        contextData.put("key2", "value2");

        RequestContext context1 = new RequestContext(contextData);

        RequestContext context2 = new RequestContext()
                .add("key1", "value1")
                .add("key2", "value2");

        assertEquals(context1, context2);
    }

    @Test
    public void when_TwoRequestContextDoNotHaveSameKeys_then_TheyAreEqual() {
        Map<String, String> contextData = new HashMap<>();
        contextData.put("key1", "value1");

        RequestContext context1 = new RequestContext(contextData);

        RequestContext context2 = new RequestContext()
                .add("key1", "value1")
                .add("key2", "value2");

        assertFalse(context1.equals(context2));
    }

    @Test
    public void hashCodeWithEmptyContext() {
        RequestContext context1 = new RequestContext()
                .add("key1", "value1")
                .add("key2", "value2");

        assertTrue(context1.hashCode() != 0);
    }

}
