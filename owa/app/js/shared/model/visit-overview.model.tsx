import { Moment } from 'moment';
import INameUrl from './name-url.model';

export default interface IVisitOverview {
  uuid: string;
  patientIdentifier: string;
  nameUrl: INameUrl;
  startDate: Date;
  time: string | null;
  type: string;
  status: string | null;
  location: string | null;
  actualDate?: Date;
}
