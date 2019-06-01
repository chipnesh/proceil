import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import MaterialMeasurement from './material-measurement';
import MaterialMeasurementDetail from './material-measurement-detail';
import MaterialMeasurementUpdate from './material-measurement-update';
import MaterialMeasurementDeleteDialog from './material-measurement-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={MaterialMeasurementUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={MaterialMeasurementUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={MaterialMeasurementDetail} />
      <ErrorBoundaryRoute path={match.url} component={MaterialMeasurement} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={MaterialMeasurementDeleteDialog} />
  </>
);

export default Routes;
