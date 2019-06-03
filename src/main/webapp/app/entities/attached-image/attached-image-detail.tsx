import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction, openFile, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './attached-image.reducer';
import { IAttachedImage } from 'app/shared/model/attached-image.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IAttachedImageDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class AttachedImageDetail extends React.Component<IAttachedImageDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { attachedImageEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="proceilApp.attachedImage.detail.title">AttachedImage</Translate> [<b>{attachedImageEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="imageName">
                <Translate contentKey="proceilApp.attachedImage.imageName">Image Name</Translate>
              </span>
            </dt>
            <dd>{attachedImageEntity.imageName}</dd>
            <dt>
              <span id="imageFile">
                <Translate contentKey="proceilApp.attachedImage.imageFile">Image File</Translate>
              </span>
            </dt>
            <dd>
              {attachedImageEntity.imageFile ? (
                <div>
                  <a onClick={openFile(attachedImageEntity.imageFileContentType, attachedImageEntity.imageFile)}>
                    <img
                      src={`data:${attachedImageEntity.imageFileContentType};base64,${attachedImageEntity.imageFile}`}
                      style={{ maxHeight: '30px' }}
                    />
                  </a>
                  <span>
                    {attachedImageEntity.imageFileContentType}, {byteSize(attachedImageEntity.imageFile)}
                  </span>
                </div>
              ) : null}
            </dd>
          </dl>
          <Button tag={Link} to="/entity/attached-image" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/attached-image/${attachedImageEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ attachedImage }: IRootState) => ({
  attachedImageEntity: attachedImage.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(AttachedImageDetail);
