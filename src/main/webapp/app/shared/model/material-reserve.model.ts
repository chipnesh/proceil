import { Moment } from 'moment';

export const enum MaterialReserveStatus {
  NEW = 'NEW',
  RESERVED = 'RESERVED',
  OUT_OF_STOCK = 'OUT_OF_STOCK'
}

export const enum MeasureUnit {
  METER = 'METER',
  SQUARE_METER = 'SQUARE_METER',
  KILO = 'KILO',
  LITRES = 'LITRES',
  QANTITY = 'QANTITY',
  BOX = 'BOX',
  SET = 'SET'
}

export interface IMaterialReserve {
  id?: number;
  reserveDate?: Moment;
  reserveStatus?: MaterialReserveStatus;
  quantityToReserve?: number;
  measureUnit?: MeasureUnit;
  materialMaterialName?: string;
  materialId?: number;
}

export const defaultValue: Readonly<IMaterialReserve> = {};
