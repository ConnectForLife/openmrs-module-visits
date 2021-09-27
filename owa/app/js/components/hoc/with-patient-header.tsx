/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import Header from '@bit/soldevelo-omrs.cfl-components.person-header';
import React from 'react';
import { RouteComponentProps } from 'react-router-dom';
import ManageVisits from '../manage-visits/manage-visits';
import PersonStatus from '@bit/soldevelo-omrs.cfl-components.person-status';

interface IWrappedComponentProps extends RouteComponentProps<{ patientUuid: string }> {
  isNew?: boolean;
}

const withPatientHeader = (WrappedComponent) => {
  return class extends React.Component<IWrappedComponentProps> {
    
    getBaseUrl = () => {
      const path = window.location.pathname;
      return path.substring(0, path.indexOf('/owa/')) + '/';
    }
    
    render() {
      const newProps = {
        ...this.props,
        patientUuid: this.props.match.params.patientUuid,
        dashboardType: "PATIENT",
        redirectUrl: this.getBaseUrl() + "coreapps/clinicianfacing/patient.page?patientId=" 
          + this.props.match.params.patientUuid,
        displayTelephone: true
      };
      return (
          <div className="body-wrapper">
            <Header {...newProps}>
              <PersonStatus patientUuid={newProps.patientUuid}/>
            </Header>
            <div className="content">
              <WrappedComponent  {...this.props}/>
            </div>
          </div>
      )
    }
  }
}

export const ManageVisitsWithHeader = withPatientHeader(ManageVisits);
