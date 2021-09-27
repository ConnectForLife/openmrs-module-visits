/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React from "react";
import { connect } from 'react-redux';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";
import _ from "lodash";
import { RouteComponentProps } from 'react-router-dom';
import IModalParams from "../manage-visits/modal-params";
import { IRootState } from '../../reducers';
import {
  getVisitTypes,
  getLocations,
  updateVisit,
  saveVisit,
  getVisitTimes,
  getVisit,
  getVisitStatuses,
  reset,
} from "../../reducers/schedule-visit.reducer";
import {
  Form,
  FormGroup,
  FormControl,
  Col,
  Button,
  Row,
  Modal,
} from "react-bootstrap";
import ErrorDesc from '@bit/soldevelo-omrs.cfl-components.error-description';
import FormLabel from '@bit/soldevelo-omrs.cfl-components.form-label';
import OpenMrsDatePicker from '@bit/soldevelo-omrs.cfl-components.openmrs-date-picker';
import * as Default from '../../shared/utils/messages';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCircle, faTimes } from "@fortawesome/free-solid-svg-icons";

import "../schedule-visit/schedule-visit-modal.scss"

interface IProps extends DispatchProps, StateProps, RouteComponentProps {
  show: boolean;
  modalParams: IModalParams | null;
  patientUuid: string;
  visitUuid?: string;
  confirm: (modalParams: IModalParams | null) => void;
  cancel: () => void;
  refetchVisits: () => void;
}

interface IState {
  isSaveButtonDisabled: boolean;
}

const FORM_CLASS = 'form-control';
const ERROR_FORM_CLASS = FORM_CLASS + ' error-field';

class ScheduleVisitModal extends React.PureComponent<IProps, IState> {
  state = {
    isSaveButtonDisabled: true,
  };

  componentDidMount() {
    if (this.isEdit()) {
      this.loadComponent();
      this.props.getVisit(this.props.visitUuid!);
    } else {
      this.props.reset(this.loadComponent);
    }
  }

  componentDidUpdate(prevProps, prevState, snapshot) {
    if (!!this.props.visitUuid && this.props.visitUuid !== prevProps.visitUuid) {
      this.loadComponent();
      this.props.getVisit(this.props.visitUuid!);
    } else if (!!prevProps.visitUuid && this.props.visitUuid === null) {
      this.props.reset(this.loadComponent);
    }
  }

  loadComponent = () => {
    this.props.getVisitTypes();
    this.props.getLocations();
    this.props.getVisitTimes();
    this.props.getVisitStatuses();

    this.handleChange(this.props.patientUuid, "patientUuid");
  };

  isEdit = () => !!this.props.visitUuid;

  validate = () =>
    this.props.visit.validate(true, this.isEdit()).then((visit) => {
      this.setState({ isSaveButtonDisabled: !!Object.keys(visit.errors).length });
    });

  handleChange = (newValue: string, prop: string) => {
    const cloned = _.cloneDeep(this.props.visit);
    cloned[prop] = newValue;
    cloned.touchedFields[prop] = true;
    this.props.updateVisit(cloned);
  };

  handleSave = () => this.props.saveVisit(this.props.visit, this.saveVisitCallback);

  saveVisitCallback = () => {
    this.closeModal();
    this.props.refetchVisits();
  };

  closeModal = () => {
    if (!this.isEdit()) {
      this.props.reset(this.loadComponent);
    }
    this.props.cancel();
  };

  renderDropdown = (
    errors,
    label: string,
    fieldName: string,
    options: Array<React.ReactFragment>,
    required?: boolean
  ) => (
    <FormGroup controlId={fieldName}>
      <FormLabel label={label} mandatory={required} />
      <FormControl
        componentClass="select"
        name={fieldName}
        value={this.props.visit[fieldName]}
        onChange={(e) => this.handleChange((e.target as HTMLInputElement).value, fieldName)}
        className={errors && errors[fieldName] ? ERROR_FORM_CLASS : FORM_CLASS}
      >
        <option value="" />
        {options}
      </FormControl>
      {errors && <ErrorDesc field={errors[fieldName]} />}
    </FormGroup>
  );

  renderDatePicker = (errors, label: string, fieldName: string) => (
    <FormGroup controlId={fieldName}>
      <FormLabel label={label} />
      <OpenMrsDatePicker
        value={this.props.visit[fieldName]}
        onChange={(isoDate) => this.handleChange(isoDate, fieldName)}
      />
      {errors && <ErrorDesc field={errors[fieldName]} />}
    </FormGroup>
  );

  renderVisitDate = (errors) =>
    this.renderDatePicker(
      errors,
      getIntl().formatMessage({
        id: "VISITS_VISIT_PLANNED_DATE_LABEL",
        defaultMessage: Default.VISIT_PLANNED_DATE_LABEL,
      }),
      "startDate"
    );

  renderActualDate = (errors) =>
    this.renderDatePicker(
      errors,
      getIntl().formatMessage({
        id: "VISITS_VISIT_ACTUAL_DATE_LABEL",
        defaultMessage: Default.VISIT_ACTUAL_DATE_LABEL,
      }),
      "actualDate"
    );

  renderLocation = (errors) =>
    this.renderDropdown(
      errors,
      getIntl().formatMessage({ id: "VISITS_LOCATION_LABEL", defaultMessage: Default.LOCATION_LABEL }),
      "location",
      this.props.locations.map((type) => (
        <option value={type.uuid} key={type.uuid}>
          {type.display}
        </option>
      ))
    );

  renderVisitType = (errors) =>
    this.renderDropdown(
      errors,
      getIntl().formatMessage({ id: "VISITS_VISIT_TYPE_LABEL", defaultMessage: Default.VISIT_TYPE_LABEL }),
      "type",
      this.props.visitTypes.map((type) => (
        <option value={type.uuid} key={type.uuid}>
          {type.display}
        </option>
      )),
      true
    );

  renderVisitStatus = (errors) =>
    this.renderDropdown(
      errors,
      getIntl().formatMessage({ id: "VISITS_VISIT_STATUS_LABEL", defaultMessage: Default.VISIT_STATUS_LABEL }),
      "status",
      this.props.visitStatuses.map((status) => (
        <option value={status} key={status}>
          {status}
        </option>
      )),
      true
    );

  renderVisitTime = (errors) =>
    this.renderDropdown(
      errors,
      getIntl().formatMessage({ id: "VISITS_VISIT_TIME_LABEL", defaultMessage: Default.VISIT_TIME_LABEL }),
      "time",
      this.props.visitTimes.map((time) => (
        <option value={time} key={time}>
          {time}
        </option>
      ))
    );

  renderSaveButton = () => (
    <Button
      id="schedule-visit-save"
      className="btn btn-success btn-md pull-right confirm"
      onClick={this.handleSave}
      disabled={this.state.isSaveButtonDisabled}
    >
      {getIntl().formatMessage({
        id: this.isEdit() ? "VISITS_SAVE_BUTTON_LABEL" : "VISITS_SCHEDULE_VISIT_BUTTON_LABEL",
        defaultMessage: Default.SAVE_BUTTON_LABEL,
      })}
    </Button>
  );

  renderCancelButton = () => (
    <Button id="schedule-visit-cancel" onClick={this.closeModal}>
      <span className="fa-stack fa-2x">
        <FontAwesomeIcon icon={faCircle} className="fa-stack-2x icon-button-background" />
        <FontAwesomeIcon icon={faTimes} className="fa-stack-1x" />
      </span>
    </Button>
  );

  buildModal = () => {
    const { show, cancel } = this.props;
    const { errors } = this.props.visit;
    return (
      <Modal id="schedule-visit-modal" show={show} onHide={cancel}>
        <Modal.Body>
          <div className="modal-title">
            {this.isEdit()
              ? getIntl().formatMessage({ id: "VISITS_EDIT_VISIT", defaultMessage: Default.EDIT_VISIT })
              : getIntl().formatMessage({ id: "VISITS_SCHEDULE_VISIT", defaultMessage: Default.SCHEDULE_VISIT })}
          </div>
          <Form className="fields-form">
            <Row>
              <Col md={12}>
                {this.renderVisitDate(errors)}
                {this.renderVisitTime(errors)}
                {this.renderLocation(errors)}
                {this.renderVisitType(errors)}
                {this.isEdit() && this.renderVisitStatus(errors)}
              </Col>
            </Row>
            <Row>
              <FormGroup className="control-buttons">
                {this.renderCancelButton()}
                {this.renderSaveButton()}
              </FormGroup>
            </Row>
          </Form>
        </Modal.Body>
      </Modal>
    );
  };

  render() {
    this.validate();
    return this.props.show ? this.buildModal() : null;
  }
}

const mapStateToProps = ({ scheduleVisit }: IRootState) => ({
  visit: scheduleVisit.visit,
  visitTypes: scheduleVisit.visitTypes,
  visitStatuses: scheduleVisit.visitStatuses,
  visitTimes: scheduleVisit.visitTimes,
  locations: scheduleVisit.locations
});

const mapDispatchToProps = ({
  getVisitTypes,
  getVisitTimes,
  getLocations,
  updateVisit,
  saveVisit,
  getVisit,
  getVisitStatuses,
  reset
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ScheduleVisitModal);
