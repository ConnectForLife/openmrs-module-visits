package org.openmrs.module.visits.api.model;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ErrorMessageEnumTest {

    @Test
    public void shouldReturnBadParamMessageEnum() {
        ErrorMessageEnum messageEnum = ErrorMessageEnum.ERR_BAD_PARAM;
        assertNotNull(messageEnum.getCode());

    }

    @Test
    public void shouldReturnSystemMessageEnum() {
        ErrorMessageEnum messageEnum = ErrorMessageEnum.ERR_SYSTEM;
        assertNotNull(messageEnum.getCode());
    }
}
