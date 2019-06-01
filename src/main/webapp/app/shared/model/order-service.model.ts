import { Moment } from 'moment';

export interface IOrderService {
  id?: number;
  serviceSummary?: string;
  createdDate?: Moment;
  serviceDate?: Moment;
  quotaQuotaStatus?: string;
  quotaId?: number;
  executorEmployeeName?: string;
  executorId?: number;
  orderOrderSummary?: string;
  orderId?: number;
}

export const defaultValue: Readonly<IOrderService> = {};
