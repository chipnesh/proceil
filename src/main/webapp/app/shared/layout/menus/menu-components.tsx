import React from 'react';

import { DropdownMenu, DropdownToggle, UncontrolledDropdown } from 'reactstrap';
import { library } from '@fortawesome/fontawesome-svg-core';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { fas } from '@fortawesome/free-solid-svg-icons';
import { fab } from '@fortawesome/free-brands-svg-icons';

library.add(fab, fas);

export const NavDropdown = props => (
  <UncontrolledDropdown nav inNavbar id={ props.id }>
    <DropdownToggle nav caret className="d-flex align-items-center">
      <FontAwesomeIcon icon={ props.icon }/>
      <span>{ props.name }</span>
    </DropdownToggle>
    <DropdownMenu right style={ props.style }>
      { props.children }
    </DropdownMenu>
  </UncontrolledDropdown>
);
