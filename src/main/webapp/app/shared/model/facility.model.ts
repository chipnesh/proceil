import { IZone } from 'app/shared/model/zone.model';

export interface IFacility {
  id?: number;
  facilityName?: string;
  zones?: IZone[];
}

export const defaultValue: Readonly<IFacility> = {};
