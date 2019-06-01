import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, setFileData, byteSize, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { ICustomer } from 'app/shared/model/customer.model';
import { getEntities as getCustomers } from 'app/entities/customer/customer.reducer';
import { getEntity, updateEntity, createEntity, setBlob, reset } from './feedback.reducer';
import { IFeedback } from 'app/shared/model/feedback.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IFeedbackUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IFeedbackUpdateState {
  isNew: boolean;
  authorId: string;
}

export class FeedbackUpdate extends React.Component<IFeedbackUpdateProps, IFeedbackUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      authorId: '0',
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

    this.props.getCustomers();
  }

  onBlobChange = (isAnImage, name) => event => {
    setFileData(event, (contentType, data) => this.props.setBlob(name, data, contentType), isAnImage);
  };

  clearBlob = name => () => {
    this.props.setBlob(name, undefined, undefined);
  };

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { feedbackEntity } = this.props;
      const entity = {
        ...feedbackEntity,
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
    this.props.history.push('/entity/feedback');
  };

  render() {
    const { feedbackEntity, customers, loading, updating } = this.props;
    const { isNew } = this.state;

    const { text, feedbackResponse } = feedbackEntity;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="proceilApp.feedback.home.createOrEditLabel">
              <Translate contentKey="proceilApp.feedback.home.createOrEditLabel">Create or edit a Feedback</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : feedbackEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="feedback-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="feedback-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="captionLabel" for="feedback-caption">
                    <Translate contentKey="proceilApp.feedback.caption">Caption</Translate>
                  </Label>
                  <AvField
                    id="feedback-caption"
                    type="text"
                    name="caption"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      minLength: { value: 1, errorMessage: translate('entity.validation.minlength', { min: 1 }) }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="emailLabel" for="feedback-email">
                    <Translate contentKey="proceilApp.feedback.email">Email</Translate>
                  </Label>
                  <AvField id="feedback-email" type="text" name="email" />
                </AvGroup>
                <AvGroup>
                  <Label id="textLabel" for="feedback-text">
                    <Translate contentKey="proceilApp.feedback.text">Text</Translate>
                  </Label>
                  <AvInput
                    id="feedback-text"
                    type="textarea"
                    name="text"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="feedbackResponseLabel" for="feedback-feedbackResponse">
                    <Translate contentKey="proceilApp.feedback.feedbackResponse">Feedback Response</Translate>
                  </Label>
                  <AvInput id="feedback-feedbackResponse" type="textarea" name="feedbackResponse" />
                </AvGroup>
                <AvGroup>
                  <Label for="feedback-author">
                    <Translate contentKey="proceilApp.feedback.author">Author</Translate>
                  </Label>
                  <AvInput id="feedback-author" type="select" className="form-control" name="authorId">
                    <option value="" key="0" />
                    {customers
                      ? customers.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.customerSummary}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/feedback" replace color="info">
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
  customers: storeState.customer.entities,
  feedbackEntity: storeState.feedback.entity,
  loading: storeState.feedback.loading,
  updating: storeState.feedback.updating,
  updateSuccess: storeState.feedback.updateSuccess
});

const mapDispatchToProps = {
  getCustomers,
  getEntity,
  updateEntity,
  setBlob,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(FeedbackUpdate);
