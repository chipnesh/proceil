import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Col, Row, Table } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import {
  byteSize,
  Translate,
  ICrudGetAllAction,
  TextFormat,
  getSortState,
  IPaginationBaseState,
  getPaginationItemsNumber,
  JhiPagination
} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntities } from './material-request.reducer';
import { IMaterialRequest } from 'app/shared/model/material-request.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';

export interface IMaterialRequestProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export type IMaterialRequestState = IPaginationBaseState;

export class MaterialRequest extends React.Component<IMaterialRequestProps, IMaterialRequestState> {
  state: IMaterialRequestState = {
    ...getSortState(this.props.location, ITEMS_PER_PAGE)
  };

  componentDidMount() {
    this.getEntities();
  }

  sort = prop => () => {
    this.setState(
      {
        order: this.state.order === 'asc' ? 'desc' : 'asc',
        sort: prop
      },
      () => this.sortEntities()
    );
  };

  sortEntities() {
    this.getEntities();
    this.props.history.push(`${this.props.location.pathname}?page=${this.state.activePage}&sort=${this.state.sort},${this.state.order}`);
  }

  handlePagination = activePage => this.setState({ activePage }, () => this.sortEntities());

  getEntities = () => {
    const { activePage, itemsPerPage, sort, order } = this.state;
    this.props.getEntities(activePage - 1, itemsPerPage, `${sort},${order}`);
  };

  render() {
    const { materialRequestList, match, totalItems } = this.props;
    return (
      <div>
        <h2 id="material-request-heading">
          <Translate contentKey="proceilApp.materialRequest.home.title">Material Requests</Translate>
          <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="proceilApp.materialRequest.home.createLabel">Create new Material Request</Translate>
          </Link>
        </h2>
        <div className="table-responsive">
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={this.sort('id')}>
                  <Translate contentKey="global.field.id">ID</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('requestSummary')}>
                  <Translate contentKey="proceilApp.materialRequest.requestSummary">Request Summary</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('createdDate')}>
                  <Translate contentKey="proceilApp.materialRequest.createdDate">Created Date</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('closedDate')}>
                  <Translate contentKey="proceilApp.materialRequest.closedDate">Closed Date</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('requestNote')}>
                  <Translate contentKey="proceilApp.materialRequest.requestNote">Request Note</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('requestPriority')}>
                  <Translate contentKey="proceilApp.materialRequest.requestPriority">Request Priority</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('requestStatus')}>
                  <Translate contentKey="proceilApp.materialRequest.requestStatus">Request Status</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('requestedQuantity')}>
                  <Translate contentKey="proceilApp.materialRequest.requestedQuantity">Requested Quantity</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('measureUnit')}>
                  <Translate contentKey="proceilApp.materialRequest.measureUnit">Measure Unit</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="proceilApp.materialRequest.requester">Requester</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="proceilApp.materialRequest.material">Material</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {materialRequestList.map((materialRequest, i) => (
                <tr key={`entity-${i}`}>
                  <td>
                    <Button tag={Link} to={`${match.url}/${materialRequest.id}`} color="link" size="sm">
                      {materialRequest.id}
                    </Button>
                  </td>
                  <td>{materialRequest.requestSummary}</td>
                  <td>
                    <TextFormat type="date" value={materialRequest.createdDate} format={APP_DATE_FORMAT} />
                  </td>
                  <td>
                    <TextFormat type="date" value={materialRequest.closedDate} format={APP_DATE_FORMAT} />
                  </td>
                  <td>{materialRequest.requestNote}</td>
                  <td>{materialRequest.requestPriority}</td>
                  <td>
                    <Translate contentKey={`proceilApp.MaterialRequestStatus.${materialRequest.requestStatus}`} />
                  </td>
                  <td>{materialRequest.requestedQuantity}</td>
                  <td>
                    <Translate contentKey={`proceilApp.MeasureUnit.${materialRequest.measureUnit}`} />
                  </td>
                  <td>
                    {materialRequest.requesterFacilityName ? (
                      <Link to={`facility/${materialRequest.requesterId}`}>{materialRequest.requesterFacilityName}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {materialRequest.materialMaterialName ? (
                      <Link to={`material/${materialRequest.materialId}`}>{materialRequest.materialMaterialName}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-right">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${materialRequest.id}`} color="info" size="sm">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${materialRequest.id}/edit`} color="primary" size="sm">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${materialRequest.id}/delete`} color="danger" size="sm">
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        </div>
        <Row className="justify-content-center">
          <JhiPagination
            items={getPaginationItemsNumber(totalItems, this.state.itemsPerPage)}
            activePage={this.state.activePage}
            onSelect={this.handlePagination}
            maxButtons={5}
          />
        </Row>
      </div>
    );
  }
}

const mapStateToProps = ({ materialRequest }: IRootState) => ({
  materialRequestList: materialRequest.entities,
  totalItems: materialRequest.totalItems
});

const mapDispatchToProps = {
  getEntities
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(MaterialRequest);
