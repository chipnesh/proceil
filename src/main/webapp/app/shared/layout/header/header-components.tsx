import React from 'react';
import { Translate } from 'react-jhipster';

import { NavbarBrand } from 'reactstrap';
import { NavLink as Link } from 'react-router-dom';

export const BrandIcon = props => (
  <div { ...props } className="brand-icon">
    <img src="content/images/scd_logo32.png" alt="Logo"/>
  </div>
);

export const Brand = props => (
  <NavbarBrand tag={ Link } to="/" className="brand-logo">
    <BrandIcon/>
    <span className="brand-title">
      <Translate contentKey="global.title">Proceil</Translate>
    </span>
  </NavbarBrand>
);
