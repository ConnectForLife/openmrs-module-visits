package org.openmrs.module.visits.api.util;

import org.junit.Test;
import org.openmrs.module.visits.BaseTest;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class TemplateUtilsTest extends BaseTest {
    private static final String VALUE = "123";

    @Test
    public void shouldReplaceParam() {
        String actual = TemplateUtils.fillTemplate("-{{param}}-", "param", VALUE);
        assertEquals("-123-", actual);
    }

    @Test
    public void shouldReplaceMultipleOccurrencesOfTheSameParam() {
        String actual = TemplateUtils.fillTemplate("-{{param}}-{{param}}-", "param", VALUE);
        assertEquals("-123-123-", actual);
    }

    @Test
    public void shouldNotThrowExceptionIfParamNotExistsInTemplate() {
        String actual = TemplateUtils.fillTemplate("--", "param", VALUE);
        assertEquals("--", actual);
    }

    @Test
    public void shouldReplaceMultipleParams() {
        HashMap<String, String> params = new HashMap<>();
        params.put("param1", "ABC");
        params.put("param2", "CBA");

        String actual = TemplateUtils.fillTemplate("-{{param1}}-{{param2}}-", params);
        assertEquals("-ABC-CBA-", actual);
    }
}
