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
      return Comparator.comparing(VisitDomainWrapper::getStartDate);
    }

    /**
     * Method generating a visit domain wrapper comparator which compares by start date descending
     *
     * @return a descending start date comparator
     */
    public static Comparator<VisitDomainWrapper> getVisitsComparatorByStartDateDesc() {
       return (v1, v2) -> (-1) * v1.getStartDate().compareTo(v2.getStartDate());
    }

    private ComparatorsHelper() { }
}
