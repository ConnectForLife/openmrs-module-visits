package org.openmrs.module.visits.domain.criteria;

import org.hibernate.Criteria;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.CriteriaImpl;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.module.visits.builder.PatientBuilder;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class VisitCriteriaTest {
    private static final int MIN_DELAY_IN_DAYS = 1;
    private static final String ENDING_STATUS = "OCCURRED";
    private static final String MISSED_STATUS = "MISSED";

    private Patient patient = new PatientBuilder().build();

    @Mock
    private SessionImplementor session;

    @Test
    public void shouldLoadHibernateCriteriaWithLocation() throws NoSuchFieldException, IllegalAccessException {
        VisitCriteria visitCriteria = new VisitCriteria(patient);
        visitCriteria.setSortingByField("someField", false);
        Criteria criteria = new CriteriaImpl("Visit", session);
        visitCriteria.loadHibernateCriteria(criteria);

        assertThat(getCriterions(criteria), hasSize(1));
    }

    @Test
    public void shouldInitHibernateCriteria() throws NoSuchFieldException, IllegalAccessException {
        VisitCriteria visitCriteria = new VisitCriteria(patient);
        Criteria criteria = new CriteriaImpl("Visit", session);
        visitCriteria.initHibernateCriteria(criteria);

        assertThat(getCriterions(criteria), hasSize(1));
    }

    @Test
    public void shouldBuildCriteriaForPatientUuid() {
        VisitCriteria visitCriteria = VisitCriteria.forPatientUuid(patient.getUuid());
        assertEquals(patient.getUuid(), visitCriteria.getPatientUuid());
    }

    @Test
    public void shouldBuildCriteriaForMissedVisits() throws NoSuchFieldException, IllegalAccessException {
        VisitCriteria visitCriteria = VisitCriteria.forMissedVisits(
                MIN_DELAY_IN_DAYS,
                MISSED_STATUS,
                Collections.singletonList(ENDING_STATUS)
        );
        Criteria criteria = new CriteriaImpl("Visit", session);
        visitCriteria.loadHibernateCriteria(criteria);

        assertThat(getCriterions(criteria), hasSize(2));

    }

    private List<CriteriaImpl.CriterionEntry> getCriterions(Criteria criteria)
            throws NoSuchFieldException, IllegalAccessException {
        Field privateStringField = CriteriaImpl.class.
                getDeclaredField("criterionEntries");
        privateStringField.setAccessible(true);

        return (List<CriteriaImpl.CriterionEntry>) privateStringField.get(criteria);
    }
}
