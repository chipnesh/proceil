import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import MaterialRequest from './material-request';
import MaterialRequestDetail from './material-request-detail';
import MaterialRequestUpdate from './material-request-update';
import MaterialRequestDeleteDialog from './material-request-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={MaterialRequestUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={MaterialRequestUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={MaterialRequestDetail} />
      <ErrorBoundaryRoute path={match.url} component={MaterialRequest} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={MaterialRequestDeleteDialog} />
  </>
);

export default Routes;
