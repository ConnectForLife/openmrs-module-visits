package org.openmrs.module.visits.domain.criteria;

import org.hibernate.Criteria;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.CriteriaImpl.CriterionEntry;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Location;
import org.openmrs.module.visits.builder.LocationBuilder;

import java.lang.reflect.Field;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class OverviewCriteriaTest {

    private static final String SAMPLE_QUERY = "SELECT 1";
    private Location location = new LocationBuilder().build();

    @Mock
    private SessionImplementor session;

    @Test
    public void shouldLoadHibernateCriteriaWithLocation() throws NoSuchFieldException, IllegalAccessException {
        OverviewCriteria overviewCriteria = new OverviewCriteria(location, SAMPLE_QUERY, null, null,
                null, null);
        Criteria criteria = new CriteriaImpl("Overview", session);
        overviewCriteria.loadHibernateCriteria(criteria);

        assertThat(getCriterions(criteria), hasSize(2));
    }

    @Test
    public void shouldBuildCriteriaWithLocationUuid() {
        OverviewCriteria overviewCriteria = OverviewCriteria.forLocationUuid(location.getUuid());
        assertEquals(location.getUuid(), overviewCriteria.getLocationUuid());
    }

    private List<CriterionEntry> getCriterions(Criteria criteria) throws NoSuchFieldException, IllegalAccessException {
        Field privateStringField = CriteriaImpl.class.
                getDeclaredField("criterionEntries");
        privateStringField.setAccessible(true);

        return (List<CriterionEntry>) privateStringField.get(criteria);
    }
}
