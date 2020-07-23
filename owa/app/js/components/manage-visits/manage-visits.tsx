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
import { Form, ControlLabel, FormGroup, Button } from 'react-bootstrap';
import { RouteComponentProps } from 'react-router-dom';
import _ from 'lodash';

import {
  getVisitsPage,
  getVisitsPagesCount,
  deleteVisit,
  openModal,
  closeModal
} from '../../reducers/schedule-visit.reducer';
import { IRootState } from '../../reducers';
import * as Default from '../../shared/utils/messages';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";
import { history } from '../../config/redux-store';
import RemoveVisitModal from './remove-visit-modal';
import ManageVisitTable from './table';
import IModalParams from './modal-params';
import ITableParams from './table-params';
import './manage-visits.scss';
import { formatDateIfDefined } from '../../shared/utils/date-util';

const MANAGE_DATE_FORMAT = 'DD.MM.YYYY';

interface IProps extends DispatchProps, StateProps, RouteComponentProps<{
  patientUuid: string
}> { }

interface IState {
}

class ManageVisits extends React.Component<IProps, IState> {

  private getVisits = (tableParams: ITableParams) => {
    this.props.getVisitsPage(tableParams.activePage, tableParams.itemsPerPage, this.props.match.params.patientUuid);
  }

  private removeVisit = (modalParams: IModalParams) => {
    this.props.openModal(modalParams);
  }

  private handleScheduleVisitButton = () => {
    history.push(`/visits/manage/${this.props.match.params.patientUuid}/schedule`);
  }

  private renderScheduleVisitButton = () => {
    return (
      <Button
        className="btn btn-success btn-md add-btn"
        onClick={this.handleScheduleVisitButton}>
        {getIntl().formatMessage({ id: 'VISITS_SCHEDULE_VISIT', defaultMessage: Default.SCHEDULE_VISIT })}
      </Button>
    );
  }

  private renderTable = () =>
    <ManageVisitTable
      data={this.props.visits.map(visit => {
        return {
          ...visit,
          startDate: formatDateIfDefined(MANAGE_DATE_FORMAT, visit.startDate),
          actualDate: formatDateIfDefined(MANAGE_DATE_FORMAT, visit.actualDate)
        }
      })}
      removeCallback={this.removeVisit}
      createPathname={this.props.location.pathname}
      loading={this.props.loading}
      pages={this.props.visitsPagesCount}
      fetchDataCallback={this.getVisits}
      sortable={false}
      multiSort={false}
      showPagination={true}
    />

  confirm = (modalParams: IModalParams | null) => {
    if (!!modalParams) {
      const { uuid, params } = modalParams;
      this.props.deleteVisit(uuid, params.activePage, params.itemsPerPage, this.props.match.params.patientUuid);
    }
  }

  renderModal = () => {
    return (
      <RemoveVisitModal
        show={this.props.showModal}
        modalParams={this.props.toRemove}
        confirm={this.confirm}
        cancel={this.props.closeModal} />);
  }

  render() {
    return (
      <div className="manage-visits">
        {this.renderModal()}
        <Form className="fields-form">
          <div className="visit-header-section">
            <div className="header-left-section">
              <ControlLabel className="fields-form-title">
                <h2>{getIntl().formatMessage({ id: 'VISITS_MANAGE_VISITS', defaultMessage: Default.MANAGE_VISITS })}</h2>
              </ControlLabel>
            </div>
            <div className="header-right-section">
              <div className="schedule-button">
                {this.renderScheduleVisitButton()}
              </div>
            </div>
          </div>
          <FormGroup>
            {this.renderTable()}
          </FormGroup>
        </Form>
      </div>
    );
  };
}

const mapStateToProps = ({ scheduleVisit }: IRootState) => ({
  visits: scheduleVisit.visits,
  visitsPagesCount: scheduleVisit.visitsPagesCount,
  loading: scheduleVisit.visitsLoading,
  showModal: scheduleVisit.showModal,
  toRemove: scheduleVisit.toRemove
});

const mapDispatchToProps = ({
  getVisitsPage,
  getVisitsPagesCount,
  deleteVisit,
  openModal,
  closeModal
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ManageVisits);
