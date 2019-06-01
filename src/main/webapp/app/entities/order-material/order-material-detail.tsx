import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './order-material.reducer';
import { IOrderMaterial } from 'app/shared/model/order-material.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IOrderMaterialDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class OrderMaterialDetail extends React.Component<IOrderMaterialDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { orderMaterialEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="proceilApp.orderMaterial.detail.title">OrderMaterial</Translate> [<b>{orderMaterialEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="materialSummary">
                <Translate contentKey="proceilApp.orderMaterial.materialSummary">Material Summary</Translate>
              </span>
            </dt>
            <dd>{orderMaterialEntity.materialSummary}</dd>
            <dt>
              <span id="createdDate">
                <Translate contentKey="proceilApp.orderMaterial.createdDate">Created Date</Translate>
              </span>
            </dt>
            <dd>
              <TextFormat value={orderMaterialEntity.createdDate} type="date" format={APP_DATE_FORMAT} />
            </dd>
            <dt>
              <span id="materialQuantity">
                <Translate contentKey="proceilApp.orderMaterial.materialQuantity">Material Quantity</Translate>
              </span>
            </dt>
            <dd>{orderMaterialEntity.materialQuantity}</dd>
            <dt>
              <span id="measureUnit">
                <Translate contentKey="proceilApp.orderMaterial.measureUnit">Measure Unit</Translate>
              </span>
            </dt>
            <dd>{orderMaterialEntity.measureUnit}</dd>
            <dt>
              <Translate contentKey="proceilApp.orderMaterial.reserve">Reserve</Translate>
            </dt>
            <dd>{orderMaterialEntity.reserveReserveStatus ? orderMaterialEntity.reserveReserveStatus : ''}</dd>
            <dt>
              <Translate contentKey="proceilApp.orderMaterial.order">Order</Translate>
            </dt>
            <dd>{orderMaterialEntity.orderOrderSummary ? orderMaterialEntity.orderOrderSummary : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/order-material" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/order-material/${orderMaterialEntity.id}/edit`} replace color="primary">
            <FontAwesomeIcon icon="pencil-alt" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.edit">Edit</Translate>
            </span>
          </Button>
        </Col>
      </Row>
    );
  }
}

const mapStateToProps = ({ orderMaterial }: IRootState) => ({
  orderMaterialEntity: orderMaterial.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(OrderMaterialDetail);
