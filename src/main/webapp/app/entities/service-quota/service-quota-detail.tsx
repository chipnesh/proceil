import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './service-quota.reducer';
import { IServiceQuota } from 'app/shared/model/service-quota.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IServiceQuotaDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class ServiceQuotaDetail extends React.Component<IServiceQuotaDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { serviceQuotaEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="proceilApp.serviceQuota.detail.title">ServiceQuota</Translate> [<b>{serviceQuotaEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="dateFrom">
                <Translate contentKey="proceilApp.serviceQuota.dateFrom">Date From</Translate>
              </span>
            </dt>
            <dd>
              <TextFormat value={serviceQuotaEntity.dateFrom} type="date" format={APP_DATE_FORMAT} />
            </dd>
            <dt>
              <span id="dateTo">
                <Translate contentKey="proceilApp.serviceQuota.dateTo">Date To</Translate>
              </span>
            </dt>
            <dd>
              <TextFormat value={serviceQuotaEntity.dateTo} type="date" format={APP_DATE_FORMAT} />
            </dd>
            <dt>
              <span id="quotaStatus">
                <Translate contentKey="proceilApp.serviceQuota.quotaStatus">Quota Status</Translate>
              </span>
            </dt>
            <dd>{serviceQuotaEntity.quotaStatus}</dd>
            <dt>
              <span id="quantityToQuote">
                <Translate contentKey="proceilApp.serviceQuota.quantityToQuote">Quantity To Quote</Translate>
              </span>
            </dt>
            <dd>{serviceQuotaEntity.quantityToQuote}</dd>
            <dt>
              <Translate contentKey="proceilApp.serviceQuota.service">Service</Translate>
            </dt>
            <dd>{serviceQuotaEntity.serviceServiceName ? serviceQuotaEntity.serviceServiceName : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/service-quota" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/service-quota/${serviceQuotaEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ serviceQuota }: IRootState) => ({
  serviceQuotaEntity: serviceQuota.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ServiceQuotaDetail);
