import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Col, Row, Table } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import {
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
import { getEntities } from './material-reserve.reducer';
import { IMaterialReserve } from 'app/shared/model/material-reserve.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';

export interface IMaterialReserveProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export type IMaterialReserveState = IPaginationBaseState;

export class MaterialReserve extends React.Component<IMaterialReserveProps, IMaterialReserveState> {
  state: IMaterialReserveState = {
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
    const { materialReserveList, match, totalItems } = this.props;
    return (
      <div>
        <h2 id="material-reserve-heading">
          <Translate contentKey="proceilApp.materialReserve.home.title">Material Reserves</Translate>
          <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="proceilApp.materialReserve.home.createLabel">Create new Material Reserve</Translate>
          </Link>
        </h2>
        <div className="table-responsive">
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={this.sort('id')}>
                  <Translate contentKey="global.field.id">ID</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('reserveDate')}>
                  <Translate contentKey="proceilApp.materialReserve.reserveDate">Reserve Date</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('reserveStatus')}>
                  <Translate contentKey="proceilApp.materialReserve.reserveStatus">Reserve Status</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('quantityToReserve')}>
                  <Translate contentKey="proceilApp.materialReserve.quantityToReserve">Quantity To Reserve</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('measureUnit')}>
                  <Translate contentKey="proceilApp.materialReserve.measureUnit">Measure Unit</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="proceilApp.materialReserve.material">Material</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {materialReserveList.map((materialReserve, i) => (
                <tr key={`entity-${i}`}>
                  <td>
                    <Button tag={Link} to={`${match.url}/${materialReserve.id}`} color="link" size="sm">
                      {materialReserve.id}
                    </Button>
                  </td>
                  <td>
                    <TextFormat blankOnInvalid type="date" value={materialReserve.reserveDate} format={APP_DATE_FORMAT} />
                  </td>
                  <td>
                    <Translate contentKey={`proceilApp.MaterialReserveStatus.${materialReserve.reserveStatus}`} />
                  </td>
                  <td>{materialReserve.quantityToReserve}</td>
                  <td>
                    <Translate contentKey={`proceilApp.MeasureUnit.${materialReserve.measureUnit}`} />
                  </td>
                  <td>
                    {materialReserve.materialMaterialName ? (
                      <Link to={`material/${materialReserve.materialId}`}>{materialReserve.materialMaterialName}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-right">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${materialReserve.id}`} color="info" size="sm">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${materialReserve.id}/edit`} color="primary" size="sm">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${materialReserve.id}/delete`} color="danger" size="sm">
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

const mapStateToProps = ({ materialReserve }: IRootState) => ({
  materialReserveList: materialReserve.entities,
  totalItems: materialReserve.totalItems
});

const mapDispatchToProps = {
  getEntities
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(MaterialReserve);
