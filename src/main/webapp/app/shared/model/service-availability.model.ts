import { Moment } from 'moment';

export interface IServiceAvailability {
  id?: number;
  availabilitySummary?: string;
  dateFrom?: Moment;
  dateTo?: Moment;
  remainingQuotas?: number;
  serviceServiceName?: string;
  serviceId?: number;
  providedByZoneName?: string;
  providedById?: number;
}

export const defaultValue: Readonly<IServiceAvailability> = {};
