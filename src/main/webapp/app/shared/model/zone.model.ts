import { IMaterialAvailability } from 'app/shared/model/material-availability.model';
import { IServiceAvailability } from 'app/shared/model/service-availability.model';

export interface IZone {
  id?: number;
  zoneName?: string;
  materials?: IMaterialAvailability[];
  services?: IServiceAvailability[];
  facilityFacilityName?: string;
  facilityId?: number;
}

export const defaultValue: Readonly<IZone> = {};
