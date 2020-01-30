/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

// Messages displayed on the frontend
export const MODULE_NAME = 'Visits';
export const SAVE_BUTTON_LABEL = 'Save';

// Generic messages
export const GENERIC_FAILURE = 'An error occurred.';
export const GENERIC_PROCESSING = 'Processing...';
export const GENERIC_SUCCESS = 'Success.';
export const GENERIC_INVALID_FORM = 'Form is invalid. Check fields and send it again.';
export const GENERIC_LOADING = 'Loading...';

// Breadcrumb
export const GENERAL_MODULE_BREADCRUMB = 'Visits';
export const MANAGE_VISITS_BREADCRUMB = 'Manage visits';
export const OVERVIEW_VISIT_BREADCRUMB = 'Visit Overview';
export const SCHEDULE_VISIT_BREADCRUMB = 'Schedule visit';
export const EDIT_VISIT_BREADCRUMB = 'Edit visit';

// Schedule visits
export const SCHEDULE_VISIT = 'Schedule visit';
export const EDIT_VISIT = 'Edit visit';
export const VISIT_DATE_LABEL = 'Visit date';
export const VISIT_TYPE_LABEL = 'Visit type';
export const VISIT_STATUS_LABEL = 'Visit status';
export const VISIT_TIME_LABEL = 'Visit time';
export const LOCATION_LABEL = 'Visit location';

// Manage visits
export const MANAGE_VISITS = 'Manage Visits';
export const MANAGE_VISITS_COLUMNS = [
  { Header: 'Date', accessor: 'startDate'},
  { Header: 'Time', accessor: 'time'},
  { Header: 'Location', accessor: 'location'},
  { Header: 'Type', accessor: 'type'},
  { Header: 'Status', accessor: 'status' }
];
export const ACTIONS_COLUMN_LABEL = 'Actions';

// Validation
export const FIELD_REQUIRED = 'Required';

//Overview
export const OVERVIEW_TITLE = 'Visit Overview';
export const OVERVIEW_PATIENT_ID_HEADER = 'Patient ID';
export const OVERVIEW_NAME_HEADER = 'Name';
export const OVERVIEW_DATE_HEADER = 'Date';
export const OVERVIEW_TIME_HEADER = 'Time';
export const OVERVIEW_TYPE_HEADER = 'Type';
export const OVERVIEW_STATUS_HEADER = 'Status';
export const OVERVIEW_PATIEND_ID_PREFIX = 'OpenMRS ID:';
export const OVERVIEW_SEARCH_TITLE = 'Search';
