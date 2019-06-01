import { Moment } from 'moment';
import { IOrderMaterial } from 'app/shared/model/order-material.model';
import { IOrderService } from 'app/shared/model/order-service.model';

export const enum OrderStatus {
  NEW = 'NEW',
  PENDING = 'PENDING',
  IN_PROGRESS = 'IN_PROGRESS',
  WAITING_AVAILABILITY = 'WAITING_AVAILABILITY',
  FULFILLED = 'FULFILLED',
  CANCELLED = 'CANCELLED',
  ABANDONED = 'ABANDONED'
}

export interface ICustomerOrder {
  id?: number;
  orderSummary?: string;
  createdDate?: Moment;
  deadlineDate?: Moment;
  orderStatus?: OrderStatus;
  orderPaid?: boolean;
  orderNote?: any;
  materials?: IOrderMaterial[];
  services?: IOrderService[];
  managerEmployeeName?: string;
  managerId?: number;
  customerCustomerSummary?: string;
  customerId?: number;
}

export const defaultValue: Readonly<ICustomerOrder> = {
  orderPaid: false
};
