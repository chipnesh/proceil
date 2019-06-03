import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction, byteSize, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './material-request.reducer';
import { IMaterialRequest } from 'app/shared/model/material-request.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IMaterialRequestDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class MaterialRequestDetail extends React.Component<IMaterialRequestDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { materialRequestEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="proceilApp.materialRequest.detail.title">MaterialRequest</Translate> [<b>{materialRequestEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="requestSummary">
                <Translate contentKey="proceilApp.materialRequest.requestSummary">Request Summary</Translate>
              </span>
            </dt>
            <dd>{materialRequestEntity.requestSummary}</dd>
            <dt>
              <span id="createdDate">
                <Translate contentKey="proceilApp.materialRequest.createdDate">Created Date</Translate>
              </span>
            </dt>
            <dd>
              <TextFormat blankOnInvalid value={materialRequestEntity.createdDate} type="date" format={APP_DATE_FORMAT} />
            </dd>
            <dt>
              <span id="closedDate">
                <Translate contentKey="proceilApp.materialRequest.closedDate">Closed Date</Translate>
              </span>
            </dt>
            <dd>
              <TextFormat blankOnInvalid value={materialRequestEntity.closedDate} type="date" format={APP_DATE_FORMAT} />
            </dd>
            <dt>
              <span id="requestNote">
                <Translate contentKey="proceilApp.materialRequest.requestNote">Request Note</Translate>
              </span>
            </dt>
            <dd>{materialRequestEntity.requestNote}</dd>
            <dt>
              <span id="requestPriority">
                <Translate contentKey="proceilApp.materialRequest.requestPriority">Request Priority</Translate>
              </span>
            </dt>
            <dd>{materialRequestEntity.requestPriority}</dd>
            <dt>
              <span id="requestStatus">
                <Translate contentKey="proceilApp.materialRequest.requestStatus">Request Status</Translate>
              </span>
            </dt>
            <dd>
              <Translate contentKey={'proceilApp.MaterialRequestStatus.' + materialRequestEntity.requestStatus}>
                {materialRequestEntity.requestStatus}
              </Translate>
            </dd>
            <dt>
              <span id="requestedQuantity">
                <Translate contentKey="proceilApp.materialRequest.requestedQuantity">Requested Quantity</Translate>
              </span>
            </dt>
            <dd>{materialRequestEntity.requestedQuantity}</dd>
            <dt>
              <span id="measureUnit">
                <Translate contentKey="proceilApp.materialRequest.measureUnit">Measure Unit</Translate>
              </span>
            </dt>
            <dd>
            <Translate contentKey={'proceilApp.MeasureUnit.' + materialRequestEntity.measureUnit}>
              {materialRequestEntity.measureUnit}
            </Translate>
            </dd>
            <dt>
              <Translate contentKey="proceilApp.materialRequest.requester">Requester</Translate>
            </dt>
            <dd>{materialRequestEntity.requesterFacilityName ? materialRequestEntity.requesterFacilityName : ''}</dd>
            <dt>
              <Translate contentKey="proceilApp.materialRequest.material">Material</Translate>
            </dt>
            <dd>{materialRequestEntity.materialMaterialName ? materialRequestEntity.materialMaterialName : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/material-request" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/material-request/${materialRequestEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ materialRequest }: IRootState) => ({
  materialRequestEntity: materialRequest.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(MaterialRequestDetail);
