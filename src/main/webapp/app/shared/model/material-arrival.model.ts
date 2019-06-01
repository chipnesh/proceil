import { Moment } from 'moment';

export const enum MeasureUnit {
  METER = 'METER',
  SQUARE_METER = 'SQUARE_METER',
  KILO = 'KILO',
  LITRES = 'LITRES',
  QANTITY = 'QANTITY',
  BOX = 'BOX',
  SET = 'SET'
}

export interface IMaterialArrival {
  id?: number;
  arrivalSummary?: string;
  arrivalDate?: Moment;
  arrivalNote?: any;
  arrivedQuantity?: number;
  measureUnit?: MeasureUnit;
  requestRequestSummary?: string;
  requestId?: number;
}

export const defaultValue: Readonly<IMaterialArrival> = {};
