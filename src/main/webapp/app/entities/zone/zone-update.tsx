import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IFacility } from 'app/shared/model/facility.model';
import { getEntities as getFacilities } from 'app/entities/facility/facility.reducer';
import { getEntity, updateEntity, createEntity, reset } from './zone.reducer';
import { IZone } from 'app/shared/model/zone.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IZoneUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IZoneUpdateState {
  isNew: boolean;
  facilityId: string;
}

export class ZoneUpdate extends React.Component<IZoneUpdateProps, IZoneUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      facilityId: '0',
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
  }

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { zoneEntity } = this.props;
      const entity = {
        ...zoneEntity,
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
    this.props.history.push('/entity/zone');
  };

  render() {
    const { zoneEntity, facilities, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="proceilApp.zone.home.createOrEditLabel">
              <Translate contentKey="proceilApp.zone.home.createOrEditLabel">Create or edit a Zone</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : zoneEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="zone-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="zone-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="zoneNameLabel" for="zone-zoneName">
                    <Translate contentKey="proceilApp.zone.zoneName">Zone Name</Translate>
                  </Label>
                  <AvField id="zone-zoneName" type="text" name="zoneName" />
                </AvGroup>
                <AvGroup>
                  <Label for="zone-facility">
                    <Translate contentKey="proceilApp.zone.facility">Facility</Translate>
                  </Label>
                  <AvInput id="zone-facility" type="select" className="form-control" name="facilityId">
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
                <Button tag={Link} id="cancel-save" to="/entity/zone" replace color="info">
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
  zoneEntity: storeState.zone.entity,
  loading: storeState.zone.loading,
  updating: storeState.zone.updating,
  updateSuccess: storeState.zone.updateSuccess
});

const mapDispatchToProps = {
  getFacilities,
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
)(ZoneUpdate);
