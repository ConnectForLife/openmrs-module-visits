/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.domain.criteria;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonName;
import org.openmrs.VisitAttribute;
import org.openmrs.module.visits.api.model.TimePeriod;
import org.openmrs.module.visits.api.service.VisitSimpleQuery;
import org.openmrs.module.visits.api.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OverviewCriteria extends BaseCriteria {

  private static final long serialVersionUID = 8L;

  private static final String PATH_SEPARATOR = ".";
  private static final String PATIENT_PATH = "patient";
  private static final String PERSON_PATH = "person";
  private static final String PATIENT_ALIAS = "patient";
  private static final String NAMES_ALIAS = "names";
  private static final String NAMES_PATH = NAMES_ALIAS + PATH_SEPARATOR + PERSON_PATH;
  private static final String IDENTIFIERS_ALIAS = "identifiers";
  private static final String IDENTIFIERS_PATH = IDENTIFIERS_ALIAS + PATH_SEPARATOR + PATIENT_PATH;
  private static final String IDENTIFIERS_IDENTIFIER_PATH = "identifiers.identifier";
  private static final String NAMES_PREFERRED_PATH = "names.preferred";
  private static final String NAMES_GIVEN_NAME_PATH = "names.givenName";
  private static final String NAMES_MIDDLE_NAME_PATH = "names.middleName";
  private static final String NAMES_FAMILY_NAME_PATH = "names.familyName";
  private static final String START_DATE_TIME_FIELD_NAME = "startDatetime";
  private static final String LOCATION_PROPERTY = "location";
  private static final String VISIT_PROPERTY = "visit";
  private static final String VALUE_REFERENCE_PROPERTY = "valueReference";
  private static final String VOIDED_PROPERTY = "voided";
  private static final String VISIT_ID_PROPERTY = "visitId";
  private static final String ATTRIBUTES = "attributes";
  private static final String ATTRIBUTE_TYPE = "attributeType";
  private static final String NAME = "name";
  private static final String VISIT_STATUS_ATTRIBUTE_TYPE_NAME = "Visit Status";
  private static final String UUID = "uuid";
  private static final int ONE = 1;
  private static final int SIX = 6;

  private final String locationUuid;
  private final String patientUuid;
  private final String query;
  private final boolean sortResults;
  private final String sortFieldName;
  private final boolean sortAscending;
  private final Long dateFrom;
  private final Long dateTo;
  private final String visitStatus;
  private final String timePeriod;

  public OverviewCriteria(Location location, String query, String visitStatus, Long dateFrom, Long dateTo,
                          String timePeriod) {
    this.locationUuid = location.getUuid();
    this.patientUuid = null;
    this.query = query;
    this.visitStatus = visitStatus;
    this.dateFrom = dateFrom;
    this.dateTo = dateTo;
    this.timePeriod = timePeriod;

    this.sortResults = true;
    this.sortFieldName = START_DATE_TIME_FIELD_NAME;
    this.sortAscending = false;
  }

  public OverviewCriteria(VisitSimpleQuery visitForLocationQuery) {
    this.locationUuid = visitForLocationQuery.getLocationUuid();
    this.patientUuid = visitForLocationQuery.getPatientUuid();
    this.query = visitForLocationQuery.getQuery();
    this.visitStatus = visitForLocationQuery.getVisitStatus();
    this.dateFrom = visitForLocationQuery.getDateFrom();
    this.dateTo = visitForLocationQuery.getDateTo();
    this.timePeriod = visitForLocationQuery.getTimePeriod();

    this.sortResults = true;
    this.sortFieldName = START_DATE_TIME_FIELD_NAME;
    this.sortAscending =
        StringUtils.equalsIgnoreCase(visitForLocationQuery.getStartDatetimeSort(), VisitSimpleQuery.SORT_ASCENDING);
  }

  @Override
  public void loadHibernateCriteria(Criteria hibernateCriteria) {
    addLocationCriteria(hibernateCriteria);
    addPatientCriteria(hibernateCriteria);
    addQueryCriteria(hibernateCriteria);
    addVisitDateRangeCriteria(hibernateCriteria);
    addVisitStatusCriteria(hibernateCriteria);
    addVisitsTimePeriodCriteria(hibernateCriteria);
    if (sortResults) {
      addResultSorting(hibernateCriteria);
    }
  }

  private void addVisitDateRangeCriteria(Criteria criteria) {
    if (dateFrom != null) {
      criteria.add(Restrictions.ge(START_DATE_TIME_FIELD_NAME, new Date(dateFrom)));
    }
    if (dateTo != null) {
      criteria.add(Restrictions.le(START_DATE_TIME_FIELD_NAME, new Date(dateTo)));
    }
  }

  private void addVisitStatusCriteria(Criteria criteria) {
    if (StringUtils.isNotBlank(visitStatus)) {
      DetachedCriteria detachedCriteria = getVisitStatusesSubQuery(visitStatus);
      criteria.add(Property.forName(VISIT_ID_PROPERTY).in(detachedCriteria));
    }
  }

  private DetachedCriteria getVisitStatusesSubQuery(String visitStatus) {
    return DetachedCriteria
        .forClass(VisitAttribute.class, ATTRIBUTES)
        .setProjection(Property.forName(VISIT_PROPERTY))
        .createAlias(ATTRIBUTES + PATH_SEPARATOR + ATTRIBUTE_TYPE, ATTRIBUTE_TYPE)
        .add(Restrictions.eq(ATTRIBUTE_TYPE + PATH_SEPARATOR + NAME, VISIT_STATUS_ATTRIBUTE_TYPE_NAME))
        .add(Restrictions.eq(VALUE_REFERENCE_PROPERTY, visitStatus))
        .add(Restrictions.eq(VOIDED_PROPERTY, Boolean.FALSE));
  }

  private void addVisitsTimePeriodCriteria(Criteria criteria) {
    if (StringUtils.isNotBlank(timePeriod)) {
      Date today = DateUtil.getDateIgnoringTime(DateUtil.now());
      if (StringUtils.equalsIgnoreCase(timePeriod, TimePeriod.TODAY.name())) {
        Date tomorrow = DateUtil.getDatePlusDays(today, ONE);
        criteria.add(Restrictions.ge(START_DATE_TIME_FIELD_NAME, today));
        criteria.add(Restrictions.lt(START_DATE_TIME_FIELD_NAME, tomorrow));
      } else if (StringUtils.equalsIgnoreCase(timePeriod, TimePeriod.WEEK.name())) {
        Date weekLaterDate = DateUtil.getDatePlusDays(today, SIX);
        criteria.add(Restrictions.between(START_DATE_TIME_FIELD_NAME, today, weekLaterDate));
      } else if (StringUtils.equalsIgnoreCase(timePeriod, TimePeriod.MONTH.name())) {
        Date monthLaterDate = DateUtil.getDatePlusMonths(today, ONE);
        criteria.add(Restrictions.between(START_DATE_TIME_FIELD_NAME, today, monthLaterDate));
      }
    }
  }

  private void addLocationCriteria(Criteria criteria) {
    if (StringUtils.isNotBlank(locationUuid)) {
      final DetachedCriteria locationCriteria =
          DetachedCriteria.forClass(Location.class).add(Restrictions.eq(UUID, locationUuid)).setProjection(Projections.id());

      criteria.add(Subqueries.propertyEq(LOCATION_PROPERTY, locationCriteria));
    }
  }

  private void addPatientCriteria(Criteria criteria) {
    if (StringUtils.isNotBlank(patientUuid)) {
      final DetachedCriteria patientCriteria =
          DetachedCriteria.forClass(Patient.class).add(Restrictions.eq(UUID, patientUuid)).setProjection(Projections.id());

      criteria.add(Subqueries.propertyEq(PATIENT_ALIAS, patientCriteria));
    }
  }

  private void addQueryCriteria(Criteria c) {
    if (StringUtils.isNotBlank(query)) {
      DetachedCriteria patientNames = getNamesSubQuery();
      DetachedCriteria patientIdentifier = getIdentifierSubQuery();
      c.add(Restrictions.or(Subqueries.propertyIn(PATIENT_ALIAS, patientNames),
          Subqueries.propertyIn(PATIENT_ALIAS, patientIdentifier)));
    }
  }

  private DetachedCriteria getIdentifierSubQuery() {
    return DetachedCriteria
        .forClass(PatientIdentifier.class, IDENTIFIERS_ALIAS)
        .add(getIdentifierCriterion())
        .setProjection(Projections.property(IDENTIFIERS_PATH));
  }

  private DetachedCriteria getNamesSubQuery() {
    return DetachedCriteria
        .forClass(PersonName.class, NAMES_ALIAS)
        .add(getNameCriteria())
        .setProjection(Projections.property(NAMES_PATH));
  }

  private Criterion getNameCriteria() {
    QuerySplitter splitter = new QuerySplitter(query);
    String[] queries = splitter.splitQuery();
    List<Criterion> criteria = new ArrayList<>(queries.length);
    for (String q : queries) {
      criteria.add(getNameCriterion(q));
    }
    Criterion[] array = criteria.toArray(new Criterion[0]);
    return Restrictions.and(array);
  }

  private Criterion getNameCriterion(String query) {
    return Restrictions.and(Restrictions.eq(NAMES_PREFERRED_PATH, Boolean.TRUE),
        Restrictions.or(Restrictions.ilike(NAMES_GIVEN_NAME_PATH, query, MatchMode.ANYWHERE),
            Restrictions.ilike(NAMES_MIDDLE_NAME_PATH, query, MatchMode.ANYWHERE),
            Restrictions.ilike(NAMES_FAMILY_NAME_PATH, query, MatchMode.ANYWHERE)));
  }

  private Criterion getIdentifierCriterion() {
    return Restrictions.ilike(IDENTIFIERS_IDENTIFIER_PATH, query, MatchMode.ANYWHERE);
  }

  private void addResultSorting(Criteria criteria) {
    if (sortAscending) {
      criteria.addOrder(Order.asc(sortFieldName));
    } else {
      criteria.addOrder(Order.desc(sortFieldName));
    }
  }
}
