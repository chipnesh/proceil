import React from 'react';
import { Switch } from 'react-router-dom';

// tslint:disable-next-line:no-unused-variable
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import AttachedImage from './attached-image';
import Material from './material';
import Service from './service';
import MaterialRequest from './material-request';
import MaterialArrival from './material-arrival';
import Facility from './facility';
import Zone from './zone';
import MaterialReserve from './material-reserve';
import ServiceQuota from './service-quota';
import MaterialAvailability from './material-availability';
import ServiceAvailability from './service-availability';
import Customer from './customer';
import Feedback from './feedback';
import Employee from './employee';
import MaterialMeasurement from './material-measurement';
import Measurement from './measurement';
import CustomerOrder from './customer-order';
import OrderMaterial from './order-material';
import OrderService from './order-service';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <ErrorBoundaryRoute path={`${match.url}/attached-image`} component={AttachedImage} />
      <ErrorBoundaryRoute path={`${match.url}/material`} component={Material} />
      <ErrorBoundaryRoute path={`${match.url}/service`} component={Service} />
      <ErrorBoundaryRoute path={`${match.url}/material-request`} component={MaterialRequest} />
      <ErrorBoundaryRoute path={`${match.url}/material-arrival`} component={MaterialArrival} />
      <ErrorBoundaryRoute path={`${match.url}/facility`} component={Facility} />
      <ErrorBoundaryRoute path={`${match.url}/zone`} component={Zone} />
      <ErrorBoundaryRoute path={`${match.url}/material-reserve`} component={MaterialReserve} />
      <ErrorBoundaryRoute path={`${match.url}/service-quota`} component={ServiceQuota} />
      <ErrorBoundaryRoute path={`${match.url}/material-availability`} component={MaterialAvailability} />
      <ErrorBoundaryRoute path={`${match.url}/service-availability`} component={ServiceAvailability} />
      <ErrorBoundaryRoute path={`${match.url}/customer`} component={Customer} />
      <ErrorBoundaryRoute path={`${match.url}/feedback`} component={Feedback} />
      <ErrorBoundaryRoute path={`${match.url}/employee`} component={Employee} />
      <ErrorBoundaryRoute path={`${match.url}/material-measurement`} component={MaterialMeasurement} />
      <ErrorBoundaryRoute path={`${match.url}/measurement`} component={Measurement} />
      <ErrorBoundaryRoute path={`${match.url}/customer-order`} component={CustomerOrder} />
      <ErrorBoundaryRoute path={`${match.url}/order-material`} component={OrderMaterial} />
      <ErrorBoundaryRoute path={`${match.url}/order-service`} component={OrderService} />
      {/* jhipster-needle-add-route-path - JHipster will add routes here */}
    </Switch>
  </div>
);

export default Routes;
