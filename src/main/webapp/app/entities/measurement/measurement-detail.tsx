import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction, byteSize, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './measurement.reducer';
import { IMeasurement } from 'app/shared/model/measurement.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IMeasurementDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class MeasurementDetail extends React.Component<IMeasurementDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { measurementEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="proceilApp.measurement.detail.title">Measurement</Translate> [<b>{measurementEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="measurementSummary">
                <Translate contentKey="proceilApp.measurement.measurementSummary">Measurement Summary</Translate>
              </span>
            </dt>
            <dd>{measurementEntity.measurementSummary}</dd>
            <dt>
              <span id="measureDate">
                <Translate contentKey="proceilApp.measurement.measureDate">Measure Date</Translate>
              </span>
            </dt>
            <dd>
              <TextFormat value={measurementEntity.measureDate} type="date" format={APP_DATE_FORMAT} />
            </dd>
            <dt>
              <span id="measureNote">
                <Translate contentKey="proceilApp.measurement.measureNote">Measure Note</Translate>
              </span>
            </dt>
            <dd>{measurementEntity.measureNote}</dd>
            <dt>
              <span id="measureAddress">
                <Translate contentKey="proceilApp.measurement.measureAddress">Measure Address</Translate>
              </span>
            </dt>
            <dd>{measurementEntity.measureAddress}</dd>
            <dt>
              <Translate contentKey="proceilApp.measurement.worker">Worker</Translate>
            </dt>
            <dd>{measurementEntity.workerEmployeeName ? measurementEntity.workerEmployeeName : ''}</dd>
            <dt>
              <Translate contentKey="proceilApp.measurement.client">Client</Translate>
            </dt>
            <dd>{measurementEntity.clientCustomerSummary ? measurementEntity.clientCustomerSummary : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/measurement" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/measurement/${measurementEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ measurement }: IRootState) => ({
  measurementEntity: measurement.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(MeasurementDetail);
