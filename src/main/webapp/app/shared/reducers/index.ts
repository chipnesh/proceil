import { combineReducers } from 'redux';
import { loadingBarReducer as loadingBar } from 'react-redux-loading-bar';

import locale, { LocaleState } from './locale';
import authentication, { AuthenticationState } from './authentication';
import applicationProfile, { ApplicationProfileState } from './application-profile';

import administration, { AdministrationState } from 'app/modules/administration/administration.reducer';
import userManagement, { UserManagementState } from 'app/modules/administration/user-management/user-management.reducer';
import register, { RegisterState } from 'app/modules/account/register/register.reducer';
import activate, { ActivateState } from 'app/modules/account/activate/activate.reducer';
import password, { PasswordState } from 'app/modules/account/password/password.reducer';
import settings, { SettingsState } from 'app/modules/account/settings/settings.reducer';
import passwordReset, { PasswordResetState } from 'app/modules/account/password-reset/password-reset.reducer';
import sessions, { SessionsState } from 'app/modules/account/sessions/sessions.reducer';
// prettier-ignore
import attachedImage, {
  AttachedImageState
} from 'app/entities/attached-image/attached-image.reducer';
// prettier-ignore
import material, {
  MaterialState
} from 'app/entities/material/material.reducer';
// prettier-ignore
import service, {
  ServiceState
} from 'app/entities/service/service.reducer';
// prettier-ignore
import materialRequest, {
  MaterialRequestState
} from 'app/entities/material-request/material-request.reducer';
// prettier-ignore
import materialArrival, {
  MaterialArrivalState
} from 'app/entities/material-arrival/material-arrival.reducer';
// prettier-ignore
import facility, {
  FacilityState
} from 'app/entities/facility/facility.reducer';
// prettier-ignore
import zone, {
  ZoneState
} from 'app/entities/zone/zone.reducer';
// prettier-ignore
import materialReserve, {
  MaterialReserveState
} from 'app/entities/material-reserve/material-reserve.reducer';
// prettier-ignore
import serviceQuota, {
  ServiceQuotaState
} from 'app/entities/service-quota/service-quota.reducer';
// prettier-ignore
import materialAvailability, {
  MaterialAvailabilityState
} from 'app/entities/material-availability/material-availability.reducer';
// prettier-ignore
import serviceAvailability, {
  ServiceAvailabilityState
} from 'app/entities/service-availability/service-availability.reducer';
// prettier-ignore
import customer, {
  CustomerState
} from 'app/entities/customer/customer.reducer';
// prettier-ignore
import feedback, {
  FeedbackState
} from 'app/entities/feedback/feedback.reducer';
// prettier-ignore
import employee, {
  EmployeeState
} from 'app/entities/employee/employee.reducer';
// prettier-ignore
import materialMeasurement, {
  MaterialMeasurementState
} from 'app/entities/material-measurement/material-measurement.reducer';
// prettier-ignore
import measurement, {
  MeasurementState
} from 'app/entities/measurement/measurement.reducer';
// prettier-ignore
import customerOrder, {
  CustomerOrderState
} from 'app/entities/customer-order/customer-order.reducer';
// prettier-ignore
import orderMaterial, {
  OrderMaterialState
} from 'app/entities/order-material/order-material.reducer';
// prettier-ignore
import orderService, {
  OrderServiceState
} from 'app/entities/order-service/order-service.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

export interface IRootState {
  readonly authentication: AuthenticationState;
  readonly locale: LocaleState;
  readonly applicationProfile: ApplicationProfileState;
  readonly administration: AdministrationState;
  readonly userManagement: UserManagementState;
  readonly register: RegisterState;
  readonly activate: ActivateState;
  readonly passwordReset: PasswordResetState;
  readonly password: PasswordState;
  readonly settings: SettingsState;
  readonly sessions: SessionsState;
  readonly attachedImage: AttachedImageState;
  readonly material: MaterialState;
  readonly service: ServiceState;
  readonly materialRequest: MaterialRequestState;
  readonly materialArrival: MaterialArrivalState;
  readonly facility: FacilityState;
  readonly zone: ZoneState;
  readonly materialReserve: MaterialReserveState;
  readonly serviceQuota: ServiceQuotaState;
  readonly materialAvailability: MaterialAvailabilityState;
  readonly serviceAvailability: ServiceAvailabilityState;
  readonly customer: CustomerState;
  readonly feedback: FeedbackState;
  readonly employee: EmployeeState;
  readonly materialMeasurement: MaterialMeasurementState;
  readonly measurement: MeasurementState;
  readonly customerOrder: CustomerOrderState;
  readonly orderMaterial: OrderMaterialState;
  readonly orderService: OrderServiceState;
  /* jhipster-needle-add-reducer-type - JHipster will add reducer type here */
  readonly loadingBar: any;
}

const rootReducer = combineReducers<IRootState>({
  authentication,
  locale,
  applicationProfile,
  administration,
  userManagement,
  register,
  activate,
  passwordReset,
  password,
  settings,
  sessions,
  attachedImage,
  material,
  service,
  materialRequest,
  materialArrival,
  facility,
  zone,
  materialReserve,
  serviceQuota,
  materialAvailability,
  serviceAvailability,
  customer,
  feedback,
  employee,
  materialMeasurement,
  measurement,
  customerOrder,
  orderMaterial,
  orderService,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
  loadingBar
});

export default rootReducer;
