/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React from 'react'
import { Route, Switch } from 'react-router-dom';
import { Header } from '@openmrs/react-components';
import BreadCrumb from './components/bread-crumb';
import { ManageVisitsWithHeader as ManageVisits } from './components/hoc/with-patient-header';
import { withNotifications } from './components/hoc/with-notifications';
import OverviewVisits from './components/overview-visits';
import Customize from '@bit/soldevelo-cfl.omrs-components.customize'
import { initializeLocalizationWrapper } from '@bit/soldevelo-omrs.cfl-components.localization-wrapper';
import messagesEN from "./translations/en.json";

initializeLocalizationWrapper({
  en: messagesEN,
});

export default (store) => (
    <div>
        <Customize />
        <Header />
        <BreadCrumb />
        <Switch>
            <Route path="/visits/manage/:patientUuid" component={withNotifications(ManageVisits)} />
            <Route exact path="/visits/overview" component={OverviewVisits} />
        </Switch>
    </div>
);
