import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ServiceAvailability from './service-availability';
import ServiceAvailabilityDetail from './service-availability-detail';
import ServiceAvailabilityUpdate from './service-availability-update';
import ServiceAvailabilityDeleteDialog from './service-availability-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ServiceAvailabilityUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ServiceAvailabilityUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ServiceAvailabilityDetail} />
      <ErrorBoundaryRoute path={match.url} component={ServiceAvailability} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={ServiceAvailabilityDeleteDialog} />
  </>
);

export default Routes;
