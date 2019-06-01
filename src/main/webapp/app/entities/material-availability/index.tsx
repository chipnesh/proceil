import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import MaterialAvailability from './material-availability';
import MaterialAvailabilityDetail from './material-availability-detail';
import MaterialAvailabilityUpdate from './material-availability-update';
import MaterialAvailabilityDeleteDialog from './material-availability-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={MaterialAvailabilityUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={MaterialAvailabilityUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={MaterialAvailabilityDetail} />
      <ErrorBoundaryRoute path={match.url} component={MaterialAvailability} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={MaterialAvailabilityDeleteDialog} />
  </>
);

export default Routes;
