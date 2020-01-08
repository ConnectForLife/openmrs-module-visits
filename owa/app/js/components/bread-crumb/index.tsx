
import React, { ReactFragment } from 'react';
import { connect } from 'react-redux';
import { Link, withRouter, RouteComponentProps } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import UrlPattern from 'url-pattern';
import './bread-crumb.scss';
import { UnregisterCallback } from 'history';
import * as Msg from '../../shared/utils/messages';
import { getPatient } from '../../reducers/patient.reducer';
import { IRootState } from '../../reducers';

const SCHEDULE_VISIT_PATTERN = new UrlPattern(`/visits/manage/schedule/:patientUuid*`);

const MODULE_ROUTE = '/';
const OMRS_ROUTE = '../../';
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

  componentWillUnmount = () => {
    this.unlisten();
  }

  render = () => {
    return this.buildBreadCrumb();
  }

  buildBreadCrumb = () => {
    const { current } = this.state;

    return (
      <div className="breadcrumb">
        {this.renderCrumbs(this.getCrumbs(current))}
      </div>
    );
  }

  getCrumbs = (path: string): Array<ReactFragment> => {
    if (!!SCHEDULE_VISIT_PATTERN.match(path.toLowerCase())) {
      return this.getScheduleVisitCrumbs(path);
    } else {
      return [this.renderLastCrumb(Msg.GENERAL_MODULE_BREADCRUMB)];
    }
  }

  getPatientNameCrumb = (path: string) => {
    const match = SCHEDULE_VISIT_PATTERN.match(path.toLowerCase());
    const patientUuid = match.patientUuid;

    if (this.props.patient.uuid != patientUuid) {
      this.props.getPatient(patientUuid);
    }

    const patientName = this.props.patient.person ? this.props.patient.person.display : '';
    return this.renderCrumb(PATIENT_DASHBOARD_ROUTE(patientUuid), patientName, true)
  }

  getScheduleVisitCrumbs = (path: string): Array<ReactFragment> => {
    return [
      this.getPatientNameCrumb(path),
      // it's considered as LastCrumb as the page doeas not exist yet so there should be no link 
      this.renderLastCrumb(Msg.MANAGE_VISITS_BREADCRUMB), 
      this.renderLastCrumb(Msg.SCHEDULE_VISIT_BREADCRUMB)
    ];
  }

  renderCrumbs = (elements: Array<any>) => {
    const delimiter = this.renderDelimiter();
    const lastElementId = elements.length - 1;
    return (
      <React.Fragment>
        {this.renderHomeCrumb()}
        {elements.map((e, i) =>
          <React.Fragment key={`crumb-${i}`}>
            {e}
            {i !== lastElementId && delimiter}
          </React.Fragment>)}
      </React.Fragment>
    );
  }

  renderDelimiter = () => {
    return (
      <span className="breadcrumb-link-item">
        <FontAwesomeIcon size="xs" icon={['fas', 'chevron-right']} />
      </span>);
  }

  renderHomeCrumb = () => {
    return (
      <a href={OMRS_ROUTE} className="breadcrumb-link-item">
        <FontAwesomeIcon icon={['fas', 'home']} />
      </a>);
  }

  renderCrumb = (link: string, txt: string, isAbsolute?: boolean) => {
    if (isAbsolute) {
      return (
        <a href={link} className="breadcrumb-link-item" >{txt}</a>
      );
    } else {
      return <Link to={link} className="breadcrumb-link-item">{txt}</Link>;
    }
  }

  renderLastCrumb = (txt: string) => {
    return <span className="breadcrumb-last-item">{txt}</span>;
  }
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
