import { Moment } from 'moment';

export const enum ServiceQuotingStatus {
  NEW = 'NEW',
  QUOTED = 'QUOTED',
  BUSY = 'BUSY'
}

export interface IServiceQuota {
  id?: number;
  dateFrom?: Moment;
  dateTo?: Moment;
  quotaStatus?: ServiceQuotingStatus;
  quantityToQuote?: number;
  serviceServiceName?: string;
  serviceId?: number;
}

export const defaultValue: Readonly<IServiceQuota> = {};
