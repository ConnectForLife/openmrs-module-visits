package org.openmrs.module.visits.util;

import org.openmrs.module.emrapi.visit.VisitDomainWrapper;

import java.util.Comparator;

/**
 * Util class supporting with the visit domain comparators
 */
public final class ComparatorsHelper {

    /**
     * Method generating a visit domain wrapper comparator which compares by start date ascending
     *
     * @return an ascending start date comparator
     */
    public static Comparator<VisitDomainWrapper> getVisitsComparatorByStartDateAsc() {
        return new Comparator<VisitDomainWrapper>() {
            @Override
            public int compare(VisitDomainWrapper v1, VisitDomainWrapper v2) {
                return v1.getStartDate().compareTo(v2.getStartDate());
            }
        };
    }

    /**
     * Method generating a visit domain wrapper comparator which compares by start date descending
     *
     * @return a descending start date comparator
     */
    public static Comparator<VisitDomainWrapper> getVisitsComparatorByStartDateDesc() {
        return new Comparator<VisitDomainWrapper>() {
            @Override
            public int compare(VisitDomainWrapper v1, VisitDomainWrapper v2) {
                return (-1) * v1.getStartDate().compareTo(v2.getStartDate());
            }
        };
    }

    private ComparatorsHelper() { }
}
