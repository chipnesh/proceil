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
import { getEntity, updateEntity, createEntity, reset } from './material-reserve.reducer';
import { IMaterialReserve } from 'app/shared/model/material-reserve.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IMaterialReserveUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IMaterialReserveUpdateState {
  isNew: boolean;
  materialId: string;
}

export class MaterialReserveUpdate extends React.Component<IMaterialReserveUpdateProps, IMaterialReserveUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      materialId: '0',
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
  }

  saveEntity = (event, errors, values) => {

    if (errors.length === 0) {
      const { materialReserveEntity } = this.props;
      const entity = {
        ...materialReserveEntity,
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
    this.props.history.push('/entity/material-reserve');
  };

  render() {
    const { materialReserveEntity, materials, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="proceilApp.materialReserve.home.createOrEditLabel">
              <Translate contentKey="proceilApp.materialReserve.home.createOrEditLabel">Create or edit a MaterialReserve</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : materialReserveEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="material-reserve-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="material-reserve-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="quantityToReserveLabel" for="material-reserve-quantityToReserve">
                    <Translate contentKey="proceilApp.materialReserve.quantityToReserve">Quantity To Reserve</Translate>
                  </Label>
                  <AvField id="material-reserve-quantityToReserve" type="string" className="form-control" name="quantityToReserve" />
                </AvGroup>
                <AvGroup>
                  <Label id="measureUnitLabel" for="material-reserve-measureUnit">
                    <Translate contentKey="proceilApp.materialReserve.measureUnit">Measure Unit</Translate>
                  </Label>
                  <AvInput
                    id="material-reserve-measureUnit"
                    type="select"
                    className="form-control"
                    name="measureUnit"
                    value={(!isNew && materialReserveEntity.measureUnit) || 'METER'}
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
                  <Label for="material-reserve-material">
                    <Translate contentKey="proceilApp.materialReserve.material">Material</Translate>
                  </Label>
                  <AvInput id="material-reserve-material" type="select" className="form-control" name="materialId">
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
                <Button tag={Link} id="cancel-save" to="/entity/material-reserve" replace color="info">
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
  materialReserveEntity: storeState.materialReserve.entity,
  loading: storeState.materialReserve.loading,
  updating: storeState.materialReserve.updating,
  updateSuccess: storeState.materialReserve.updateSuccess
});

const mapDispatchToProps = {
  getMaterials,
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
)(MaterialReserveUpdate);
