import { Moment } from 'moment';
import { IFeedback } from 'app/shared/model/feedback.model';
import { IMeasurement } from 'app/shared/model/measurement.model';
import { ICustomerOrder } from 'app/shared/model/customer-order.model';

export interface ICustomer {
  id?: number;
  customerSummary?: string;
  firstname?: string;
  lastname?: string;
  middlename?: string;
  birthDate?: Moment;
  email?: string;
  phone?: string;
  address?: string;
  feedbacks?: IFeedback[];
  measurements?: IMeasurement[];
  orders?: ICustomerOrder[];
}

export const defaultValue: Readonly<ICustomer> = {};
