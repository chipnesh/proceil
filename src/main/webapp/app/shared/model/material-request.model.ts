import { Moment } from 'moment';

export const enum MaterialRequestStatus {
  NEW = 'NEW',
  IN_PROGRESS = 'IN_PROGRESS',
  FINISHED = 'FINISHED'
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

export interface IMaterialRequest {
  id?: number;
  requestSummary?: string;
  createdDate?: Moment;
  closedDate?: Moment;
  requestNote?: any;
  requestPriority?: number;
  requestStatus?: MaterialRequestStatus;
  requestedQuantity?: number;
  measureUnit?: MeasureUnit;
  requesterFacilityName?: string;
  requesterId?: number;
  materialMaterialName?: string;
  materialId?: number;
}

export const defaultValue: Readonly<IMaterialRequest> = {};
