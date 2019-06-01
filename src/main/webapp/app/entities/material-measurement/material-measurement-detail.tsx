import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './material-measurement.reducer';
import { IMaterialMeasurement } from 'app/shared/model/material-measurement.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IMaterialMeasurementDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class MaterialMeasurementDetail extends React.Component<IMaterialMeasurementDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { materialMeasurementEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="proceilApp.materialMeasurement.detail.title">MaterialMeasurement</Translate> [
            <b>{materialMeasurementEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="measurementSummary">
                <Translate contentKey="proceilApp.materialMeasurement.measurementSummary">Measurement Summary</Translate>
              </span>
            </dt>
            <dd>{materialMeasurementEntity.measurementSummary}</dd>
            <dt>
              <span id="measurementValue">
                <Translate contentKey="proceilApp.materialMeasurement.measurementValue">Measurement Value</Translate>
              </span>
            </dt>
            <dd>{materialMeasurementEntity.measurementValue}</dd>
            <dt>
              <span id="measureUnit">
                <Translate contentKey="proceilApp.materialMeasurement.measureUnit">Measure Unit</Translate>
              </span>
            </dt>
            <dd>{materialMeasurementEntity.measureUnit}</dd>
            <dt>
              <Translate contentKey="proceilApp.materialMeasurement.material">Material</Translate>
            </dt>
            <dd>{materialMeasurementEntity.materialMaterialName ? materialMeasurementEntity.materialMaterialName : ''}</dd>
            <dt>
              <Translate contentKey="proceilApp.materialMeasurement.measurement">Measurement</Translate>
            </dt>
            <dd>
              {materialMeasurementEntity.measurementMeasurementSummary ? materialMeasurementEntity.measurementMeasurementSummary : ''}
            </dd>
          </dl>
          <Button tag={Link} to="/entity/material-measurement" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/material-measurement/${materialMeasurementEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ materialMeasurement }: IRootState) => ({
  materialMeasurementEntity: materialMeasurement.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(MaterialMeasurementDetail);
