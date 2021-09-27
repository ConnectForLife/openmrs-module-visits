package org.openmrs.module.visits.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.visits.util.VisitsUiConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebAppConfiguration
public class NotificationControllerTest extends BaseModuleWebContextSensitiveTest {

    private static final String VISITS_NOTIFICATIONS_URL = "/visits/notifications";
    private static final String SUCCESS_MESSAGE = "Success message";
    private static final String ERROR_MESSAGE = "Error message";
    private static final String NOT_IMPORTANT_MESSAGE = "Not important message";
    private static final String RAW_SCRIPT_TAG = "<script>eatCookie();</script>";
    private static final String ESCAPED_SCRIPT_TAG = "&lt;script&gt;eatCookie();&lt;/script&gt;";
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldSuccessfullyFetchSuccessNotification() throws Exception {
        HashMap<String, Object> sessionAttributes = buildSuccessMessageAttributes();
        mockMvc.perform(MockMvcRequestBuilders.get(VISITS_NOTIFICATIONS_URL)
                .sessionAttrs(sessionAttributes))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].errorMessage").value(false))
                .andExpect(jsonPath("$.[*].message").value(SUCCESS_MESSAGE));
    }

    @Test
    public void shouldSuccessfullyFetchErrorNotificationWhenMissingSuccessMessage() throws Exception {
        HashMap<String, Object> sessionAttributes = buildErrorMessageAttributes(false);
        mockMvc.perform(MockMvcRequestBuilders.get(VISITS_NOTIFICATIONS_URL)
                .sessionAttrs(sessionAttributes))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].errorMessage").value(true))
                .andExpect(jsonPath("$.[*].message").value(ERROR_MESSAGE));
    }

    @Test
    public void shouldSuccessfullyFetchErrorNotificationWhenExistNotImportantSuccessMessage() throws Exception {
        HashMap<String, Object> sessionAttributes = buildErrorMessageAttributes(true);
        mockMvc.perform(MockMvcRequestBuilders.get(VISITS_NOTIFICATIONS_URL)
                .sessionAttrs(sessionAttributes))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].errorMessage").value(true))
                .andExpect(jsonPath("$.[*].message").value(ERROR_MESSAGE));
    }

    @Test
    public void shouldReturnEmptyListWhenMissingSessionAttribute() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(VISITS_NOTIFICATIONS_URL))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    public void shouldReturnEmptyListWhenMissingMainAttribute() throws Exception {
        HashMap<String, Object> sessionAttributes = buildAttributesWithoutToastAttr();
        mockMvc.perform(MockMvcRequestBuilders.get(VISITS_NOTIFICATIONS_URL)
                .sessionAttrs(sessionAttributes))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    public void shouldReturnEmptyListWhenToastAttributeIsFalse() throws Exception {
        HashMap<String, Object> sessionAttributes = buildAttributesWithFalseToastAttr();
        mockMvc.perform(MockMvcRequestBuilders.get(VISITS_NOTIFICATIONS_URL)
                .sessionAttrs(sessionAttributes))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    public void shouldReturnSuccessNotificationWhenEmptyToastAttr() throws Exception {
        HashMap<String, Object> sessionAttributes = buildAttributesWithEmptyToastAttr();
        mockMvc.perform(MockMvcRequestBuilders.get(VISITS_NOTIFICATIONS_URL)
                .sessionAttrs(sessionAttributes))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    public void shouldClearSessionAfterFetchingNotification() throws Exception {
        MockHttpSession session = buildSuccessMessageSession();
        mockMvc.perform(MockMvcRequestBuilders.get(VISITS_NOTIFICATIONS_URL)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].errorMessage").value(false))
                .andExpect(jsonPath("$.[*].message").value(SUCCESS_MESSAGE));

        mockMvc.perform(MockMvcRequestBuilders.get(VISITS_NOTIFICATIONS_URL)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    public void shouldEscapeHtmlCharactersInSuccess() throws Exception {
        final MockHttpSession session = buildMessageSessionWithXSSInSuccess();
        mockMvc.perform(MockMvcRequestBuilders.get(VISITS_NOTIFICATIONS_URL)
                .session(session))
                .andExpect(jsonPath("$.[*].message").value(ESCAPED_SCRIPT_TAG))
                .andExpect(jsonPath("$.[*].errorMessage").value(false));
    }

    @Test
    public void shouldEscapeHtmlCharactersInError() throws Exception {
        final MockHttpSession session = buildMessageSessionWithXSSInError();
        mockMvc.perform(MockMvcRequestBuilders.get(VISITS_NOTIFICATIONS_URL)
                .session(session))
                .andExpect(jsonPath("$.[*].message").value(ESCAPED_SCRIPT_TAG))
                .andExpect(jsonPath("$.[*].errorMessage").value(true));
    }

    private HashMap<String, Object> buildSuccessMessageAttributes() {
        HashMap<String, Object> sessionAttributes = new HashMap<String, Object>();
        sessionAttributes.put(VisitsUiConstants.EMR_TOAST_MESSAGE, "true");
        sessionAttributes.put(VisitsUiConstants.EMR_INFO_MESSAGE, SUCCESS_MESSAGE);
        return sessionAttributes;
    }

    private HashMap<String, Object> buildErrorMessageAttributes(boolean withNotImportantSuccessMessage) {
        HashMap<String, Object> sessionAttributes = new HashMap<String, Object>();
        sessionAttributes.put(VisitsUiConstants.EMR_TOAST_MESSAGE, "true");
        sessionAttributes.put(VisitsUiConstants.EMR_ERROR_MESSAGE, ERROR_MESSAGE);
        if (withNotImportantSuccessMessage) {
            sessionAttributes.put(VisitsUiConstants.EMR_INFO_MESSAGE, NOT_IMPORTANT_MESSAGE);
        }
        return sessionAttributes;
    }

    private HashMap<String, Object> buildAttributesWithoutToastAttr() {
        HashMap<String, Object> sessionAttributes = new HashMap<String, Object>();
        sessionAttributes.put(VisitsUiConstants.EMR_ERROR_MESSAGE, ERROR_MESSAGE);
        sessionAttributes.put(VisitsUiConstants.EMR_INFO_MESSAGE, NOT_IMPORTANT_MESSAGE);
        return sessionAttributes;
    }

    private HashMap<String, Object> buildAttributesWithEmptyToastAttr() {
        HashMap<String, Object> sessionAttributes = new HashMap<String, Object>();
        sessionAttributes.put(VisitsUiConstants.EMR_TOAST_MESSAGE, StringUtils.EMPTY);
        sessionAttributes.put(VisitsUiConstants.EMR_ERROR_MESSAGE, ERROR_MESSAGE);
        sessionAttributes.put(VisitsUiConstants.EMR_INFO_MESSAGE, NOT_IMPORTANT_MESSAGE);
        return sessionAttributes;
    }

    private HashMap<String, Object> buildAttributesWithFalseToastAttr() {
        HashMap<String, Object> sessionAttributes = new HashMap<String, Object>();
        sessionAttributes.put(VisitsUiConstants.EMR_TOAST_MESSAGE, "false");
        sessionAttributes.put(VisitsUiConstants.EMR_ERROR_MESSAGE, ERROR_MESSAGE);
        sessionAttributes.put(VisitsUiConstants.EMR_INFO_MESSAGE, NOT_IMPORTANT_MESSAGE);
        return sessionAttributes;
    }

    private MockHttpSession buildSuccessMessageSession() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(VisitsUiConstants.EMR_TOAST_MESSAGE, "true");
        session.setAttribute(VisitsUiConstants.EMR_INFO_MESSAGE, SUCCESS_MESSAGE);
        return session;
    }

    private MockHttpSession buildMessageSessionWithXSSInSuccess() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(VisitsUiConstants.EMR_TOAST_MESSAGE, "true");
        session.setAttribute(VisitsUiConstants.EMR_INFO_MESSAGE, RAW_SCRIPT_TAG);
        return session;
    }

    private MockHttpSession buildMessageSessionWithXSSInError() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(VisitsUiConstants.EMR_TOAST_MESSAGE, "true");
        session.setAttribute(VisitsUiConstants.EMR_ERROR_MESSAGE, RAW_SCRIPT_TAG);
        return session;
    }
}
