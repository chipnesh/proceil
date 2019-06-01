import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IService } from 'app/shared/model/service.model';
import { getEntities as getServices } from 'app/entities/service/service.reducer';
import { IZone } from 'app/shared/model/zone.model';
import { getEntities as getZones } from 'app/entities/zone/zone.reducer';
import { getEntity, updateEntity, createEntity, reset } from './service-availability.reducer';
import { IServiceAvailability } from 'app/shared/model/service-availability.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IServiceAvailabilityUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IServiceAvailabilityUpdateState {
  isNew: boolean;
  serviceId: string;
  providedById: string;
}

export class ServiceAvailabilityUpdate extends React.Component<IServiceAvailabilityUpdateProps, IServiceAvailabilityUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      serviceId: '0',
      providedById: '0',
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

    this.props.getServices();
    this.props.getZones();
  }

  saveEntity = (event, errors, values) => {
    values.dateFrom = convertDateTimeToServer(values.dateFrom);
    values.dateTo = convertDateTimeToServer(values.dateTo);

    if (errors.length === 0) {
      const { serviceAvailabilityEntity } = this.props;
      const entity = {
        ...serviceAvailabilityEntity,
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
    this.props.history.push('/entity/service-availability');
  };

  render() {
    const { serviceAvailabilityEntity, services, zones, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="proceilApp.serviceAvailability.home.createOrEditLabel">
              <Translate contentKey="proceilApp.serviceAvailability.home.createOrEditLabel">Create or edit a ServiceAvailability</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : serviceAvailabilityEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="service-availability-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="service-availability-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="availabilitySummaryLabel" for="service-availability-availabilitySummary">
                    <Translate contentKey="proceilApp.serviceAvailability.availabilitySummary">Availability Summary</Translate>
                  </Label>
                  <AvField id="service-availability-availabilitySummary" type="text" name="availabilitySummary" />
                </AvGroup>
                <AvGroup>
                  <Label id="dateFromLabel" for="service-availability-dateFrom">
                    <Translate contentKey="proceilApp.serviceAvailability.dateFrom">Date From</Translate>
                  </Label>
                  <AvInput
                    id="service-availability-dateFrom"
                    type="datetime-local"
                    className="form-control"
                    name="dateFrom"
                    placeholder={'YYYY-MM-DD HH:mm'}
                    value={isNew ? null : convertDateTimeFromServer(this.props.serviceAvailabilityEntity.dateFrom)}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="dateToLabel" for="service-availability-dateTo">
                    <Translate contentKey="proceilApp.serviceAvailability.dateTo">Date To</Translate>
                  </Label>
                  <AvInput
                    id="service-availability-dateTo"
                    type="datetime-local"
                    className="form-control"
                    name="dateTo"
                    placeholder={'YYYY-MM-DD HH:mm'}
                    value={isNew ? null : convertDateTimeFromServer(this.props.serviceAvailabilityEntity.dateTo)}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="remainingQuotasLabel" for="service-availability-remainingQuotas">
                    <Translate contentKey="proceilApp.serviceAvailability.remainingQuotas">Remaining Quotas</Translate>
                  </Label>
                  <AvField id="service-availability-remainingQuotas" type="string" className="form-control" name="remainingQuotas" />
                </AvGroup>
                <AvGroup>
                  <Label for="service-availability-service">
                    <Translate contentKey="proceilApp.serviceAvailability.service">Service</Translate>
                  </Label>
                  <AvInput id="service-availability-service" type="select" className="form-control" name="serviceId">
                    <option value="" key="0" />
                    {services
                      ? services.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.serviceName}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="service-availability-providedBy">
                    <Translate contentKey="proceilApp.serviceAvailability.providedBy">Provided By</Translate>
                  </Label>
                  <AvInput id="service-availability-providedBy" type="select" className="form-control" name="providedById">
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
                <Button tag={Link} id="cancel-save" to="/entity/service-availability" replace color="info">
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
  services: storeState.service.entities,
  zones: storeState.zone.entities,
  serviceAvailabilityEntity: storeState.serviceAvailability.entity,
  loading: storeState.serviceAvailability.loading,
  updating: storeState.serviceAvailability.updating,
  updateSuccess: storeState.serviceAvailability.updateSuccess
});

const mapDispatchToProps = {
  getServices,
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
)(ServiceAvailabilityUpdate);
