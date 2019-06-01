import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './service-availability.reducer';
import { IServiceAvailability } from 'app/shared/model/service-availability.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IServiceAvailabilityDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class ServiceAvailabilityDetail extends React.Component<IServiceAvailabilityDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { serviceAvailabilityEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="proceilApp.serviceAvailability.detail.title">ServiceAvailability</Translate> [
            <b>{serviceAvailabilityEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="availabilitySummary">
                <Translate contentKey="proceilApp.serviceAvailability.availabilitySummary">Availability Summary</Translate>
              </span>
            </dt>
            <dd>{serviceAvailabilityEntity.availabilitySummary}</dd>
            <dt>
              <span id="dateFrom">
                <Translate contentKey="proceilApp.serviceAvailability.dateFrom">Date From</Translate>
              </span>
            </dt>
            <dd>
              <TextFormat value={serviceAvailabilityEntity.dateFrom} type="date" format={APP_DATE_FORMAT} />
            </dd>
            <dt>
              <span id="dateTo">
                <Translate contentKey="proceilApp.serviceAvailability.dateTo">Date To</Translate>
              </span>
            </dt>
            <dd>
              <TextFormat value={serviceAvailabilityEntity.dateTo} type="date" format={APP_DATE_FORMAT} />
            </dd>
            <dt>
              <span id="remainingQuotas">
                <Translate contentKey="proceilApp.serviceAvailability.remainingQuotas">Remaining Quotas</Translate>
              </span>
            </dt>
            <dd>{serviceAvailabilityEntity.remainingQuotas}</dd>
            <dt>
              <Translate contentKey="proceilApp.serviceAvailability.service">Service</Translate>
            </dt>
            <dd>{serviceAvailabilityEntity.serviceServiceName ? serviceAvailabilityEntity.serviceServiceName : ''}</dd>
            <dt>
              <Translate contentKey="proceilApp.serviceAvailability.providedBy">Provided By</Translate>
            </dt>
            <dd>{serviceAvailabilityEntity.providedByZoneName ? serviceAvailabilityEntity.providedByZoneName : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/service-availability" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/service-availability/${serviceAvailabilityEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ serviceAvailability }: IRootState) => ({
  serviceAvailabilityEntity: serviceAvailability.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ServiceAvailabilityDetail);
