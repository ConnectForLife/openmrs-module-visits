import { parse } from 'date-fns';
import moment from 'moment';

const TODAY = moment();
const WEEK = moment().add(6, 'days');
const MONTH = moment().add(1, 'months');
const CLEAR_DATE = null;

export function getCommaSeparatedDateString(date?: Date, defaultValue?: string) {
  if (!!date) {
    try {
      return new Date(date).toLocaleDateString().replace(/\//g, '.');
    } catch (e) {
      console.error(e);
      return defaultValue;
    }
  }
  return defaultValue;
}

export const formatDateIfDefined = (format: string, date?: Date) => {
  return !!date ? moment(date).format(format) : date;
}

export const isDateValid = (date?: Date) =>
  !!date && date instanceof Date && !isNaN(date.valueOf());

export const parseOrNow = (date?: string): Date => {
  const parsed = parse(date ? date : Date.now());
  return isDateValid(parsed) ? parsed : parse(Date.now());
}

export const getDatesByPeriod = {
  TODAY: () => ({
    dateFrom: TODAY,
    dateTo: TODAY
  }),
  WEEK: () => ({
    dateFrom: TODAY,
    dateTo: WEEK
  }),
  MONTH: () => ({
    dateFrom: TODAY,
    dateTo: MONTH
  }),
  ALL: () => ({
    dateFrom: CLEAR_DATE,
    dateTo: CLEAR_DATE
  }),
};
