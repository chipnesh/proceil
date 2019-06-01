import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './zone.reducer';
import { IZone } from 'app/shared/model/zone.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IZoneDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class ZoneDetail extends React.Component<IZoneDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { zoneEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="proceilApp.zone.detail.title">Zone</Translate> [<b>{zoneEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="zoneName">
                <Translate contentKey="proceilApp.zone.zoneName">Zone Name</Translate>
              </span>
            </dt>
            <dd>{zoneEntity.zoneName}</dd>
            <dt>
              <Translate contentKey="proceilApp.zone.facility">Facility</Translate>
            </dt>
            <dd>{zoneEntity.facilityFacilityName ? zoneEntity.facilityFacilityName : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/zone" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/zone/${zoneEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ zone }: IRootState) => ({
  zoneEntity: zone.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ZoneDetail);
