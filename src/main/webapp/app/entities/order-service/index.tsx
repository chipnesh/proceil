import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import OrderService from './order-service';
import OrderServiceDetail from './order-service-detail';
import OrderServiceUpdate from './order-service-update';
import OrderServiceDeleteDialog from './order-service-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={OrderServiceUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={OrderServiceUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={OrderServiceDetail} />
      <ErrorBoundaryRoute path={match.url} component={OrderService} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={OrderServiceDeleteDialog} />
  </>
);

export default Routes;
