export const enum MeasureUnit {
  METER = 'METER',
  SQUARE_METER = 'SQUARE_METER',
  KILO = 'KILO',
  LITRES = 'LITRES',
  QANTITY = 'QANTITY',
  BOX = 'BOX',
  SET = 'SET'
}

export interface IMaterialMeasurement {
  id?: number;
  measurementSummary?: string;
  measurementValue?: number;
  measureUnit?: MeasureUnit;
  materialMaterialName?: string;
  materialId?: number;
  measurementMeasurementSummary?: string;
  measurementId?: number;
}

export const defaultValue: Readonly<IMaterialMeasurement> = {};
