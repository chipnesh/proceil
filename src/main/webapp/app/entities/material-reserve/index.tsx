import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import MaterialReserve from './material-reserve';
import MaterialReserveDetail from './material-reserve-detail';
import MaterialReserveUpdate from './material-reserve-update';
import MaterialReserveDeleteDialog from './material-reserve-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={MaterialReserveUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={MaterialReserveUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={MaterialReserveDetail} />
      <ErrorBoundaryRoute path={match.url} component={MaterialReserve} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={MaterialReserveDeleteDialog} />
  </>
);

export default Routes;
