package org.openmrs.module.visits.api.model;

import org.junit.Test;
import org.openmrs.module.visits.ContextMockedTest;

import static org.junit.Assert.assertEquals;

public class ErrorMessageTest extends ContextMockedTest {

    private static final String SAMPLE_TEXT = "Some text";

    @Test
    public void shouldSetCodeProperly() {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setCode(SAMPLE_TEXT);
        assertEquals(SAMPLE_TEXT, errorMessage.getCode());
    }

    @Test
    public void shouldSetMessageProperly() {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setMessage(SAMPLE_TEXT);
        assertEquals(SAMPLE_TEXT, errorMessage.getMessage());
    }
}
