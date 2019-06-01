import { IMeasurement } from 'app/shared/model/measurement.model';

export interface IEmployee {
  id?: number;
  employeeName?: string;
  phone?: string;
  measurements?: IMeasurement[];
}

export const defaultValue: Readonly<IEmployee> = {};
