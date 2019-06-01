import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './material.reducer';
import { IMaterial } from 'app/shared/model/material.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IMaterialDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class MaterialDetail extends React.Component<IMaterialDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { materialEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="proceilApp.material.detail.title">Material</Translate> [<b>{materialEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="materialName">
                <Translate contentKey="proceilApp.material.materialName">Material Name</Translate>
              </span>
            </dt>
            <dd>{materialEntity.materialName}</dd>
            <dt>
              <span id="materialDescription">
                <Translate contentKey="proceilApp.material.materialDescription">Material Description</Translate>
              </span>
            </dt>
            <dd>{materialEntity.materialDescription}</dd>
            <dt>
              <span id="materialPrice">
                <Translate contentKey="proceilApp.material.materialPrice">Material Price</Translate>
              </span>
            </dt>
            <dd>{materialEntity.materialPrice}</dd>
          </dl>
          <Button tag={Link} to="/entity/material" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/material/${materialEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ material }: IRootState) => ({
  materialEntity: material.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(MaterialDetail);
