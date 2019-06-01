import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Measurement from './measurement';
import MeasurementDetail from './measurement-detail';
import MeasurementUpdate from './measurement-update';
import MeasurementDeleteDialog from './measurement-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={MeasurementUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={MeasurementUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={MeasurementDetail} />
      <ErrorBoundaryRoute path={match.url} component={Measurement} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={MeasurementDeleteDialog} />
  </>
);

export default Routes;
