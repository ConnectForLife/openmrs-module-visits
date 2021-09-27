package org.openmrs.module.visits.api.util;

import org.junit.Test;
import org.openmrs.module.visits.BaseTest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class UriUtilsTest extends BaseTest {

    @Test
    public void shouldReturnTrueIfUriIsValid() {
        String uri = "/patient/123";
        boolean actual = UriUtils.isUriValid(uri);
        assertTrue(actual);
    }

    @Test
    public void shouldReturnFalseIfUriContainsDoubleSlashes() {
        String uri = "//patient/123";
        boolean actual = UriUtils.isUriValid(uri);
        assertFalse(actual);
    }

    @Test
    public void shouldReturnFalseIfUriHasSpaces() {
        String uri = "/patient /123";
        boolean actual = UriUtils.isUriValid(uri);
        assertFalse(actual);
    }

    @Test
    public void shouldReturnFalseIfUriHasNotReplacedParam() {
        String uri = "/patient/{{param}}";
        boolean actual = UriUtils.isUriValid(uri);
        assertFalse(actual);
    }
}
