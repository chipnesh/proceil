import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, setFileData, byteSize, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IFacility } from 'app/shared/model/facility.model';
import { getEntities as getFacilities } from 'app/entities/facility/facility.reducer';
import { IMaterial } from 'app/shared/model/material.model';
import { getEntities as getMaterials } from 'app/entities/material/material.reducer';
import { getEntity, updateEntity, createEntity, setBlob, reset } from './material-request.reducer';
import { IMaterialRequest } from 'app/shared/model/material-request.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IMaterialRequestUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IMaterialRequestUpdateState {
  isNew: boolean;
  requesterId: string;
  materialId: string;
}

export class MaterialRequestUpdate extends React.Component<IMaterialRequestUpdateProps, IMaterialRequestUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      requesterId: '0',
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

    this.props.getFacilities();
    this.props.getMaterials();
  }

  onBlobChange = (isAnImage, name) => event => {
    setFileData(event, (contentType, data) => this.props.setBlob(name, data, contentType), isAnImage);
  };

  clearBlob = name => () => {
    this.props.setBlob(name, undefined, undefined);
  };

  saveEntity = (event, errors, values) => {
    values.createdDate = convertDateTimeToServer(values.createdDate);
    values.closedDate = convertDateTimeToServer(values.closedDate);

    if (errors.length === 0) {
      const { materialRequestEntity } = this.props;
      const entity = {
        ...materialRequestEntity,
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
    this.props.history.push('/entity/material-request');
  };

  render() {
    const { materialRequestEntity, facilities, materials, loading, updating } = this.props;
    const { isNew } = this.state;

    const { requestNote } = materialRequestEntity;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="proceilApp.materialRequest.home.createOrEditLabel">
              <Translate contentKey="proceilApp.materialRequest.home.createOrEditLabel">Create or edit a MaterialRequest</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : materialRequestEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="material-request-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="material-request-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="requestSummaryLabel" for="material-request-requestSummary">
                    <Translate contentKey="proceilApp.materialRequest.requestSummary">Request Summary</Translate>
                  </Label>
                  <AvField id="material-request-requestSummary" type="text" name="requestSummary" />
                </AvGroup>
                <AvGroup>
                  <Label id="createdDateLabel" for="material-request-createdDate">
                    <Translate contentKey="proceilApp.materialRequest.createdDate">Created Date</Translate>
                  </Label>
                  <AvInput
                    id="material-request-createdDate"
                    type="datetime-local"
                    className="form-control"
                    name="createdDate"
                    placeholder={'YYYY-MM-DD HH:mm'}
                    value={isNew ? null : convertDateTimeFromServer(this.props.materialRequestEntity.createdDate)}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="closedDateLabel" for="material-request-closedDate">
                    <Translate contentKey="proceilApp.materialRequest.closedDate">Closed Date</Translate>
                  </Label>
                  <AvInput
                    id="material-request-closedDate"
                    type="datetime-local"
                    className="form-control"
                    name="closedDate"
                    placeholder={'YYYY-MM-DD HH:mm'}
                    value={isNew ? null : convertDateTimeFromServer(this.props.materialRequestEntity.closedDate)}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="requestNoteLabel" for="material-request-requestNote">
                    <Translate contentKey="proceilApp.materialRequest.requestNote">Request Note</Translate>
                  </Label>
                  <AvInput id="material-request-requestNote" type="textarea" name="requestNote" />
                </AvGroup>
                <AvGroup>
                  <Label id="requestPriorityLabel" for="material-request-requestPriority">
                    <Translate contentKey="proceilApp.materialRequest.requestPriority">Request Priority</Translate>
                  </Label>
                  <AvField id="material-request-requestPriority" type="string" className="form-control" name="requestPriority" />
                </AvGroup>
                <AvGroup>
                  <Label id="requestStatusLabel" for="material-request-requestStatus">
                    <Translate contentKey="proceilApp.materialRequest.requestStatus">Request Status</Translate>
                  </Label>
                  <AvInput
                    id="material-request-requestStatus"
                    type="select"
                    className="form-control"
                    name="requestStatus"
                    value={(!isNew && materialRequestEntity.requestStatus) || 'NEW'}
                  >
                    <option value="NEW">
                      {translate('proceilApp.MaterialRequestStatus.NEW')}
                    </option>
                    <option value="IN_PROGRESS">
                      {translate('proceilApp.MaterialRequestStatus.IN_PROGRESS')}
                    </option>
                    <option value="FINISHED">
                      {translate('proceilApp.MaterialRequestStatus.FINISHED')}
                    </option>
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label id="requestedQuantityLabel" for="material-request-requestedQuantity">
                    <Translate contentKey="proceilApp.materialRequest.requestedQuantity">Requested Quantity</Translate>
                  </Label>
                  <AvField id="material-request-requestedQuantity" type="string" className="form-control" name="requestedQuantity" />
                </AvGroup>
                <AvGroup>
                  <Label id="measureUnitLabel" for="material-request-measureUnit">
                    <Translate contentKey="proceilApp.materialRequest.measureUnit">Measure Unit</Translate>
                  </Label>
                  <AvInput
                    id="material-request-measureUnit"
                    type="select"
                    className="form-control"
                    name="measureUnit"
                    value={(!isNew && materialRequestEntity.measureUnit) || 'METER'}
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
                  <Label for="material-request-requester">
                    <Translate contentKey="proceilApp.materialRequest.requester">Requester</Translate>
                  </Label>
                  <AvInput id="material-request-requester" type="select" className="form-control" name="requesterId">
                    <option value="" key="0" />
                    {facilities
                      ? facilities.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.facilityName}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="material-request-material">
                    <Translate contentKey="proceilApp.materialRequest.material">Material</Translate>
                  </Label>
                  <AvInput id="material-request-material" type="select" className="form-control" name="materialId">
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
                <Button tag={Link} id="cancel-save" to="/entity/material-request" replace color="info">
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
  facilities: storeState.facility.entities,
  materials: storeState.material.entities,
  materialRequestEntity: storeState.materialRequest.entity,
  loading: storeState.materialRequest.loading,
  updating: storeState.materialRequest.updating,
  updateSuccess: storeState.materialRequest.updateSuccess
});

const mapDispatchToProps = {
  getFacilities,
  getMaterials,
  getEntity,
  updateEntity,
  setBlob,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(MaterialRequestUpdate);
