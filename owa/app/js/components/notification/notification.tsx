/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React from 'react';
import { connect } from 'react-redux';
import _ from 'lodash';

import {
  getNotifications,
  removeFirstNotification
} from '../../reducers/notification.reducer';
import { IRootState } from '../../reducers';
import { successToast, errorToast } from '@bit/soldevelo-omrs.cfl-components.toast-handler';

interface IProps extends DispatchProps, StateProps { }

interface IState { }

class Notification extends React.Component<IProps, IState> {

  componentDidMount() {
    this.props.getNotifications();
  }

  componentDidUpdate() {
    if (this.props.notifications[0]) {
      const notification = this.props.notifications[0];
      if (notification.errorMessage) {
        errorToast(notification.message);
      } else {
        successToast(notification.message);
      }
      this.props.removeFirstNotification(this.props.notifications);
    }
  }

  render() {
    return (null);
  };
}

const mapStateToProps = ({ notification }: IRootState) => ({
  notifications: notification.notifications,
  notificationLoading: notification.notificationLoading
});

const mapDispatchToProps = ({
  getNotifications,
  removeFirstNotification
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Notification);
