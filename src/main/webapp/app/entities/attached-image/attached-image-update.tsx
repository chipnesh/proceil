import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, setFileData, openFile, byteSize, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { getEntity, updateEntity, createEntity, setBlob, reset } from './attached-image.reducer';
import { IAttachedImage } from 'app/shared/model/attached-image.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IAttachedImageUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IAttachedImageUpdateState {
  isNew: boolean;
}

export class AttachedImageUpdate extends React.Component<IAttachedImageUpdateProps, IAttachedImageUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
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
  }

  onBlobChange = (isAnImage, name) => event => {
    setFileData(event, (contentType, data) => this.props.setBlob(name, data, contentType), isAnImage);
  };

  clearBlob = name => () => {
    this.props.setBlob(name, undefined, undefined);
  };

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { attachedImageEntity } = this.props;
      const entity = {
        ...attachedImageEntity,
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
    this.props.history.push('/entity/attached-image');
  };

  render() {
    const { attachedImageEntity, loading, updating } = this.props;
    const { isNew } = this.state;

    const { imageFile, imageFileContentType } = attachedImageEntity;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="proceilApp.attachedImage.home.createOrEditLabel">
              <Translate contentKey="proceilApp.attachedImage.home.createOrEditLabel">Create or edit a AttachedImage</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : attachedImageEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="attached-image-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="attached-image-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="imageNameLabel" for="attached-image-imageName">
                    <Translate contentKey="proceilApp.attachedImage.imageName">Image Name</Translate>
                  </Label>
                  <AvField id="attached-image-imageName" type="text" name="imageName" />
                </AvGroup>
                <AvGroup>
                  <AvGroup>
                    <Label id="imageFileLabel" for="imageFile">
                      <Translate contentKey="proceilApp.attachedImage.imageFile">Image File</Translate>
                    </Label>
                    <br />
                    {imageFile ? (
                      <div>
                        <a onClick={openFile(imageFileContentType, imageFile)}>
                          <img src={`data:${imageFileContentType};base64,${imageFile}`} style={{ maxHeight: '100px' }} />
                        </a>
                        <br />
                        <Row>
                          <Col md="11">
                            <span>
                              {imageFileContentType}, {byteSize(imageFile)}
                            </span>
                          </Col>
                          <Col md="1">
                            <Button color="danger" onClick={this.clearBlob('imageFile')}>
                              <FontAwesomeIcon icon="times-circle" />
                            </Button>
                          </Col>
                        </Row>
                      </div>
                    ) : null}
                    <input id="file_imageFile" type="file" onChange={this.onBlobChange(true, 'imageFile')} accept="image/*" />
                    <AvInput type="hidden" name="imageFile" value={imageFile} />
                  </AvGroup>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/attached-image" replace color="info">
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
  attachedImageEntity: storeState.attachedImage.entity,
  loading: storeState.attachedImage.loading,
  updating: storeState.attachedImage.updating,
  updateSuccess: storeState.attachedImage.updateSuccess
});

const mapDispatchToProps = {
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
)(AttachedImageUpdate);
