import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Col, Row, Table } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAllAction, getSortState, IPaginationBaseState, getPaginationItemsNumber, JhiPagination } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntities } from './material-availability.reducer';
import { IMaterialAvailability } from 'app/shared/model/material-availability.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';

export interface IMaterialAvailabilityProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export type IMaterialAvailabilityState = IPaginationBaseState;

export class MaterialAvailability extends React.Component<IMaterialAvailabilityProps, IMaterialAvailabilityState> {
  state: IMaterialAvailabilityState = {
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
    const { materialAvailabilityList, match, totalItems } = this.props;
    return (
      <div>
        <h2 id="material-availability-heading">
          <Translate contentKey="proceilApp.materialAvailability.home.title">Material Availabilities</Translate>
          <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="proceilApp.materialAvailability.home.createLabel">Create new Material Availability</Translate>
          </Link>
        </h2>
        <div className="table-responsive">
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={this.sort('id')}>
                  <Translate contentKey="global.field.id">ID</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('availabilitySummary')}>
                  <Translate contentKey="proceilApp.materialAvailability.availabilitySummary">Availability Summary</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('remainingQuantity')}>
                  <Translate contentKey="proceilApp.materialAvailability.remainingQuantity">Remaining Quantity</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('measureUnit')}>
                  <Translate contentKey="proceilApp.materialAvailability.measureUnit">Measure Unit</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="proceilApp.materialAvailability.material">Material</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="proceilApp.materialAvailability.availableAt">Available At</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {materialAvailabilityList.map((materialAvailability, i) => (
                <tr key={`entity-${i}`}>
                  <td>
                    <Button tag={Link} to={`${match.url}/${materialAvailability.id}`} color="link" size="sm">
                      {materialAvailability.id}
                    </Button>
                  </td>
                  <td>{materialAvailability.availabilitySummary}</td>
                  <td>{materialAvailability.remainingQuantity}</td>
                  <td>
                    <Translate contentKey={`proceilApp.MeasureUnit.${materialAvailability.measureUnit}`} />
                  </td>
                  <td>
                    {materialAvailability.materialMaterialName ? (
                      <Link to={`material/${materialAvailability.materialId}`}>{materialAvailability.materialMaterialName}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {materialAvailability.availableAtZoneName ? (
                      <Link to={`zone/${materialAvailability.availableAtId}`}>{materialAvailability.availableAtZoneName}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-right">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${materialAvailability.id}`} color="info" size="sm">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${materialAvailability.id}/edit`} color="primary" size="sm">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${materialAvailability.id}/delete`} color="danger" size="sm">
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

const mapStateToProps = ({ materialAvailability }: IRootState) => ({
  materialAvailabilityList: materialAvailability.entities,
  totalItems: materialAvailability.totalItems
});

const mapDispatchToProps = {
  getEntities
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(MaterialAvailability);
