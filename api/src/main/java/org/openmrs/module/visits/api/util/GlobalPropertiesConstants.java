package org.openmrs.module.visits.api.util;

public final class GlobalPropertiesConstants {

  public static final GPDefinition PAST_VISITS_LIMIT =
      new GPDefinition(
          "visits.past-visits-limit",
          "1",
          "Limit of past visits displayed on the patient dashboard visits widget.",
          true);

  public static final GPDefinition UPCOMING_VISITS_LIMIT =
      new GPDefinition(
          "visits.upcoming-visits-limit",
          "2",
          "Limit of upcoming visits displayed on the patient dashboard visits widget.",
          true);

  public static final GPDefinition VISIT_TIMES =
      new GPDefinition(
          "visits.visit-times",
          "Morning,Afternoon,Evening",
          "Coma separated list of visit types used to schedule a visit.",
          true);

  public static final GPDefinition VISIT_STATUSES =
      new GPDefinition(
          "visits.visit-statuses",
          "SCHEDULED,OCCURRED,MISSED",
          "Coma separated list of visit statuses used to schedule a visit. "
              + "IMPORTANT: Status of newly created visits is always set to the first element of the list.",
          true);

  public static final String VISIT_FORM_URIS_KEY = "visits.visit-form-uris";

  public static final GPDefinition MINIMUM_VISIT_DELAY_TO_MARK_IT_AS_MISSED =
      new GPDefinition(
          "visits.minimumVisitDelayToAutomaticallyMarkItAsMissed",
          "1",
          "The number of days after which "
              + "missed visits should be automatically marked as missed.",
          true);

  public static final GPDefinition STATUSES_ENDING_VISIT =
      new GPDefinition(
          "visits.statusesEndingVisit",
          "OCCURRED",
          "The comma-separated list allowing to specify "
              + "statuses which point on already completed visits. "
              + "Visits with these statuses should not be changed to "
              + "missed.",
          true);

  public static final GPDefinition STATUS_OF_MISSED_VISIT =
      new GPDefinition(
          "visits.statusOfMissedVisit",
          "MISSED",
          "The value specifying a status that will be set if the visit will be "
              + "determined to be marked as missing.",
          true);

  public static final GPDefinition STATUS_OF_OCCURRED_VISIT =
      new GPDefinition(
          "visits.statusOfOccurredVisit",
          "OCCURRED",
          "The value specifying a status that will be set if the visit will be "
              + "determined to be marked as occurred.",
          true);

  public static final GPDefinition ENCOUNTER_DATETIME_VALIDATION =
      new GPDefinition(
          "visits.encounterDatetimeValidation",
          "true",
          "Used to control if encounter datetime should be validated. By default OpenMRS doesn't support adding "
              + "encounter if its date isn't between visit start and end date. This GP can be used to turn off this "
              + "default validation. When the value is set to false then the custom EnterHtmlFormFragment is used to "
              + "override the encounter validation in emr api and additionally OpenMRS core encounter validator "
              + "is overridden. Possible values: true, false",
          true);

  public static final GPDefinition MISSED_VISIT_CHANGER_CREATION_GP =
      new GPDefinition(
          "visits.isMissedVisitChangerShouldBeCreated",
          "false",
          "Used to determine if Missed Visits Status Changer task should be created during module startup. "
              + "Changes will be applied the next time the module is restarted. If task already exists "
              + "and GP value is false it will not remove task during next server restart - it has to be "
              + "removed manually. Possible values: true, false");

  private GlobalPropertiesConstants() {}
}
