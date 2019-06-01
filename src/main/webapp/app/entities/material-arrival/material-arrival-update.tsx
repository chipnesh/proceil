import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, setFileData, byteSize, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IMaterialRequest } from 'app/shared/model/material-request.model';
import { getEntities as getMaterialRequests } from 'app/entities/material-request/material-request.reducer';
import { getEntity, updateEntity, createEntity, setBlob, reset } from './material-arrival.reducer';
import { IMaterialArrival } from 'app/shared/model/material-arrival.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IMaterialArrivalUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IMaterialArrivalUpdateState {
  isNew: boolean;
  requestId: string;
}

export class MaterialArrivalUpdate extends React.Component<IMaterialArrivalUpdateProps, IMaterialArrivalUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      requestId: '0',
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

    this.props.getMaterialRequests();
  }

  onBlobChange = (isAnImage, name) => event => {
    setFileData(event, (contentType, data) => this.props.setBlob(name, data, contentType), isAnImage);
  };

  clearBlob = name => () => {
    this.props.setBlob(name, undefined, undefined);
  };

  saveEntity = (event, errors, values) => {
    values.arrivalDate = convertDateTimeToServer(values.arrivalDate);

    if (errors.length === 0) {
      const { materialArrivalEntity } = this.props;
      const entity = {
        ...materialArrivalEntity,
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
    this.props.history.push('/entity/material-arrival');
  };

  render() {
    const { materialArrivalEntity, materialRequests, loading, updating } = this.props;
    const { isNew } = this.state;

    const { arrivalNote } = materialArrivalEntity;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="proceilApp.materialArrival.home.createOrEditLabel">
              <Translate contentKey="proceilApp.materialArrival.home.createOrEditLabel">Create or edit a MaterialArrival</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : materialArrivalEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="material-arrival-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="material-arrival-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="arrivalSummaryLabel" for="material-arrival-arrivalSummary">
                    <Translate contentKey="proceilApp.materialArrival.arrivalSummary">Arrival Summary</Translate>
                  </Label>
                  <AvField id="material-arrival-arrivalSummary" type="text" name="arrivalSummary" />
                </AvGroup>
                <AvGroup>
                  <Label id="arrivalDateLabel" for="material-arrival-arrivalDate">
                    <Translate contentKey="proceilApp.materialArrival.arrivalDate">Arrival Date</Translate>
                  </Label>
                  <AvInput
                    id="material-arrival-arrivalDate"
                    type="datetime-local"
                    className="form-control"
                    name="arrivalDate"
                    placeholder={'YYYY-MM-DD HH:mm'}
                    value={isNew ? null : convertDateTimeFromServer(this.props.materialArrivalEntity.arrivalDate)}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="arrivalNoteLabel" for="material-arrival-arrivalNote">
                    <Translate contentKey="proceilApp.materialArrival.arrivalNote">Arrival Note</Translate>
                  </Label>
                  <AvInput id="material-arrival-arrivalNote" type="textarea" name="arrivalNote" />
                </AvGroup>
                <AvGroup>
                  <Label id="arrivedQuantityLabel" for="material-arrival-arrivedQuantity">
                    <Translate contentKey="proceilApp.materialArrival.arrivedQuantity">Arrived Quantity</Translate>
                  </Label>
                  <AvField id="material-arrival-arrivedQuantity" type="string" className="form-control" name="arrivedQuantity" />
                </AvGroup>
                <AvGroup>
                  <Label id="measureUnitLabel" for="material-arrival-measureUnit">
                    <Translate contentKey="proceilApp.materialArrival.measureUnit">Measure Unit</Translate>
                  </Label>
                  <AvInput
                    id="material-arrival-measureUnit"
                    type="select"
                    className="form-control"
                    name="measureUnit"
                    value={(!isNew && materialArrivalEntity.measureUnit) || 'METER'}
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
                  <Label for="material-arrival-request">
                    <Translate contentKey="proceilApp.materialArrival.request">Request</Translate>
                  </Label>
                  <AvInput id="material-arrival-request" type="select" className="form-control" name="requestId">
                    <option value="" key="0" />
                    {materialRequests
                      ? materialRequests.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.requestSummary}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/material-arrival" replace color="info">
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
  materialRequests: storeState.materialRequest.entities,
  materialArrivalEntity: storeState.materialArrival.entity,
  loading: storeState.materialArrival.loading,
  updating: storeState.materialArrival.updating,
  updateSuccess: storeState.materialArrival.updateSuccess
});

const mapDispatchToProps = {
  getMaterialRequests,
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
)(MaterialArrivalUpdate);
