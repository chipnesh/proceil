import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ServiceQuota from './service-quota';
import ServiceQuotaDetail from './service-quota-detail';
import ServiceQuotaUpdate from './service-quota-update';
import ServiceQuotaDeleteDialog from './service-quota-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ServiceQuotaUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ServiceQuotaUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ServiceQuotaDetail} />
      <ErrorBoundaryRoute path={match.url} component={ServiceQuota} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={ServiceQuotaDeleteDialog} />
  </>
);

export default Routes;
