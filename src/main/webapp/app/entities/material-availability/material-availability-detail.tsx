import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './material-availability.reducer';
import { IMaterialAvailability } from 'app/shared/model/material-availability.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IMaterialAvailabilityDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class MaterialAvailabilityDetail extends React.Component<IMaterialAvailabilityDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { materialAvailabilityEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="proceilApp.materialAvailability.detail.title">MaterialAvailability</Translate> [
            <b>{materialAvailabilityEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="availabilitySummary">
                <Translate contentKey="proceilApp.materialAvailability.availabilitySummary">Availability Summary</Translate>
              </span>
            </dt>
            <dd>{materialAvailabilityEntity.availabilitySummary}</dd>
            <dt>
              <span id="remainingQuantity">
                <Translate contentKey="proceilApp.materialAvailability.remainingQuantity">Remaining Quantity</Translate>
              </span>
            </dt>
            <dd>{materialAvailabilityEntity.remainingQuantity}</dd>
            <dt>
              <span id="measureUnit">
                <Translate contentKey="proceilApp.materialAvailability.measureUnit">Measure Unit</Translate>
              </span>
            </dt>
            <dd>{materialAvailabilityEntity.measureUnit}</dd>
            <dt>
              <Translate contentKey="proceilApp.materialAvailability.material">Material</Translate>
            </dt>
            <dd>{materialAvailabilityEntity.materialMaterialName ? materialAvailabilityEntity.materialMaterialName : ''}</dd>
            <dt>
              <Translate contentKey="proceilApp.materialAvailability.availableAt">Available At</Translate>
            </dt>
            <dd>{materialAvailabilityEntity.availableAtZoneName ? materialAvailabilityEntity.availableAtZoneName : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/material-availability" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/material-availability/${materialAvailabilityEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ materialAvailability }: IRootState) => ({
  materialAvailabilityEntity: materialAvailability.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(MaterialAvailabilityDetail);
