package org.openmrs.module.visits.web;

import org.junit.After;
import org.junit.Before;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.visits.VisitsActivator;
import org.openmrs.module.visits.api.util.ConfigConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class BaseModuleWebContextSensitiveWithActivatorTest extends BaseModuleWebContextSensitiveTest {

    private VisitsActivator activator;

    @Before
    public void setUpWithActivator() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        DaemonToken daemonToken = getDaemonToken();
        activator = new VisitsActivator();
        activator.setDaemonToken(daemonToken);
        activator.started();
    }

    @After
    public void stopActivator() {
        activator.stopped();
    }

    private DaemonToken getDaemonToken() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Module module = new Module(ConfigConstants.MODULE_ID);
        module.setModuleId(ConfigConstants.MODULE_ID);
        Method method = ModuleFactory.class.getDeclaredMethod("getDaemonToken", Module.class);
        method.setAccessible(true);
        return (DaemonToken) method.invoke(null, module);
    }
}
