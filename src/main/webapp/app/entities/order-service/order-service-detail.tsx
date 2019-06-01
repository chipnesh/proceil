import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './order-service.reducer';
import { IOrderService } from 'app/shared/model/order-service.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IOrderServiceDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class OrderServiceDetail extends React.Component<IOrderServiceDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { orderServiceEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="proceilApp.orderService.detail.title">OrderService</Translate> [<b>{orderServiceEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="serviceSummary">
                <Translate contentKey="proceilApp.orderService.serviceSummary">Service Summary</Translate>
              </span>
            </dt>
            <dd>{orderServiceEntity.serviceSummary}</dd>
            <dt>
              <span id="createdDate">
                <Translate contentKey="proceilApp.orderService.createdDate">Created Date</Translate>
              </span>
            </dt>
            <dd>
              <TextFormat value={orderServiceEntity.createdDate} type="date" format={APP_DATE_FORMAT} />
            </dd>
            <dt>
              <span id="serviceDate">
                <Translate contentKey="proceilApp.orderService.serviceDate">Service Date</Translate>
              </span>
            </dt>
            <dd>
              <TextFormat value={orderServiceEntity.serviceDate} type="date" format={APP_DATE_FORMAT} />
            </dd>
            <dt>
              <Translate contentKey="proceilApp.orderService.quota">Quota</Translate>
            </dt>
            <dd>{orderServiceEntity.quotaQuotaStatus ? orderServiceEntity.quotaQuotaStatus : ''}</dd>
            <dt>
              <Translate contentKey="proceilApp.orderService.executor">Executor</Translate>
            </dt>
            <dd>{orderServiceEntity.executorEmployeeName ? orderServiceEntity.executorEmployeeName : ''}</dd>
            <dt>
              <Translate contentKey="proceilApp.orderService.order">Order</Translate>
            </dt>
            <dd>{orderServiceEntity.orderOrderSummary ? orderServiceEntity.orderOrderSummary : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/order-service" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/order-service/${orderServiceEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ orderService }: IRootState) => ({
  orderServiceEntity: orderService.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(OrderServiceDetail);
