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
  deleteVisit
} from '../../reducers/schedule-visit.reducer';
import { IRootState } from '../../reducers';
import * as Default from '../../shared/utils/messages';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";
import DeleteVisitModal from './delete-visit-modal';
import ScheduleVisitModal from '../schedule-visit/schedule-visit-modal';
import ManageVisitTable from './table';
import IModalParams from './modal-params';
import ITableParams from './table-params';
import './manage-visits.scss';
import { formatDateIfDefined } from '../../shared/utils/date-util';
import { SINGLE_PAGE_NUMBER } from './table/constants';

const MANAGE_DATE_FORMAT = 'DD MMM YYYY';

interface IProps extends DispatchProps, StateProps, RouteComponentProps<{
  patientUuid: string
}> { }

interface IState {
  showDeleteVisitModal: boolean,
  showScheduleVisitModal: boolean,
  modalParams: IModalParams | null,
  tableParams: ITableParams | null
}

class ManageVisits extends React.Component<IProps, IState> {
  state = {
    showDeleteVisitModal: false,
    showScheduleVisitModal: false,
    modalParams: null as IModalParams | null,
    tableParams: null as ITableParams | null
  }

  private getVisits = (tableParams: ITableParams) => {
    this.setState(
      { tableParams },
      () => this.props.getVisitsPage(
        tableParams.activePage,
        tableParams.itemsPerPage,
        this.props.match.params.patientUuid
      )
    );
  }

  private refetchVisits = () => {
    const { tableParams } = this.state;
    if (!!tableParams) {
      this.props.getVisitsPage(
        tableParams.activePage,
        tableParams.itemsPerPage,
        this.props.match.params.patientUuid
      );
    }
  }

  private deleteVisit = (modalParams: IModalParams) => this.setState({ showDeleteVisitModal: true, modalParams });

  private updateVisit = (modalParams: IModalParams) => this.setState({ showScheduleVisitModal: true, modalParams });

  private handleScheduleVisitButton = () => this.setState({ showScheduleVisitModal: true, modalParams: null });

  private renderScheduleVisitButton = () => {
    return (
      <Button
        id="visit-schedule-button"
        className="btn btn-success btn-md add-btn"
        onClick={this.handleScheduleVisitButton}
      >
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
      deleteCallback={this.deleteVisit}
      updateCallback={this.updateVisit}
      createPathname={this.props.location.pathname}
      loading={this.props.loading}
      pages={this.props.visitsPagesCount}
      fetchDataCallback={this.getVisits}
      sortable={false}
      multiSort={false}
      showPagination={this.props.visitsPagesCount > SINGLE_PAGE_NUMBER}
    />

  confirmDeleteVisit = (modalParams: IModalParams | null) => {
    if (!!modalParams) {
      const { uuid, params } = modalParams;
      this.props.deleteVisit(uuid, params.activePage, params.itemsPerPage, this.props.match.params.patientUuid);
      this.closeDeleteVisitModal();
    }
  }

  closeDeleteVisitModal = () => this.setState({ showDeleteVisitModal: false });

  renderDeleteVisitModal = () => {
    return (
      <DeleteVisitModal
        show={this.state.showDeleteVisitModal}
        modalParams={this.state.modalParams}
        confirm={this.confirmDeleteVisit}
        cancel={this.closeDeleteVisitModal}
      />
    );
  }

  closeScheduleVisitModal = () => this.setState({ showScheduleVisitModal: false });

  saveVisitCallback = () => {
    this.setState(
      { showScheduleVisitModal: false },
      () => this.getVisits(this.state.tableParams!)
    );
  }

  renderScheduleVisitModal = () => {
    return (
      <ScheduleVisitModal
        show={this.state.showScheduleVisitModal}
        patientUuid={this.props.match.params.patientUuid}
        visitUuid={!!this.state.modalParams ? this.state.modalParams.uuid : null}
        confirm={this.confirmDeleteVisit}
        cancel={this.closeScheduleVisitModal}
        saveVisitCallback={this.saveVisitCallback}
        refetchVisits={this.refetchVisits}
      />
    );
  }

  render() {
    return (
      <div className="manage-visits">
        {this.renderDeleteVisitModal()}
        {this.renderScheduleVisitModal()}
        <Form className="fields-form">
          <div className="visit-header-section">
            <div className="header-left-section">
              <ControlLabel className="fields-form-title">
                <h2>{getIntl().formatMessage({ id: 'VISITS_MANAGE_VISITS', defaultMessage: Default.MANAGE_VISITS })}</h2>
                <div className="helper-text">
                  {getIntl().formatMessage({ id: 'VISITS_MANAGE_VISITS_DESCRIPTION', defaultMessage: Default.OVERVIEW_DESCRIPTION })}
                </div>
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
  loading: scheduleVisit.visitsLoading
});

const mapDispatchToProps = ({
  getVisitsPage,
  getVisitsPagesCount,
  deleteVisit
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ManageVisits);
