import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './material-reserve.reducer';
import { IMaterialReserve } from 'app/shared/model/material-reserve.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IMaterialReserveDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class MaterialReserveDetail extends React.Component<IMaterialReserveDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { materialReserveEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="proceilApp.materialReserve.detail.title">MaterialReserve</Translate> [<b>{materialReserveEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="reserveDate">
                <Translate contentKey="proceilApp.materialReserve.reserveDate">Reserve Date</Translate>
              </span>
            </dt>
            <dd>
              <TextFormat value={materialReserveEntity.reserveDate} type="date" format={APP_DATE_FORMAT} />
            </dd>
            <dt>
              <span id="reserveStatus">
                <Translate contentKey="proceilApp.materialReserve.reserveStatus">Reserve Status</Translate>
              </span>
            </dt>
            <dd>{materialReserveEntity.reserveStatus}</dd>
            <dt>
              <span id="quantityToReserve">
                <Translate contentKey="proceilApp.materialReserve.quantityToReserve">Quantity To Reserve</Translate>
              </span>
            </dt>
            <dd>{materialReserveEntity.quantityToReserve}</dd>
            <dt>
              <span id="measureUnit">
                <Translate contentKey="proceilApp.materialReserve.measureUnit">Measure Unit</Translate>
              </span>
            </dt>
            <dd>{materialReserveEntity.measureUnit}</dd>
            <dt>
              <Translate contentKey="proceilApp.materialReserve.material">Material</Translate>
            </dt>
            <dd>{materialReserveEntity.materialMaterialName ? materialReserveEntity.materialMaterialName : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/material-reserve" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/material-reserve/${materialReserveEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ materialReserve }: IRootState) => ({
  materialReserveEntity: materialReserve.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(MaterialReserveDetail);
