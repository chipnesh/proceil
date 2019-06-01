import { IAttachedImage } from 'app/shared/model/attached-image.model';

export interface IMaterial {
  id?: number;
  materialName?: string;
  materialDescription?: string;
  materialPrice?: number;
  images?: IAttachedImage[];
}

export const defaultValue: Readonly<IMaterial> = {};
