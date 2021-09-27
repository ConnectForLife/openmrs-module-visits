
import React, { ReactFragment } from 'react';
import { connect } from 'react-redux';
import { Link, withRouter, RouteComponentProps } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import UrlPattern from 'url-pattern';
import './bread-crumb.scss';
import { UnregisterCallback } from 'history';
import * as Default from '../../shared/utils/messages';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";
import { getPatient } from '../../reducers/patient.reducer';
import { IRootState } from '../../reducers';

const EDIT_VISIT_PATTERN = new UrlPattern(`/visits/manage/:patientUuid/schedule/:visitUuid`);
const OVERVIEW_VISIT_PATTERN = new UrlPattern(`/visits/overview`);
const SCHEDULE_VISIT_PATTERN = new UrlPattern(`/visits/manage/:patientUuid/schedule`);
const MANAGE_VISIT_PATTERN = new UrlPattern(`/visits/manage/:patientUuid*`);
const OMRS_ROUTE = '../../';
const MANAGE_VISITS_ROUTE = patientUuid => `/visits/manage/${patientUuid}`;
const PATIENT_DASHBOARD_ROUTE = patientUuid => `${OMRS_ROUTE}coreapps/clinicianfacing/patient.page?patientId=${patientUuid}`;

interface IBreadCrumbProps extends DispatchProps, StateProps, RouteComponentProps {
};

interface IBreadCrumbState {
  current: string
};

class BreadCrumb extends React.PureComponent<IBreadCrumbProps, IBreadCrumbState>{
  unlisten: UnregisterCallback;

  constructor(props: IBreadCrumbProps) {
    super(props);

    const { history } = this.props;
    this.state = {
      current: history.location.pathname
    };
  }

  componentDidMount = () => {
    const { history } = this.props;
    this.unlisten = history.listen((location) => {
      const current = location.pathname;
      this.setState({ current });
    });
  }

  componentWillUnmount = () => this.unlisten();


  buildBreadCrumb = () =>
    <div id="breadcrumbs" className="breadcrumb">
      {this.renderCrumbs(this.getCrumbs(this.state.current))}
    </div>


  getCrumbs = (path: string): Array<ReactFragment> => {
    if (!!OVERVIEW_VISIT_PATTERN.match(path.toLowerCase())) {
      return this.getOverviewVisitCrumbs();
    } if (!!SCHEDULE_VISIT_PATTERN.match(path.toLowerCase())) {
      return this.getScheduleVisitCrumbs(path);
    } if (!!EDIT_VISIT_PATTERN.match(path.toLowerCase())) {
      return this.getEditVisitCrumbs(path);
    } else if (!!MANAGE_VISIT_PATTERN.match(path.toLowerCase())) {
      return this.getManageVisitCrumbs(path);
    } else {
      return [this.renderLastCrumb(getIntl().formatMessage({ id: 'VISITS_GENERAL_MODULE_BREADCRUMB', defaultMessage: Default.GENERAL_MODULE_BREADCRUMB }))];
    }
  }

  getPatientLabelCrumb = (path: string, pattern: UrlPattern) => {
    const { patient, getPatient } = this.props;
    const match = pattern.match(path.toLowerCase());
    const patientUuid = match.patientUuid;

    if (patient.uuid != patientUuid) {
      getPatient(patientUuid);
    }

    const patientLabel = !!patient.person
      ? `${patient.person.display} (${patient.person.age}/${patient.person.gender})`
      : '';
    return this.renderCrumb(PATIENT_DASHBOARD_ROUTE(patientUuid), patientLabel, true)
  }

  getManageVisitsCrumb = (path: string, pattern: UrlPattern) => {
    const match = pattern.match(path.toLowerCase());
    return this.renderCrumb(
      MANAGE_VISITS_ROUTE(match.patientUuid),
      getIntl().formatMessage({ id: 'VISITS_MANAGE_VISITS_BREADCRUMB', defaultMessage: Default.MANAGE_VISITS_BREADCRUMB })
    );
  }

  getOverviewVisitCrumbs = (): Array<ReactFragment> => [
    this.renderLastCrumb(getIntl().formatMessage({ id: 'VISITS_OVERVIEW_VISIT_BREADCRUMB', defaultMessage: Default.OVERVIEW_VISIT_BREADCRUMB }))
  ];

  getScheduleVisitCrumbs = (path: string): Array<ReactFragment> => [
    this.getPatientLabelCrumb(path, SCHEDULE_VISIT_PATTERN),
    this.getManageVisitsCrumb(path, SCHEDULE_VISIT_PATTERN),
    this.renderLastCrumb(getIntl().formatMessage({ id: 'VISITS_SCHEDULE_VISIT_BREADCRUMB', defaultMessage: Default.SCHEDULE_VISIT_BREADCRUMB }))
  ];

  getEditVisitCrumbs = (path: string): Array<ReactFragment> => [
    this.getPatientLabelCrumb(path, EDIT_VISIT_PATTERN),
    this.getManageVisitsCrumb(path, EDIT_VISIT_PATTERN),
    this.renderLastCrumb(getIntl().formatMessage({ id: 'VISITS_EDIT_VISIT_BREADCRUMB', defaultMessage: Default.EDIT_VISIT_BREADCRUMB }))
  ];

  getManageVisitCrumbs = (path: string): Array<ReactFragment> => [
    this.getPatientLabelCrumb(path, MANAGE_VISIT_PATTERN),
    this.renderLastCrumb(getIntl().formatMessage({ id: 'VISITS_MANAGE_VISITS_BREADCRUMB', defaultMessage: Default.MANAGE_VISITS_BREADCRUMB }))
  ];

  renderCrumbs = (elements: Array<any>) => {
    const delimiter = this.renderDelimiter();
    const lastElementId = elements.length - 1;
    return (
      <React.Fragment>
        {this.renderHomeCrumb()}
        { (0 < elements.length) && delimiter}
        {elements.map((e, i) =>
          <React.Fragment key={`crumb-${i}`}>
            {e}
            {i !== lastElementId && delimiter}
          </React.Fragment>)}
      </React.Fragment>
    );
  }

  renderDelimiter = () =>
    <span className="breadcrumb-link-item breadcrumb-delimiter">
      <FontAwesomeIcon size="xs" icon={['fas', 'chevron-right']} />
    </span>

  renderHomeCrumb = () =>
    <a href={OMRS_ROUTE} className="breadcrumb-link-item home-crumb">
      <FontAwesomeIcon icon={['fas', 'home']} />
    </a>

  renderCrumb = (link: string, txt: string, isAbsolute?: boolean) => {
    if (isAbsolute) {
      return (
        <a href={link} className="breadcrumb-link-item" >{txt}</a>
      );
    } else {
      return <Link to={link} className="breadcrumb-link-item">{txt}</Link>;
    }
  }

  renderLastCrumb = (txt: string) =>
    <span className="breadcrumb-last-item">{txt}</span>

  render = () => this.buildBreadCrumb();
}

const mapStateToProps = ({ patient }: IRootState) => ({
  patient: patient.patient
});

const mapDispatchToProps = ({
  getPatient
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default withRouter(connect(
  mapStateToProps,
  mapDispatchToProps
)(BreadCrumb));
