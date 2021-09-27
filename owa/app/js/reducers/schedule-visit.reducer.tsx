/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import _ from 'lodash';

import { REQUEST, SUCCESS, FAILURE } from './action-type.util';
import axiosInstance from '@bit/soldevelo-omrs.cfl-components.shared/axios'
import IVisitType from '../shared/model/visit-type';
import VisitUI from '../shared/model/visit-ui';
import * as Default from '../shared/utils/messages';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";
import { handleRequest } from '@bit/soldevelo-omrs.cfl-components.request-toast-handler';
import ILocation from '../shared/model/location';
import IVisitDetails from '../shared/model/visit-details';
import IModalParams from '../components/manage-visits/modal-params';

export const ACTION_TYPES = {
  GET_VISITS: 'scheduleVisitReducer/GET_VISITS',
  GET_VISITS_PAGES_COUNT: 'scheduleVisitReducer/GET_VISITS_PAGES_COUNT',
  GET_VISIT_TYPES: 'scheduleVisitReducer/GET_VISIT_TYPES',
  GET_VISIT_TIMES: 'scheduleVisitReducer/GET_VISIT_TIMES',
  GET_VISIT_STATUSES: 'scheduleVisitReducer/GET_VISIT_STATUSES',
  GET_LOCATIONS: 'scheduleVisitReducer/GET_LOCATIONS',
  GET_VISIT: 'scheduleVisitReducer/GET_VISIT',
  DELETE_VISIT: 'scheduleVisitReducer/DELETE_VISIT',
  UPDATE_VISIT: 'scheduleVisitReducer/UPDATE_VISIT',
  POST_VISIT: 'scheduleVisitReducer/POST_VISIT',
  RESET: 'scheduleVisitReducer/RESET'
};

const initialState = {
  visit: VisitUI.getNew(),
  visits: [] as Array<IVisitDetails>,
  visitsLoading: false,
  visitTypes: [] as Array<IVisitType>,
  visitTimes: [] as Array<string>,
  visitStatuses: [] as Array<string>,
  locations: [] as Array<ILocation>,
  visitsPagesCount: 0
};

export type ScheduleVisitState = Readonly<typeof initialState>;

export default (state = initialState, action) => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.POST_VISIT):
    case FAILURE(ACTION_TYPES.POST_VISIT):
    case SUCCESS(ACTION_TYPES.POST_VISIT):
    case REQUEST(ACTION_TYPES.GET_VISIT_TYPES):
    case FAILURE(ACTION_TYPES.GET_VISIT_TYPES):
    case REQUEST(ACTION_TYPES.GET_VISIT_TIMES):
    case FAILURE(ACTION_TYPES.GET_VISIT_TIMES):
    case REQUEST(ACTION_TYPES.GET_VISIT_STATUSES):
    case FAILURE(ACTION_TYPES.GET_VISIT_STATUSES):
    case REQUEST(ACTION_TYPES.GET_LOCATIONS):
    case FAILURE(ACTION_TYPES.GET_LOCATIONS):
    case REQUEST(ACTION_TYPES.GET_VISIT):
    case FAILURE(ACTION_TYPES.GET_VISIT):
    case REQUEST(ACTION_TYPES.DELETE_VISIT):
    case FAILURE(ACTION_TYPES.DELETE_VISIT):
      return {
        ...state
      };
    case REQUEST(ACTION_TYPES.GET_VISITS):
      return {
        ...state,
        visitsLoading: true
      };
    case FAILURE(ACTION_TYPES.GET_VISITS):
      return {
        ...state,
        visitsLoading: false
      };
    case SUCCESS(ACTION_TYPES.GET_VISITS):
      return {
        ...state,
        visits: action.payload.data.content,
        visitsPagesCount: action.payload.data.pageCount,
        visitsLoading: false
      };
    case SUCCESS(ACTION_TYPES.GET_VISITS_PAGES_COUNT):
      return {
        ...state,
        visitsPagesCount: Math.ceil((action.payload.data.results.length / action.meta))
      };
    case SUCCESS(ACTION_TYPES.GET_VISIT_TYPES):
      return {
        ...state,
        visitTypes: action.payload.data.results
      };
    case SUCCESS(ACTION_TYPES.GET_VISIT):
      return {
        ...state,
        visit: new VisitUI(action.payload.data)
      };
    case SUCCESS(ACTION_TYPES.DELETE_VISIT):
      return {
        ...state
      };
    case SUCCESS(ACTION_TYPES.GET_VISIT_TIMES):
      return {
        ...state,
        visitTimes: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.GET_VISIT_STATUSES):
      return {
        ...state,
        visitStatuses: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.GET_LOCATIONS):
      return {
        ...state,
        locations: action.payload.data.results
      };
    case ACTION_TYPES.UPDATE_VISIT: {
      return {
        ...state,
        visit: action.payload
      };
    }
    case ACTION_TYPES.RESET:
      return {
        ...state,
        visit: VisitUI.getNew()
      };
    default:
      return state;
  }
};

const baseUrl = "/ws";
const restUrl = `${baseUrl}/rest/v1`;
const moduleUrl = `${baseUrl}/visits`;
const visitTypeUrl = `${restUrl}/visittype`;
const visitUrl = `${restUrl}/visit`;
const locationUrl = `${restUrl}/location`;
const visitsTimesUrl = `${moduleUrl}/times`;
const visitsStatusesUrl = `${moduleUrl}/statuses`;

const visitLocationTagName = 'Visit Location';

export const getVisitTypes = () => async (dispatch) => {
  await dispatch({
    type: ACTION_TYPES.GET_VISIT_TYPES,
    payload: axiosInstance.get(visitTypeUrl)
  });
};

export const getVisitTimes = () => async (dispatch) => {
  await dispatch({
    type: ACTION_TYPES.GET_VISIT_TIMES,
    payload: axiosInstance.get(visitsTimesUrl)
  });
};

export const getVisitStatuses = () => async (dispatch) => {
  await dispatch({
    type: ACTION_TYPES.GET_VISIT_STATUSES,
    payload: axiosInstance.get(visitsStatusesUrl)
  });
};

export const getLocations = () => async (dispatch) => {
  await dispatch({
    type: ACTION_TYPES.GET_LOCATIONS,
    payload: axiosInstance.get(locationUrl, {
      params: {
        tag: visitLocationTagName
      }
    })
  });
};

export const updateVisit = (visit: VisitUI) => async (dispatch) => {
  dispatch({
    type: ACTION_TYPES.UPDATE_VISIT,
    payload: await visit.validate(false, true)
  })
};

export const saveVisit = (visit: VisitUI, successCallback?) => async (dispatch) => {
  const isEdit = !!visit.uuid;
  const validated = await visit.validate(true, isEdit);
  if (_.isEmpty(validated.errors)) {
    const payload = isEdit ?
      axiosInstance.put(`${moduleUrl}/${visit.uuid}`, visit)
      : axiosInstance.post(moduleUrl, visit);

    const body = {
      type: ACTION_TYPES.POST_VISIT,
      payload
    };

    await handleRequest(
      dispatch,
      body,
      getIntl().formatMessage({
        id: isEdit ? 'VISITS_GENERIC_SUCCESS' : 'VISITS_SCHEDULE_VISIT_SUCCESS',
        defaultMessage: Default.GENERIC_SUCCESS
      }),
      getIntl().formatMessage({ id: 'VISITS_GENERIC_FAILURE', defaultMessage: Default.GENERIC_FAILURE })
    );
    if (successCallback) {
      successCallback();
    }
  } else {
    dispatch({
      type: ACTION_TYPES.UPDATE_VISIT,
      payload: validated
    })
  }
};

export const getVisit = (visitUuid: string) => async (dispatch) => {
  await dispatch({
    type: ACTION_TYPES.GET_VISIT,
    payload: axiosInstance.get(`${moduleUrl}/${visitUuid}`)
  });
};

export const getVisitsPage = (page: number, size: number, patientUuid: string) => async (dispatch) => {
  const url = `${moduleUrl}/patient/${patientUuid}`;
  await dispatch({
    type: ACTION_TYPES.GET_VISITS,
    payload: axiosInstance.get(url, {
      params: {
        page: page + 1,
        rows: size,
      }
    })
  });
};

export const getVisitsPagesCount = (size: number, patientUuid: string) => async (dispatch) => {
  const url = `${visitUrl}?patient=${patientUuid}&v=custom:(uuid)`;
  await dispatch({
    type: ACTION_TYPES.GET_VISITS_PAGES_COUNT,
    payload: axiosInstance.get(url),
    meta: size
  });
};

export const deleteVisit = (uuid: string, activePage: number, itemsPerPage: number, patientUuid: string) => async (dispatch) => {
  const url = `/ws/rest/v1/visit/${uuid}`;
  const body = {
    type: ACTION_TYPES.DELETE_VISIT,
    payload: axiosInstance.delete(url)
  };
  await handleRequest(dispatch, body,
    getIntl().formatMessage({ id: 'VISITS_GENERIC_SUCCESS', defaultMessage: Default.GENERIC_SUCCESS }),
    getIntl().formatMessage({ id: 'VISITS_GENERIC_FAILURE', defaultMessage: Default.GENERIC_FAILURE }));
  dispatch(getVisitsPage(activePage, itemsPerPage, patientUuid));
};

export const reset = (successCallback?) => async (dispatch) => {
  await dispatch({
    type: ACTION_TYPES.RESET
  });
  if (successCallback) {
    successCallback();
  }
};
