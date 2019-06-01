import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import OrderMaterial from './order-material';
import OrderMaterialDetail from './order-material-detail';
import OrderMaterialUpdate from './order-material-update';
import OrderMaterialDeleteDialog from './order-material-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={OrderMaterialUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={OrderMaterialUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={OrderMaterialDetail} />
      <ErrorBoundaryRoute path={match.url} component={OrderMaterial} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={OrderMaterialDeleteDialog} />
  </>
);

export default Routes;
