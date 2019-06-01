import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './customer.reducer';
import { ICustomer } from 'app/shared/model/customer.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface ICustomerDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class CustomerDetail extends React.Component<ICustomerDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { customerEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="proceilApp.customer.detail.title">Customer</Translate> [<b>{customerEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="customerSummary">
                <Translate contentKey="proceilApp.customer.customerSummary">Customer Summary</Translate>
              </span>
            </dt>
            <dd>{customerEntity.customerSummary}</dd>
            <dt>
              <span id="firstname">
                <Translate contentKey="proceilApp.customer.firstname">Firstname</Translate>
              </span>
            </dt>
            <dd>{customerEntity.firstname}</dd>
            <dt>
              <span id="lastname">
                <Translate contentKey="proceilApp.customer.lastname">Lastname</Translate>
              </span>
            </dt>
            <dd>{customerEntity.lastname}</dd>
            <dt>
              <span id="middlename">
                <Translate contentKey="proceilApp.customer.middlename">Middlename</Translate>
              </span>
            </dt>
            <dd>{customerEntity.middlename}</dd>
            <dt>
              <span id="birthDate">
                <Translate contentKey="proceilApp.customer.birthDate">Birth Date</Translate>
              </span>
            </dt>
            <dd>
              <TextFormat value={customerEntity.birthDate} type="date" format={APP_DATE_FORMAT} />
            </dd>
            <dt>
              <span id="email">
                <Translate contentKey="proceilApp.customer.email">Email</Translate>
              </span>
            </dt>
            <dd>{customerEntity.email}</dd>
            <dt>
              <span id="phone">
                <Translate contentKey="proceilApp.customer.phone">Phone</Translate>
              </span>
            </dt>
            <dd>{customerEntity.phone}</dd>
            <dt>
              <span id="address">
                <Translate contentKey="proceilApp.customer.address">Address</Translate>
              </span>
            </dt>
            <dd>{customerEntity.address}</dd>
          </dl>
          <Button tag={Link} to="/entity/customer" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/customer/${customerEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ customer }: IRootState) => ({
  customerEntity: customer.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(CustomerDetail);
