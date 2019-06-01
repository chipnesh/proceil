import { IAttachedImage } from 'app/shared/model/attached-image.model';

export interface IService {
  id?: number;
  serviceName?: string;
  serviceDescription?: string;
  servicePrice?: number;
  images?: IAttachedImage[];
}

export const defaultValue: Readonly<IService> = {};
