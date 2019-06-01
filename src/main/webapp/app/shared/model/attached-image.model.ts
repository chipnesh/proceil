export interface IAttachedImage {
  id?: number;
  imageName?: string;
  imageFileContentType?: string;
  imageFile?: any;
  materialId?: number;
  serviceId?: number;
}

export const defaultValue: Readonly<IAttachedImage> = {};
