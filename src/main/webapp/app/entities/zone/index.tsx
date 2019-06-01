import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Zone from './zone';
import ZoneDetail from './zone-detail';
import ZoneUpdate from './zone-update';
import ZoneDeleteDialog from './zone-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ZoneUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ZoneUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ZoneDetail} />
      <ErrorBoundaryRoute path={match.url} component={Zone} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={ZoneDeleteDialog} />
  </>
);

export default Routes;
