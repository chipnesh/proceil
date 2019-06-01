export const enum MeasureUnit {
  METER = 'METER',
  SQUARE_METER = 'SQUARE_METER',
  KILO = 'KILO',
  LITRES = 'LITRES',
  QANTITY = 'QANTITY',
  BOX = 'BOX',
  SET = 'SET'
}

export interface IMaterialAvailability {
  id?: number;
  availabilitySummary?: string;
  remainingQuantity?: number;
  measureUnit?: MeasureUnit;
  materialMaterialName?: string;
  materialId?: number;
  availableAtZoneName?: string;
  availableAtId?: number;
}

export const defaultValue: Readonly<IMaterialAvailability> = {};
