import './home.scss';

import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { Button } from 'reactstrap';
import { Translate } from 'react-jhipster';

const cardStyle = {
  width: '80%',
  height: '300px',
  borderRadius: '21px 21px 0 0'
};

export interface IHomeProp extends StateProps, DispatchProps {
}

export class Home extends React.Component<IHomeProp> {
  render() {
    return (
      <div>

        <div className="position-relative overflow-hidden p-3 p-md-5 m-md-3 text-center bg-light">
          <div className="col-md-5 p-lg-5 mx-auto my-5">
            <h1 className="display-4 font-weight-normal">
              Маркетинговая хрень
            </h1>
            <p className="lead font-weight-normal">
              Подробная маркетинговая хрень
            </p>
            {/*<Button tag={Link} id="go-feedback" to="/entity/feedback" replace>
              <span className="d-none d-md-inline">
                    <Translate contentKey="global.menu.entities.feedback">Feedback</Translate>
              </span>
            </Button>*/}
          </div>
          <div className="product-device box-shadow d-none d-md-block"/>
          <div className="product-device product-device-2 box-shadow d-none d-md-block"/>
        </div>

        <div className="d-md-flex flex-md-equal w-100 my-md-3 pl-md-3">
          <div className="bg-dark mr-md-3 pt-3 px-3 pt-md-5 px-md-5 text-center text-white overflow-hidden">
            <div className="my-3 py-3">
              <h2 className="display-5">Другая маркетинговая хрень</h2>
              <p className="lead">Подробная маркетинговая хрень</p>
            </div>
            <div className="bg-light box-shadow mx-auto"
                 style={ cardStyle }/>
          </div>
          <div className="bg-light mr-md-3 pt-3 px-3 pt-md-5 px-md-5 text-center overflow-hidden">
            <div className="my-3 p-3">
              <h2 className="display-5">Другая маркетинговая хрень</h2>
              <p className="lead">Подробная маркетинговая хрень</p>
            </div>
            <div className="bg-dark box-shadow mx-auto"
                 style={ cardStyle }/>
          </div>
        </div>

        <div className="d-md-flex flex-md-equal w-100 my-md-3 pl-md-3">
          <div className="bg-light mr-md-3 pt-3 px-3 pt-md-5 px-md-5 text-center overflow-hidden">
            <div className="my-3 p-3">
              <h2 className="display-5">Другая маркетинговая хрень</h2>
              <p className="lead">Подробная маркетинговая хрень</p>
            </div>
            <div className="bg-dark box-shadow mx-auto"
                 style={ cardStyle }/>
          </div>
          <div className="bg-primary mr-md-3 pt-3 px-3 pt-md-5 px-md-5 text-center text-white overflow-hidden">
            <div className="my-3 py-3">
              <h2 className="display-5">Другая маркетинговая хрень</h2>
              <p className="lead">Подробная маркетинговая хрень</p>
            </div>
            <div className="bg-light box-shadow mx-auto"
                 style={ cardStyle }/>
          </div>
        </div>

        <div className="d-md-flex flex-md-equal w-100 my-md-3 pl-md-3">
          <div className="bg-light mr-md-3 pt-3 px-3 pt-md-5 px-md-5 text-center overflow-hidden">
            <div className="my-3 p-3">
              <h2 className="display-5">Другая маркетинговая хрень</h2>
              <p className="lead">Подробная маркетинговая хрень</p>
            </div>
            <div className="bg-white box-shadow mx-auto"
                 style={ cardStyle }/>
          </div>
          <div className="bg-light mr-md-3 pt-3 px-3 pt-md-5 px-md-5 text-center overflow-hidden">
            <div className="my-3 py-3">
              <h2 className="display-5">Другая маркетинговая хрень</h2>
              <p className="lead">Подробная маркетинговая хрень</p>
            </div>
            <div className="bg-white box-shadow mx-auto"
                 style={ cardStyle }/>
          </div>
        </div>

        <div className="d-md-flex flex-md-equal w-100 my-md-3 pl-md-3">
          <div className="bg-light mr-md-3 pt-3 px-3 pt-md-5 px-md-5 text-center overflow-hidden">
            <div className="my-3 p-3">
              <h2 className="display-5">Другая маркетинговая хрень</h2>
              <p className="lead">Подробная маркетинговая хрень</p>
            </div>
            <div className="bg-white box-shadow mx-auto"
                 style={ cardStyle }/>
          </div>
          <div className="bg-light mr-md-3 pt-3 px-3 pt-md-5 px-md-5 text-center overflow-hidden">
            <div className="my-3 py-3">
              <h2 className="display-5">Другая маркетинговая хрень</h2>
              <p className="lead">Подробная маркетинговая хрень</p>
            </div>
            <div className="bg-white box-shadow mx-auto"
                 style={ cardStyle }/>
          </div>
        </div>
      </div>
    );
  }
}

const mapStateToProps = storeState => ({
  account: storeState.authentication.account,
  isAuthenticated: storeState.authentication.isAuthenticated
});

const mapDispatchToProps = { };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Home);
