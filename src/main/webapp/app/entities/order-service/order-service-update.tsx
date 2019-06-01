import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IServiceQuota } from 'app/shared/model/service-quota.model';
import { getEntities as getServiceQuotas } from 'app/entities/service-quota/service-quota.reducer';
import { IEmployee } from 'app/shared/model/employee.model';
import { getEntities as getEmployees } from 'app/entities/employee/employee.reducer';
import { ICustomerOrder } from 'app/shared/model/customer-order.model';
import { getEntities as getCustomerOrders } from 'app/entities/customer-order/customer-order.reducer';
import { getEntity, updateEntity, createEntity, reset } from './order-service.reducer';
import { IOrderService } from 'app/shared/model/order-service.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IOrderServiceUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IOrderServiceUpdateState {
  isNew: boolean;
  quotaId: string;
  executorId: string;
  orderId: string;
}

export class OrderServiceUpdate extends React.Component<IOrderServiceUpdateProps, IOrderServiceUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      quotaId: '0',
      executorId: '0',
      orderId: '0',
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

    this.props.getServiceQuotas();
    this.props.getEmployees();
    this.props.getCustomerOrders();
  }

  saveEntity = (event, errors, values) => {
    values.createdDate = convertDateTimeToServer(values.createdDate);
    values.serviceDate = convertDateTimeToServer(values.serviceDate);

    if (errors.length === 0) {
      const { orderServiceEntity } = this.props;
      const entity = {
        ...orderServiceEntity,
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
    this.props.history.push('/entity/order-service');
  };

  render() {
    const { orderServiceEntity, serviceQuotas, employees, customerOrders, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="proceilApp.orderService.home.createOrEditLabel">
              <Translate contentKey="proceilApp.orderService.home.createOrEditLabel">Create or edit a OrderService</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : orderServiceEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="order-service-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="order-service-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="serviceSummaryLabel" for="order-service-serviceSummary">
                    <Translate contentKey="proceilApp.orderService.serviceSummary">Service Summary</Translate>
                  </Label>
                  <AvField id="order-service-serviceSummary" type="text" name="serviceSummary" />
                </AvGroup>
                <AvGroup>
                  <Label id="createdDateLabel" for="order-service-createdDate">
                    <Translate contentKey="proceilApp.orderService.createdDate">Created Date</Translate>
                  </Label>
                  <AvInput
                    id="order-service-createdDate"
                    type="datetime-local"
                    className="form-control"
                    name="createdDate"
                    placeholder={'YYYY-MM-DD HH:mm'}
                    value={isNew ? null : convertDateTimeFromServer(this.props.orderServiceEntity.createdDate)}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="serviceDateLabel" for="order-service-serviceDate">
                    <Translate contentKey="proceilApp.orderService.serviceDate">Service Date</Translate>
                  </Label>
                  <AvInput
                    id="order-service-serviceDate"
                    type="datetime-local"
                    className="form-control"
                    name="serviceDate"
                    placeholder={'YYYY-MM-DD HH:mm'}
                    value={isNew ? null : convertDateTimeFromServer(this.props.orderServiceEntity.serviceDate)}
                  />
                </AvGroup>
                <AvGroup>
                  <Label for="order-service-quota">
                    <Translate contentKey="proceilApp.orderService.quota">Quota</Translate>
                  </Label>
                  <AvInput id="order-service-quota" type="select" className="form-control" name="quotaId">
                    <option value="" key="0" />
                    {serviceQuotas
                      ? serviceQuotas.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.quotaStatus}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="order-service-executor">
                    <Translate contentKey="proceilApp.orderService.executor">Executor</Translate>
                  </Label>
                  <AvInput id="order-service-executor" type="select" className="form-control" name="executorId">
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
                  <Label for="order-service-order">
                    <Translate contentKey="proceilApp.orderService.order">Order</Translate>
                  </Label>
                  <AvInput id="order-service-order" type="select" className="form-control" name="orderId">
                    <option value="" key="0" />
                    {customerOrders
                      ? customerOrders.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.orderSummary}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/order-service" replace color="info">
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
  serviceQuotas: storeState.serviceQuota.entities,
  employees: storeState.employee.entities,
  customerOrders: storeState.customerOrder.entities,
  orderServiceEntity: storeState.orderService.entity,
  loading: storeState.orderService.loading,
  updating: storeState.orderService.updating,
  updateSuccess: storeState.orderService.updateSuccess
});

const mapDispatchToProps = {
  getServiceQuotas,
  getEmployees,
  getCustomerOrders,
  getEntity,
  updateEntity,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(OrderServiceUpdate);
