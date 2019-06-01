import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction, byteSize, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './material-arrival.reducer';
import { IMaterialArrival } from 'app/shared/model/material-arrival.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IMaterialArrivalDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class MaterialArrivalDetail extends React.Component<IMaterialArrivalDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { materialArrivalEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="proceilApp.materialArrival.detail.title">MaterialArrival</Translate> [<b>{materialArrivalEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="arrivalSummary">
                <Translate contentKey="proceilApp.materialArrival.arrivalSummary">Arrival Summary</Translate>
              </span>
            </dt>
            <dd>{materialArrivalEntity.arrivalSummary}</dd>
            <dt>
              <span id="arrivalDate">
                <Translate contentKey="proceilApp.materialArrival.arrivalDate">Arrival Date</Translate>
              </span>
            </dt>
            <dd>
              <TextFormat value={materialArrivalEntity.arrivalDate} type="date" format={APP_DATE_FORMAT} />
            </dd>
            <dt>
              <span id="arrivalNote">
                <Translate contentKey="proceilApp.materialArrival.arrivalNote">Arrival Note</Translate>
              </span>
            </dt>
            <dd>{materialArrivalEntity.arrivalNote}</dd>
            <dt>
              <span id="arrivedQuantity">
                <Translate contentKey="proceilApp.materialArrival.arrivedQuantity">Arrived Quantity</Translate>
              </span>
            </dt>
            <dd>{materialArrivalEntity.arrivedQuantity}</dd>
            <dt>
              <span id="measureUnit">
                <Translate contentKey="proceilApp.materialArrival.measureUnit">Measure Unit</Translate>
              </span>
            </dt>
            <dd>{materialArrivalEntity.measureUnit}</dd>
            <dt>
              <Translate contentKey="proceilApp.materialArrival.request">Request</Translate>
            </dt>
            <dd>{materialArrivalEntity.requestRequestSummary ? materialArrivalEntity.requestRequestSummary : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/material-arrival" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/material-arrival/${materialArrivalEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ materialArrival }: IRootState) => ({
  materialArrivalEntity: materialArrival.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(MaterialArrivalDetail);
