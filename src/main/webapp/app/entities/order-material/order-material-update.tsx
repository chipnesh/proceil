import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IMaterialReserve } from 'app/shared/model/material-reserve.model';
import { getEntities as getMaterialReserves } from 'app/entities/material-reserve/material-reserve.reducer';
import { ICustomerOrder } from 'app/shared/model/customer-order.model';
import { getEntities as getCustomerOrders } from 'app/entities/customer-order/customer-order.reducer';
import { getEntity, updateEntity, createEntity, reset } from './order-material.reducer';
import { IOrderMaterial } from 'app/shared/model/order-material.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IOrderMaterialUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IOrderMaterialUpdateState {
  isNew: boolean;
  reserveId: string;
  orderId: string;
}

export class OrderMaterialUpdate extends React.Component<IOrderMaterialUpdateProps, IOrderMaterialUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      reserveId: '0',
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

    this.props.getMaterialReserves();
    this.props.getCustomerOrders();
  }

  saveEntity = (event, errors, values) => {
    values.createdDate = convertDateTimeToServer(values.createdDate);

    if (errors.length === 0) {
      const { orderMaterialEntity } = this.props;
      const entity = {
        ...orderMaterialEntity,
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
    this.props.history.push('/entity/order-material');
  };

  render() {
    const { orderMaterialEntity, materialReserves, customerOrders, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="proceilApp.orderMaterial.home.createOrEditLabel">
              <Translate contentKey="proceilApp.orderMaterial.home.createOrEditLabel">Create or edit a OrderMaterial</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : orderMaterialEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="order-material-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="order-material-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="materialSummaryLabel" for="order-material-materialSummary">
                    <Translate contentKey="proceilApp.orderMaterial.materialSummary">Material Summary</Translate>
                  </Label>
                  <AvField id="order-material-materialSummary" type="text" name="materialSummary" />
                </AvGroup>
                <AvGroup>
                  <Label id="createdDateLabel" for="order-material-createdDate">
                    <Translate contentKey="proceilApp.orderMaterial.createdDate">Created Date</Translate>
                  </Label>
                  <AvInput
                    id="order-material-createdDate"
                    type="datetime-local"
                    className="form-control"
                    name="createdDate"
                    placeholder={'YYYY-MM-DD HH:mm'}
                    value={isNew ? null : convertDateTimeFromServer(this.props.orderMaterialEntity.createdDate)}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="materialQuantityLabel" for="order-material-materialQuantity">
                    <Translate contentKey="proceilApp.orderMaterial.materialQuantity">Material Quantity</Translate>
                  </Label>
                  <AvField
                    id="order-material-materialQuantity"
                    type="string"
                    className="form-control"
                    name="materialQuantity"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      min: { value: 0, errorMessage: translate('entity.validation.min', { min: 0 }) },
                      number: { value: true, errorMessage: translate('entity.validation.number') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="measureUnitLabel" for="order-material-measureUnit">
                    <Translate contentKey="proceilApp.orderMaterial.measureUnit">Measure Unit</Translate>
                  </Label>
                  <AvInput
                    id="order-material-measureUnit"
                    type="select"
                    className="form-control"
                    name="measureUnit"
                    value={(!isNew && orderMaterialEntity.measureUnit) || 'METER'}
                  >
                    <option value="METER">
                      {translate('proceilApp.MeasureUnit.METER')}
                    </option>
                    <option value="SQUARE_METER">
                      {translate('proceilApp.MeasureUnit.SQUARE_METER')}
                    </option>
                    <option value="KILO">
                      {translate('proceilApp.MeasureUnit.KILO')}
                    </option>
                    <option value="LITRES">
                      {translate('proceilApp.MeasureUnit.LITRES')}
                    </option>
                    <option value="QANTITY">
                      {translate('proceilApp.MeasureUnit.QANTITY')}
                    </option>
                    <option value="BOX">
                      {translate('proceilApp.MeasureUnit.BOX')}
                    </option>
                    <option value="SET">
                      {translate('proceilApp.MeasureUnit.SET')}
                    </option>
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="order-material-reserve">
                    <Translate contentKey="proceilApp.orderMaterial.reserve">Reserve</Translate>
                  </Label>
                  <AvInput id="order-material-reserve" type="select" className="form-control" name="reserveId">
                    <option value="" key="0" />
                    {materialReserves
                      ? materialReserves.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.reserveStatus}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="order-material-order">
                    <Translate contentKey="proceilApp.orderMaterial.order">Order</Translate>
                  </Label>
                  <AvInput id="order-material-order" type="select" className="form-control" name="orderId">
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
                <Button tag={Link} id="cancel-save" to="/entity/order-material" replace color="info">
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
  materialReserves: storeState.materialReserve.entities,
  customerOrders: storeState.customerOrder.entities,
  orderMaterialEntity: storeState.orderMaterial.entity,
  loading: storeState.orderMaterial.loading,
  updating: storeState.orderMaterial.updating,
  updateSuccess: storeState.orderMaterial.updateSuccess
});

const mapDispatchToProps = {
  getMaterialReserves,
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
)(OrderMaterialUpdate);
