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
import { getEntity, updateEntity, createEntity, reset } from './service-quota.reducer';
import { IServiceQuota } from 'app/shared/model/service-quota.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IServiceQuotaUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IServiceQuotaUpdateState {
  isNew: boolean;
  serviceId: string;
}

export class ServiceQuotaUpdate extends React.Component<IServiceQuotaUpdateProps, IServiceQuotaUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      serviceId: '0',
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
  }

  saveEntity = (event, errors, values) => {
    values.dateFrom = convertDateTimeToServer(values.dateFrom);
    values.dateTo = convertDateTimeToServer(values.dateTo);

    if (errors.length === 0) {
      const { serviceQuotaEntity } = this.props;
      const entity = {
        ...serviceQuotaEntity,
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
    this.props.history.push('/entity/service-quota');
  };

  render() {
    const { serviceQuotaEntity, services, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="proceilApp.serviceQuota.home.createOrEditLabel">
              <Translate contentKey="proceilApp.serviceQuota.home.createOrEditLabel">Create or edit a ServiceQuota</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : serviceQuotaEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="service-quota-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="service-quota-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="dateFromLabel" for="service-quota-dateFrom">
                    <Translate contentKey="proceilApp.serviceQuota.dateFrom">Date From</Translate>
                  </Label>
                  <AvInput
                    id="service-quota-dateFrom"
                    type="datetime-local"
                    className="form-control"
                    name="dateFrom"
                    placeholder={'YYYY-MM-DD HH:mm'}
                    value={isNew ? null : convertDateTimeFromServer(this.props.serviceQuotaEntity.dateFrom)}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="dateToLabel" for="service-quota-dateTo">
                    <Translate contentKey="proceilApp.serviceQuota.dateTo">Date To</Translate>
                  </Label>
                  <AvInput
                    id="service-quota-dateTo"
                    type="datetime-local"
                    className="form-control"
                    name="dateTo"
                    placeholder={'YYYY-MM-DD HH:mm'}
                    value={isNew ? null : convertDateTimeFromServer(this.props.serviceQuotaEntity.dateTo)}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="quotaStatusLabel" for="service-quota-quotaStatus">
                    <Translate contentKey="proceilApp.serviceQuota.quotaStatus">Quota Status</Translate>
                  </Label>
                  <AvInput
                    id="service-quota-quotaStatus"
                    type="select"
                    className="form-control"
                    name="quotaStatus"
                    value={(!isNew && serviceQuotaEntity.quotaStatus) || 'NEW'}
                  >
                    <option value="NEW">
                      {translate('proceilApp.ServiceQuotingStatus.NEW')}
                    </option>
                    <option value="QUOTED">
                      {translate('proceilApp.ServiceQuotingStatus.QUOTED')}
                    </option>
                    <option value="BUSY">
                      {translate('proceilApp.ServiceQuotingStatus.BUSY')}
                    </option>
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label id="quantityToQuoteLabel" for="service-quota-quantityToQuote">
                    <Translate contentKey="proceilApp.serviceQuota.quantityToQuote">Quantity To Quote</Translate>
                  </Label>
                  <AvField id="service-quota-quantityToQuote" type="string" className="form-control" name="quantityToQuote" />
                </AvGroup>
                <AvGroup>
                  <Label for="service-quota-service">
                    <Translate contentKey="proceilApp.serviceQuota.service">Service</Translate>
                  </Label>
                  <AvInput id="service-quota-service" type="select" className="form-control" name="serviceId">
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
                <Button tag={Link} id="cancel-save" to="/entity/service-quota" replace color="info">
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
  serviceQuotaEntity: storeState.serviceQuota.entity,
  loading: storeState.serviceQuota.loading,
  updating: storeState.serviceQuota.updating,
  updateSuccess: storeState.serviceQuota.updateSuccess
});

const mapDispatchToProps = {
  getServices,
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
)(ServiceQuotaUpdate);
