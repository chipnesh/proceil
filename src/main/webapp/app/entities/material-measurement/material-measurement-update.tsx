import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IMaterial } from 'app/shared/model/material.model';
import { getEntities as getMaterials } from 'app/entities/material/material.reducer';
import { IMeasurement } from 'app/shared/model/measurement.model';
import { getEntities as getMeasurements } from 'app/entities/measurement/measurement.reducer';
import { getEntity, updateEntity, createEntity, reset } from './material-measurement.reducer';
import { IMaterialMeasurement } from 'app/shared/model/material-measurement.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IMaterialMeasurementUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IMaterialMeasurementUpdateState {
  isNew: boolean;
  materialId: string;
  measurementId: string;
}

export class MaterialMeasurementUpdate extends React.Component<IMaterialMeasurementUpdateProps, IMaterialMeasurementUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      materialId: '0',
      measurementId: '0',
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

    this.props.getMaterials();
    this.props.getMeasurements();
  }

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { materialMeasurementEntity } = this.props;
      const entity = {
        ...materialMeasurementEntity,
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
    this.props.history.push('/entity/material-measurement');
  };

  render() {
    const { materialMeasurementEntity, materials, measurements, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="proceilApp.materialMeasurement.home.createOrEditLabel">
              <Translate contentKey="proceilApp.materialMeasurement.home.createOrEditLabel">Create or edit a MaterialMeasurement</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : materialMeasurementEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="material-measurement-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="material-measurement-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="measurementSummaryLabel" for="material-measurement-measurementSummary">
                    <Translate contentKey="proceilApp.materialMeasurement.measurementSummary">Measurement Summary</Translate>
                  </Label>
                  <AvField id="material-measurement-measurementSummary" type="text" name="measurementSummary" />
                </AvGroup>
                <AvGroup>
                  <Label id="measurementValueLabel" for="material-measurement-measurementValue">
                    <Translate contentKey="proceilApp.materialMeasurement.measurementValue">Measurement Value</Translate>
                  </Label>
                  <AvField id="material-measurement-measurementValue" type="string" className="form-control" name="measurementValue" />
                </AvGroup>
                <AvGroup>
                  <Label id="measureUnitLabel" for="material-measurement-measureUnit">
                    <Translate contentKey="proceilApp.materialMeasurement.measureUnit">Measure Unit</Translate>
                  </Label>
                  <AvInput
                    id="material-measurement-measureUnit"
                    type="select"
                    className="form-control"
                    name="measureUnit"
                    value={(!isNew && materialMeasurementEntity.measureUnit) || 'METER'}
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
                  <Label for="material-measurement-material">
                    <Translate contentKey="proceilApp.materialMeasurement.material">Material</Translate>
                  </Label>
                  <AvInput id="material-measurement-material" type="select" className="form-control" name="materialId">
                    <option value="" key="0" />
                    {materials
                      ? materials.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.materialName}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="material-measurement-measurement">
                    <Translate contentKey="proceilApp.materialMeasurement.measurement">Measurement</Translate>
                  </Label>
                  <AvInput id="material-measurement-measurement" type="select" className="form-control" name="measurementId">
                    <option value="" key="0" />
                    {measurements
                      ? measurements.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.measurementSummary}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/material-measurement" replace color="info">
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
  materials: storeState.material.entities,
  measurements: storeState.measurement.entities,
  materialMeasurementEntity: storeState.materialMeasurement.entity,
  loading: storeState.materialMeasurement.loading,
  updating: storeState.materialMeasurement.updating,
  updateSuccess: storeState.materialMeasurement.updateSuccess
});

const mapDispatchToProps = {
  getMaterials,
  getMeasurements,
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
)(MaterialMeasurementUpdate);
