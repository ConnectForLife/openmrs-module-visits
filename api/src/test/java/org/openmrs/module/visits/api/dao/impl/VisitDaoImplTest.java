package org.openmrs.module.visits.api.dao.impl;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Visit;
import org.openmrs.api.LocationService;
import org.openmrs.module.visits.api.dao.VisitDao;
import org.openmrs.module.visits.builder.VisitCriteriaBuilder;
import org.openmrs.module.visits.domain.PagingInfo;
import org.openmrs.module.visits.domain.criteria.OverviewCriteria;
import org.openmrs.module.visits.domain.criteria.VisitCriteria;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class VisitDaoImplTest extends BaseModuleContextSensitiveTest {

    private static final String XML_DATA_SET_PATH = "datasets/";
    public static final int PAGE = 1;
    public static final int PAGE_SIZE = 2;
    public static final int EXPECTED_TWO = 2;
    public static final int EXPECTED_ONE = 1;

    private Location location;

    @Autowired
    @Qualifier("visits.visitDao")
    private VisitDao visitDao;

    @Autowired
    private LocationService locationService;

    @Before
    public void setUp() throws Exception {
        executeDataSet(XML_DATA_SET_PATH + "VisitDataSet.xml");
        location = locationService.getLocation(1);
    }

    @Test
    public void findByCriteria() {
        PagingInfo pagingInfo = new PagingInfo(PAGE, PAGE_SIZE);
        VisitCriteria criteria = new VisitCriteriaBuilder().build();
        List<Visit> actual = visitDao.findAllByCriteria(criteria, pagingInfo);
        assertThat(actual.size(), is(EXPECTED_TWO));
    }

    @Test
    public void findByOverviewCriteria() {
        PagingInfo pagingInfo = new PagingInfo(PAGE, PAGE_SIZE);
        OverviewCriteria criteria = new OverviewCriteria(location, "ic", null, null,
                null, null);
        List<Visit> actual = visitDao.findAllByCriteria(criteria, pagingInfo);
        assertThat(actual.size(), is(EXPECTED_ONE));
    }
}
