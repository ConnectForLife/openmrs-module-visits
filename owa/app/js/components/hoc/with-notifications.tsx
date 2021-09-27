
/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React from 'react'
import Notification from '../notification/notification';
import { RouteComponentProps } from 'react-router-dom';

export const withNotifications = (WrappedComponent) => {
    return class extends React.Component<RouteComponentProps<{ patientUuid: string }>> {
        render() {
            return (
                <>
                    <Notification {...this.props}/>
                    <WrappedComponent  {...this.props}/>
                </>
            );
        }
    }
}