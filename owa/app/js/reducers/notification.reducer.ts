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
import INotification from '../shared/model/notification';

export const ACTION_TYPES = {
  GET_NOTIFICATION: 'notificationReducer/GET_NOTIFICATION',
  UPDATE_NOTIFICATION: 'notificationReducer/UPDATE_NOTIFICATION',
  RESET: 'notificationReducer/RESET'
};

const initialState = {
  notifications: [] as INotification[],
  notificationLoading: false
};

export type NotificationState = Readonly<typeof initialState>;

export default (state = initialState, action) => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.GET_NOTIFICATION):
      return {
        ...state,
        notificationLoading: true
      };
    case FAILURE(ACTION_TYPES.GET_NOTIFICATION):
      return {
        ...state,
        notificationLoading: false
      };
    case SUCCESS(ACTION_TYPES.GET_NOTIFICATION):
      return {
        ...state,
        notificationLoading: false,
        notifications: action.payload.data
      };
    case ACTION_TYPES.UPDATE_NOTIFICATION:
      return {
        ...state,
        notificationLoading: false,
        notifications: action.payload
      };
    case ACTION_TYPES.RESET:
      return { 
        ..._.cloneDeep(initialState)
      };
    default:
      return state;
  }
};

const notificationsUrl = "/ws/visits/notifications";

export const getNotifications = () => async (dispatch) => {
  await dispatch({
    type: ACTION_TYPES.GET_NOTIFICATION,
    payload: axiosInstance.get(notificationsUrl)
  });
};

export const removeFirstNotification = (notifications: INotification[]) => async (dispatch) => {
  notifications.shift();
  await dispatch({
    type: ACTION_TYPES.GET_NOTIFICATION,
    payload: notifications
  });
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
