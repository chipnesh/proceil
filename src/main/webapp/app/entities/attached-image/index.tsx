import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import AttachedImage from './attached-image';
import AttachedImageDetail from './attached-image-detail';
import AttachedImageUpdate from './attached-image-update';
import AttachedImageDeleteDialog from './attached-image-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={AttachedImageUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={AttachedImageUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={AttachedImageDetail} />
      <ErrorBoundaryRoute path={match.url} component={AttachedImage} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={AttachedImageDeleteDialog} />
  </>
);

export default Routes;
