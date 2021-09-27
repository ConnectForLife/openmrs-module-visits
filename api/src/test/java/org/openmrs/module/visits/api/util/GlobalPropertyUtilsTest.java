package org.openmrs.module.visits.api.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Spy;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.AdministrationServiceImpl;
import org.openmrs.module.visits.ContextMockedTest;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

public class GlobalPropertyUtilsTest extends ContextMockedTest {

    private static final String SOME_KEY = "key";
    private static final String SOME_VALUE = "5";
    private static final String SOME_DESC = "description";

    @Spy
    private AdministrationService administrationService = new AdministrationServiceImpl();

    @Before
    public void setUp() {
        mockStatic(Context.class);
        when(Context.getAdministrationService()).thenReturn(administrationService);
    }

    @Test
    public void shouldReturnGlobalProperty() {
        doReturn(SOME_VALUE).when(administrationService).getGlobalProperty(SOME_KEY);
        String globalProperty = GlobalPropertyUtils.getGlobalProperty(SOME_KEY);
        assertEquals(SOME_VALUE, globalProperty);
    }

    @Test
    public void shouldCreateGlobalSettingsIfNotExists() {
        doReturn(null).when(administrationService).getGlobalProperty(SOME_KEY);
        doReturn(new GlobalProperty()).when(administrationService).saveGlobalProperty(any(GlobalProperty.class));

        GlobalPropertyUtils.createGlobalSettingsIfNotExists(Arrays.asList(
                new GPDefinition(SOME_KEY, SOME_VALUE, SOME_DESC),
                new GPDefinition(SOME_KEY, SOME_VALUE, SOME_DESC)
        ));

        verify(administrationService, times(2)).getGlobalProperty(SOME_KEY);
        verify(administrationService, times(2)).saveGlobalProperty(any(GlobalProperty.class));
    }

    @Test
    public void shouldCreateGlobalSettingIfNotExists() {
        doReturn(null).when(administrationService).getGlobalProperty(SOME_KEY);
        doReturn(new GlobalProperty()).when(administrationService).saveGlobalProperty(any(GlobalProperty.class));

        GlobalPropertyUtils.createGlobalSettingIfNotExists(new GPDefinition(SOME_KEY, SOME_VALUE, SOME_DESC));

        verify(administrationService, times(1)).getGlobalProperty(SOME_KEY);
        verify(administrationService, times(1)).saveGlobalProperty(any(GlobalProperty.class));
    }

    @Test
    public void shouldReturnParsedInteger() {
        doReturn(SOME_VALUE).when(administrationService).getGlobalProperty(SOME_KEY);
        assertEquals(Integer.valueOf(SOME_VALUE), GlobalPropertyUtils.getInteger(SOME_KEY));

    }
}
