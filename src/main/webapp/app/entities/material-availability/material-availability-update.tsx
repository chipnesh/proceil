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
import { IZone } from 'app/shared/model/zone.model';
import { getEntities as getZones } from 'app/entities/zone/zone.reducer';
import { getEntity, updateEntity, createEntity, reset } from './material-availability.reducer';
import { IMaterialAvailability } from 'app/shared/model/material-availability.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IMaterialAvailabilityUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IMaterialAvailabilityUpdateState {
  isNew: boolean;
  materialId: string;
  availableAtId: string;
}

export class MaterialAvailabilityUpdate extends React.Component<IMaterialAvailabilityUpdateProps, IMaterialAvailabilityUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      materialId: '0',
      availableAtId: '0',
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
    this.props.getZones();
  }

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { materialAvailabilityEntity } = this.props;
      const entity = {
        ...materialAvailabilityEntity,
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
    this.props.history.push('/entity/material-availability');
  };

  render() {
    const { materialAvailabilityEntity, materials, zones, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="proceilApp.materialAvailability.home.createOrEditLabel">
              <Translate contentKey="proceilApp.materialAvailability.home.createOrEditLabel">
                Create or edit a MaterialAvailability
              </Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : materialAvailabilityEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="material-availability-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="material-availability-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="remainingQuantityLabel" for="material-availability-remainingQuantity">
                    <Translate contentKey="proceilApp.materialAvailability.remainingQuantity">Remaining Quantity</Translate>
                  </Label>
                  <AvField id="material-availability-remainingQuantity" type="string" className="form-control" name="remainingQuantity" />
                </AvGroup>
                <AvGroup>
                  <Label id="measureUnitLabel" for="material-availability-measureUnit">
                    <Translate contentKey="proceilApp.materialAvailability.measureUnit">Measure Unit</Translate>
                  </Label>
                  <AvInput
                    id="material-availability-measureUnit"
                    type="select"
                    className="form-control"
                    name="measureUnit"
                    value={(!isNew && materialAvailabilityEntity.measureUnit) || 'METER'}
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
                  <Label for="material-availability-material">
                    <Translate contentKey="proceilApp.materialAvailability.material">Material</Translate>
                  </Label>
                  <AvInput id="material-availability-material" type="select" className="form-control" name="materialId">
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
                  <Label for="material-availability-availableAt">
                    <Translate contentKey="proceilApp.materialAvailability.availableAt">Available At</Translate>
                  </Label>
                  <AvInput id="material-availability-availableAt" type="select" className="form-control" name="availableAtId">
                    <option value="" key="0" />
                    {zones
                      ? zones.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.zoneName}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/material-availability" replace color="info">
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
  zones: storeState.zone.entities,
  materialAvailabilityEntity: storeState.materialAvailability.entity,
  loading: storeState.materialAvailability.loading,
  updating: storeState.materialAvailability.updating,
  updateSuccess: storeState.materialAvailability.updateSuccess
});

const mapDispatchToProps = {
  getMaterials,
  getZones,
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
)(MaterialAvailabilityUpdate);
