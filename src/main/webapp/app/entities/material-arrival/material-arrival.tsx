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
import { getEntities } from './material-arrival.reducer';
import { IMaterialArrival } from 'app/shared/model/material-arrival.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';

export interface IMaterialArrivalProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export type IMaterialArrivalState = IPaginationBaseState;

export class MaterialArrival extends React.Component<IMaterialArrivalProps, IMaterialArrivalState> {
  state: IMaterialArrivalState = {
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
    const { materialArrivalList, match, totalItems } = this.props;
    return (
      <div>
        <h2 id="material-arrival-heading">
          <Translate contentKey="proceilApp.materialArrival.home.title">Material Arrivals</Translate>
          <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="proceilApp.materialArrival.home.createLabel">Create new Material Arrival</Translate>
          </Link>
        </h2>
        <div className="table-responsive">
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={this.sort('id')}>
                  <Translate contentKey="global.field.id">ID</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('arrivalSummary')}>
                  <Translate contentKey="proceilApp.materialArrival.arrivalSummary">Arrival Summary</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('arrivalDate')}>
                  <Translate contentKey="proceilApp.materialArrival.arrivalDate">Arrival Date</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('arrivalNote')}>
                  <Translate contentKey="proceilApp.materialArrival.arrivalNote">Arrival Note</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('arrivedQuantity')}>
                  <Translate contentKey="proceilApp.materialArrival.arrivedQuantity">Arrived Quantity</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('measureUnit')}>
                  <Translate contentKey="proceilApp.materialArrival.measureUnit">Measure Unit</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="proceilApp.materialArrival.request">Request</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {materialArrivalList.map((materialArrival, i) => (
                <tr key={`entity-${i}`}>
                  <td>
                    <Button tag={Link} to={`${match.url}/${materialArrival.id}`} color="link" size="sm">
                      {materialArrival.id}
                    </Button>
                  </td>
                  <td>{materialArrival.arrivalSummary}</td>
                  <td>
                    <TextFormat blankOnInvalid type="date" value={materialArrival.arrivalDate} format={APP_DATE_FORMAT} />
                  </td>
                  <td>{materialArrival.arrivalNote}</td>
                  <td>{materialArrival.arrivedQuantity}</td>
                  <td>
                    <Translate contentKey={`proceilApp.MeasureUnit.${materialArrival.measureUnit}`} />
                  </td>
                  <td>
                    {materialArrival.requestRequestSummary ? (
                      <Link to={`material-request/${materialArrival.requestId}`}>{materialArrival.requestRequestSummary}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-right">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${materialArrival.id}`} color="info" size="sm">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${materialArrival.id}/edit`} color="primary" size="sm">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${materialArrival.id}/delete`} color="danger" size="sm">
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

const mapStateToProps = ({ materialArrival }: IRootState) => ({
  materialArrivalList: materialArrival.entities,
  totalItems: materialArrival.totalItems
});

const mapDispatchToProps = {
  getEntities
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(MaterialArrival);
