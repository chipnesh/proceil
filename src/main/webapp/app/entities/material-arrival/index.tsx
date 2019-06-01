import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import MaterialArrival from './material-arrival';
import MaterialArrivalDetail from './material-arrival-detail';
import MaterialArrivalUpdate from './material-arrival-update';
import MaterialArrivalDeleteDialog from './material-arrival-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={MaterialArrivalUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={MaterialArrivalUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={MaterialArrivalDetail} />
      <ErrorBoundaryRoute path={match.url} component={MaterialArrival} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={MaterialArrivalDeleteDialog} />
  </>
);

export default Routes;
