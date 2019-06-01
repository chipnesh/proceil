import { Moment } from 'moment';
import { IMaterialMeasurement } from 'app/shared/model/material-measurement.model';

export interface IMeasurement {
  id?: number;
  measurementSummary?: string;
  measureDate?: Moment;
  measureNote?: any;
  measureAddress?: string;
  materials?: IMaterialMeasurement[];
  workerEmployeeName?: string;
  workerId?: number;
  clientCustomerSummary?: string;
  clientId?: number;
}

export const defaultValue: Readonly<IMeasurement> = {};
