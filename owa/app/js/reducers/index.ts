/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import { combineReducers } from 'redux';
import { reducers as openmrs } from '@openmrs/react-components';
import patient, { PatientState } from './patient.reducer';
import scheduleVisit, { ScheduleVisitState } from './schedule-visit.reducer';
import overview, { OverviewVisitState } from './overview-visits.reducer';
import notification, { NotificationState } from './notification.reducer';
import customizeReducer, { CustomizeState } 
  from '@bit/soldevelo-cfl.omrs-components.customize/customize.reducer';
import person, { PersonState }
  from '@bit/soldevelo-omrs.cfl-components.person-header/person-header/person.reducer';
import personStatus, { PersonStatusState } 
  from '@bit/soldevelo-omrs.cfl-components.person-status/person-status/person-status.reducer';

export interface IRootState {
  readonly openmrs: any;
  readonly patient: PatientState;
  readonly person: PersonState;
  readonly scheduleVisit: ScheduleVisitState;
  readonly overview: OverviewVisitState;
  readonly notification: NotificationState;
  readonly customizeReducer: CustomizeState;
  readonly personStatus: PersonStatusState;
}

export default combineReducers<IRootState>({
  openmrs,
  patient,
  person,
  scheduleVisit,
  overview,
  notification,
  customizeReducer,
  personStatus
});
