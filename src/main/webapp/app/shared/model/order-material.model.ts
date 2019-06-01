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

export interface IOrderMaterial {
  id?: number;
  materialSummary?: string;
  createdDate?: Moment;
  materialQuantity?: number;
  measureUnit?: MeasureUnit;
  reserveReserveStatus?: string;
  reserveId?: number;
  orderOrderSummary?: string;
  orderId?: number;
}

export const defaultValue: Readonly<IOrderMaterial> = {};
