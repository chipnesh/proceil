import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, setFileData, byteSize, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IEmployee } from 'app/shared/model/employee.model';
import { getEntities as getEmployees } from 'app/entities/employee/employee.reducer';
import { ICustomer } from 'app/shared/model/customer.model';
import { getEntities as getCustomers } from 'app/entities/customer/customer.reducer';
import { getEntity, updateEntity, createEntity, setBlob, reset } from './measurement.reducer';
import { IMeasurement } from 'app/shared/model/measurement.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IMeasurementUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IMeasurementUpdateState {
  isNew: boolean;
  workerId: string;
  clientId: string;
}

export class MeasurementUpdate extends React.Component<IMeasurementUpdateProps, IMeasurementUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      workerId: '0',
      clientId: '0',
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

    this.props.getEmployees();
    this.props.getCustomers();
  }

  onBlobChange = (isAnImage, name) => event => {
    setFileData(event, (contentType, data) => this.props.setBlob(name, data, contentType), isAnImage);
  };

  clearBlob = name => () => {
    this.props.setBlob(name, undefined, undefined);
  };

  saveEntity = (event, errors, values) => {
    values.measureDate = convertDateTimeToServer(values.measureDate);

    if (errors.length === 0) {
      const { measurementEntity } = this.props;
      const entity = {
        ...measurementEntity,
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
    this.props.history.push('/entity/measurement');
  };

  render() {
    const { measurementEntity, employees, customers, loading, updating } = this.props;
    const { isNew } = this.state;

    const { measureNote } = measurementEntity;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="proceilApp.measurement.home.createOrEditLabel">
              <Translate contentKey="proceilApp.measurement.home.createOrEditLabel">Create or edit a Measurement</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : measurementEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="measurement-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="measurement-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="measurementSummaryLabel" for="measurement-measurementSummary">
                    <Translate contentKey="proceilApp.measurement.measurementSummary">Measurement Summary</Translate>
                  </Label>
                  <AvField id="measurement-measurementSummary" type="text" name="measurementSummary" />
                </AvGroup>
                <AvGroup>
                  <Label id="measureDateLabel" for="measurement-measureDate">
                    <Translate contentKey="proceilApp.measurement.measureDate">Measure Date</Translate>
                  </Label>
                  <AvInput
                    id="measurement-measureDate"
                    type="datetime-local"
                    className="form-control"
                    name="measureDate"
                    placeholder={'YYYY-MM-DD HH:mm'}
                    value={isNew ? null : convertDateTimeFromServer(this.props.measurementEntity.measureDate)}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="measureNoteLabel" for="measurement-measureNote">
                    <Translate contentKey="proceilApp.measurement.measureNote">Measure Note</Translate>
                  </Label>
                  <AvInput id="measurement-measureNote" type="textarea" name="measureNote" />
                </AvGroup>
                <AvGroup>
                  <Label id="measureAddressLabel" for="measurement-measureAddress">
                    <Translate contentKey="proceilApp.measurement.measureAddress">Measure Address</Translate>
                  </Label>
                  <AvField id="measurement-measureAddress" type="text" name="measureAddress" />
                </AvGroup>
                <AvGroup>
                  <Label for="measurement-worker">
                    <Translate contentKey="proceilApp.measurement.worker">Worker</Translate>
                  </Label>
                  <AvInput id="measurement-worker" type="select" className="form-control" name="workerId">
                    <option value="" key="0" />
                    {employees
                      ? employees.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.employeeName}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="measurement-client">
                    <Translate contentKey="proceilApp.measurement.client">Client</Translate>
                  </Label>
                  <AvInput id="measurement-client" type="select" className="form-control" name="clientId">
                    <option value="" key="0" />
                    {customers
                      ? customers.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.customerSummary}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/measurement" replace color="info">
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
  employees: storeState.employee.entities,
  customers: storeState.customer.entities,
  measurementEntity: storeState.measurement.entity,
  loading: storeState.measurement.loading,
  updating: storeState.measurement.updating,
  updateSuccess: storeState.measurement.updateSuccess
});

const mapDispatchToProps = {
  getEmployees,
  getCustomers,
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
)(MeasurementUpdate);
