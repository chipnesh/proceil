import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, setFileData, byteSize, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IEmployee } from 'app/shared/model/employee.model';
import { getEntities as getEmployees } from 'app/entities/employee/employee.reducer';
import { ICustomer } from 'app/shared/model/customer.model';
import { getEntities as getCustomers } from 'app/entities/customer/customer.reducer';
import { getEntity, updateEntity, createEntity, setBlob, reset } from './customer-order.reducer';
import { ICustomerOrder } from 'app/shared/model/customer-order.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface ICustomerOrderUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface ICustomerOrderUpdateState {
  isNew: boolean;
  managerId: string;
  customerId: string;
}

export class CustomerOrderUpdate extends React.Component<ICustomerOrderUpdateProps, ICustomerOrderUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      managerId: '0',
      customerId: '0',
      isNew: !this.props.match.params || !this.props.match.params.id
    };
  }

  componentWillUpdate(nextProps, nextState) {
    if (nextProps.updateSuccess !== this.props.updateSuccess && nextProps.updateSuccess) {
      this.handleClose();
    }
  }

  componentDidMount() {
    if (this.state.isNew) {
      this.props.reset();
    } else {
      this.props.getEntity(this.props.match.params.id);
    }

    this.props.getEmployees();
    this.props.getCustomers();
  }

  onBlobChange = (isAnImage, name) => event => {
    setFileData(event, (contentType, data) => this.props.setBlob(name, data, contentType), isAnImage);
  };

  clearBlob = name => () => {
    this.props.setBlob(name, undefined, undefined);
  };

  saveEntity = (event, errors, values) => {
    values.createdDate = convertDateTimeToServer(values.createdDate);
    values.deadlineDate = convertDateTimeToServer(values.deadlineDate);

    if (errors.length === 0) {
      const { customerOrderEntity } = this.props;
      const entity = {
        ...customerOrderEntity,
        ...values
      };

      if (this.state.isNew) {
        this.props.createEntity(entity);
      } else {
        this.props.updateEntity(entity);
      }
    }
  };

  handleClose = () => {
    this.props.history.push('/entity/customer-order');
  };

  render() {
    const { customerOrderEntity, employees, customers, loading, updating } = this.props;
    const { isNew } = this.state;

    const { orderNote } = customerOrderEntity;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="proceilApp.customerOrder.home.createOrEditLabel">
              <Translate contentKey="proceilApp.customerOrder.home.createOrEditLabel">Create or edit a CustomerOrder</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : customerOrderEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="customer-order-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="customer-order-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="orderSummaryLabel" for="customer-order-orderSummary">
                    <Translate contentKey="proceilApp.customerOrder.orderSummary">Order Summary</Translate>
                  </Label>
                  <AvField id="customer-order-orderSummary" type="text" name="orderSummary" />
                </AvGroup>
                <AvGroup>
                  <Label id="createdDateLabel" for="customer-order-createdDate">
                    <Translate contentKey="proceilApp.customerOrder.createdDate">Created Date</Translate>
                  </Label>
                  <AvInput
                    id="customer-order-createdDate"
                    type="datetime-local"
                    className="form-control"
                    name="createdDate"
                    placeholder={'YYYY-MM-DD HH:mm'}
                    value={isNew ? null : convertDateTimeFromServer(this.props.customerOrderEntity.createdDate)}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="deadlineDateLabel" for="customer-order-deadlineDate">
                    <Translate contentKey="proceilApp.customerOrder.deadlineDate">Deadline Date</Translate>
                  </Label>
                  <AvInput
                    id="customer-order-deadlineDate"
                    type="datetime-local"
                    className="form-control"
                    name="deadlineDate"
                    placeholder={'YYYY-MM-DD HH:mm'}
                    value={isNew ? null : convertDateTimeFromServer(this.props.customerOrderEntity.deadlineDate)}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="orderStatusLabel" for="customer-order-orderStatus">
                    <Translate contentKey="proceilApp.customerOrder.orderStatus">Order Status</Translate>
                  </Label>
                  <AvInput
                    id="customer-order-orderStatus"
                    type="select"
                    className="form-control"
                    name="orderStatus"
                    value={(!isNew && customerOrderEntity.orderStatus) || 'NEW'}
                  >
                    <option value="NEW">
                      {translate('proceilApp.OrderStatus.NEW')}
                    </option>
                    <option value="PENDING">
                      {translate('proceilApp.OrderStatus.PENDING')}
                    </option>
                    <option value="IN_PROGRESS">
                      {translate('proceilApp.OrderStatus.IN_PROGRESS')}
                    </option>
                    <option value="WAITING_AVAILABILITY">
                      {translate('proceilApp.OrderStatus.WAITING_AVAILABILITY')}
                    </option>
                    <option value="FULFILLED">
                      {translate('proceilApp.OrderStatus.FULFILLED')}
                    </option>
                    <option value="CANCELLED">
                      {translate('proceilApp.OrderStatus.CANCELLED')}
                    </option>
                    <option value="ABANDONED">
                      {translate('proceilApp.OrderStatus.ABANDONED')}
                    </option>
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label id="orderPaidLabel" check>
                    <AvInput id="customer-order-orderPaid" type="checkbox" className="form-control" name="orderPaid" />
                    <Translate contentKey="proceilApp.customerOrder.orderPaid">Order Paid</Translate>
                  </Label>
                </AvGroup>
                <AvGroup>
                  <Label id="orderNoteLabel" for="customer-order-orderNote">
                    <Translate contentKey="proceilApp.customerOrder.orderNote">Order Note</Translate>
                  </Label>
                  <AvInput id="customer-order-orderNote" type="textarea" name="orderNote" />
                </AvGroup>
                <AvGroup>
                  <Label for="customer-order-manager">
                    <Translate contentKey="proceilApp.customerOrder.manager">Manager</Translate>
                  </Label>
                  <AvInput id="customer-order-manager" type="select" className="form-control" name="managerId">
                    <option value="" key="0" />
                    {employees
                      ? employees.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.employeeName}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="customer-order-customer">
                    <Translate contentKey="proceilApp.customerOrder.customer">Customer</Translate>
                  </Label>
                  <AvInput id="customer-order-customer" type="select" className="form-control" name="customerId">
                    <option value="" key="0" />
                    {customers
                      ? customers.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.customerSummary}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/customer-order" replace color="info">
                  <FontAwesomeIcon icon="arrow-left" />
                  &nbsp;
                  <span className="d-none d-md-inline">
                    <Translate contentKey="entity.action.back">Back</Translate>
                  </span>
                </Button>
                &nbsp;
                <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                  <FontAwesomeIcon icon="save" />
                  &nbsp;
                  <Translate contentKey="entity.action.save">Save</Translate>
                </Button>
              </AvForm>
            )}
          </Col>
        </Row>
      </div>
    );
  }
}

const mapStateToProps = (storeState: IRootState) => ({
  employees: storeState.employee.entities,
  customers: storeState.customer.entities,
  customerOrderEntity: storeState.customerOrder.entity,
  loading: storeState.customerOrder.loading,
  updating: storeState.customerOrder.updating,
  updateSuccess: storeState.customerOrder.updateSuccess
});

const mapDispatchToProps = {
  getEmployees,
  getCustomers,
  getEntity,
  updateEntity,
  setBlob,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(CustomerOrderUpdate);
