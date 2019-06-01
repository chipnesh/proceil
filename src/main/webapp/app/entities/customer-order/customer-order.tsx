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
import { getEntities } from './customer-order.reducer';
import { ICustomerOrder } from 'app/shared/model/customer-order.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';

export interface ICustomerOrderProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export type ICustomerOrderState = IPaginationBaseState;

export class CustomerOrder extends React.Component<ICustomerOrderProps, ICustomerOrderState> {
  state: ICustomerOrderState = {
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
    const { customerOrderList, match, totalItems } = this.props;
    return (
      <div>
        <h2 id="customer-order-heading">
          <Translate contentKey="proceilApp.customerOrder.home.title">Customer Orders</Translate>
          <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="proceilApp.customerOrder.home.createLabel">Create new Customer Order</Translate>
          </Link>
        </h2>
        <div className="table-responsive">
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={this.sort('id')}>
                  <Translate contentKey="global.field.id">ID</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('orderSummary')}>
                  <Translate contentKey="proceilApp.customerOrder.orderSummary">Order Summary</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('createdDate')}>
                  <Translate contentKey="proceilApp.customerOrder.createdDate">Created Date</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('deadlineDate')}>
                  <Translate contentKey="proceilApp.customerOrder.deadlineDate">Deadline Date</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('orderStatus')}>
                  <Translate contentKey="proceilApp.customerOrder.orderStatus">Order Status</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('orderPaid')}>
                  <Translate contentKey="proceilApp.customerOrder.orderPaid">Order Paid</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('orderNote')}>
                  <Translate contentKey="proceilApp.customerOrder.orderNote">Order Note</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="proceilApp.customerOrder.manager">Manager</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="proceilApp.customerOrder.customer">Customer</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {customerOrderList.map((customerOrder, i) => (
                <tr key={`entity-${i}`}>
                  <td>
                    <Button tag={Link} to={`${match.url}/${customerOrder.id}`} color="link" size="sm">
                      {customerOrder.id}
                    </Button>
                  </td>
                  <td>{customerOrder.orderSummary}</td>
                  <td>
                    <TextFormat type="date" value={customerOrder.createdDate} format={APP_DATE_FORMAT} />
                  </td>
                  <td>
                    <TextFormat type="date" value={customerOrder.deadlineDate} format={APP_DATE_FORMAT} />
                  </td>
                  <td>
                    <Translate contentKey={`proceilApp.OrderStatus.${customerOrder.orderStatus}`} />
                  </td>
                  <td>{customerOrder.orderPaid ? 'true' : 'false'}</td>
                  <td>{customerOrder.orderNote}</td>
                  <td>
                    {customerOrder.managerEmployeeName ? (
                      <Link to={`employee/${customerOrder.managerId}`}>{customerOrder.managerEmployeeName}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {customerOrder.customerCustomerSummary ? (
                      <Link to={`customer/${customerOrder.customerId}`}>{customerOrder.customerCustomerSummary}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-right">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${customerOrder.id}`} color="info" size="sm">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${customerOrder.id}/edit`} color="primary" size="sm">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${customerOrder.id}/delete`} color="danger" size="sm">
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

const mapStateToProps = ({ customerOrder }: IRootState) => ({
  customerOrderList: customerOrder.entities,
  totalItems: customerOrder.totalItems
});

const mapDispatchToProps = {
  getEntities
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(CustomerOrder);
