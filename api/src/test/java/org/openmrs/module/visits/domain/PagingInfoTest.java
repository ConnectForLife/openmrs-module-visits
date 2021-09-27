package org.openmrs.module.visits.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PagingInfoTest {

    public static final int PAGE = 1;
    public static final int PAGE_SIZE = 2;
    public static final Long TOTAL_RECORD_COUNT = 10L;
    public static final boolean LOAD_RECORD_COUNT = false;

    @Test
    public void shouldConstructProperObject() {
        PagingInfo pagingInfo = new PagingInfo(PAGE, PAGE_SIZE);
        assertEquals(PAGE, pagingInfo.getPage());
        assertEquals(PAGE_SIZE, pagingInfo.getPageSize());
    }

    @Test
    public void shouldSetParametersProperly() {
        PagingInfo pagingInfo = initPageInfo();

        assertEquals(PAGE, pagingInfo.getPage());
        assertEquals(PAGE_SIZE, pagingInfo.getPageSize());
        assertEquals(LOAD_RECORD_COUNT, pagingInfo.shouldLoadRecordCount());
        assertEquals(TOTAL_RECORD_COUNT, pagingInfo.getTotalRecordCount());
        assertEquals(true, pagingInfo.hasMoreResults());
    }

    @Test
    public void shouldReturnFalseForMoreResultsWhenRecordCountNull() {
        PagingInfo pagingInfo = new PagingInfo();
        pagingInfo.setTotalRecordCount(null);

        assertEquals(false, pagingInfo.hasMoreResults());
    }

    @Test
    public void shouldCompareProperly() {
        PagingInfo pagingInfo = initPageInfo();
        PagingInfo pagingInfo2 = initPageInfo();

        assertTrue(pagingInfo.equals(pagingInfo));
        assertTrue(pagingInfo.equals(pagingInfo2));
        assertFalse(pagingInfo.equals(new Object()));
    }

    private PagingInfo initPageInfo() {
        PagingInfo pagingInfo = new PagingInfo();
        pagingInfo.setPage(PAGE);
        pagingInfo.setPageSize(PAGE_SIZE);
        pagingInfo.setLoadRecordCount(LOAD_RECORD_COUNT);
        pagingInfo.setTotalRecordCount(TOTAL_RECORD_COUNT);
        return pagingInfo;
    }
}
