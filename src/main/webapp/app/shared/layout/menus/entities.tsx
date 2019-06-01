import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';
import { Translate, translate } from 'react-jhipster';
import { Col, Container, Row, DropdownItem } from 'reactstrap';
import { NavDropdown } from './menu-components';

export const EntitiesMenu = props => (
  // tslint:disable-next-line:jsx-self-close

  <Container>
    <Row>
      <Col>
        <NavDropdown icon="list-alt" name={translate('global.menu.entities.catalog')} id="entity-catalog">
          <MenuItem icon="asterisk" to="/entity/material">
            <Translate contentKey="global.menu.entities.material" />
          </MenuItem>
          <MenuItem icon="asterisk" to="/entity/service">
            <Translate contentKey="global.menu.entities.service" />
          </MenuItem>
          <MenuItem icon="asterisk" to="/entity/attached-image">
            <Translate contentKey="global.menu.entities.attachedImage" />
          </MenuItem>
        </NavDropdown>
      </Col>
      <Col>
        <NavDropdown icon="warehouse" name={translate('global.menu.entities.warehouse')} id="entity-warehouse">
          <MenuItem icon="asterisk" to="/entity/material-request">
            <Translate contentKey="global.menu.entities.materialRequest" />
          </MenuItem>
          <MenuItem icon="asterisk" to="/entity/material-arrival">
            <Translate contentKey="global.menu.entities.materialArrival" />
          </MenuItem>
          <MenuItem icon="asterisk" to="/entity/material-availability">
            <Translate contentKey="global.menu.entities.materialAvailability" />
          </MenuItem>
          <MenuItem icon="asterisk" to="/entity/material-reserve">
            <Translate contentKey="global.menu.entities.materialReserve" />
          </MenuItem>
          <DropdownItem divider />
          <MenuItem icon="asterisk" to="/entity/service-availability">
            <Translate contentKey="global.menu.entities.serviceAvailability" />
          </MenuItem>
          <MenuItem icon="asterisk" to="/entity/service-quota">
            <Translate contentKey="global.menu.entities.serviceQuota" />
          </MenuItem>
          <DropdownItem divider />
          <MenuItem icon="asterisk" to="/entity/facility">
            <Translate contentKey="global.menu.entities.facility" />
          </MenuItem>
          <MenuItem icon="asterisk" to="/entity/zone">
            <Translate contentKey="global.menu.entities.zone" />
          </MenuItem>
        </NavDropdown>
      </Col>
      <Col>
        <NavDropdown icon="user-friends" name={translate('global.menu.entities.client')} id="entity-client">
          <MenuItem icon="asterisk" to="/entity/customer">
            <Translate contentKey="global.menu.entities.customer" />
          </MenuItem>
          <MenuItem icon="asterisk" to="/entity/feedback">
            <Translate contentKey="global.menu.entities.feedback" />
          </MenuItem>
          <MenuItem icon="asterisk" to="/entity/employee">
            <Translate contentKey="global.menu.entities.employee" />
          </MenuItem>
        </NavDropdown>
      </Col>
      <Col>
        <NavDropdown icon="ruler-combined" name={translate('global.menu.entities.measurement')} id="entity-measurement">
          <MenuItem icon="asterisk" to="/entity/material-measurement">
            <Translate contentKey="global.menu.entities.materialMeasurement" />
          </MenuItem>
          <MenuItem icon="asterisk" to="/entity/measurement">
            <Translate contentKey="global.menu.entities.measurement" />
          </MenuItem>
        </NavDropdown>
      </Col>
      <Col>
        <NavDropdown icon="file-invoice-dollar" name={translate('global.menu.entities.order')} id="entity-order">
          <MenuItem icon="asterisk" to="/entity/customer-order">
            <Translate contentKey="global.menu.entities.customerOrder" />
          </MenuItem>
          <MenuItem icon="asterisk" to="/entity/order-material">
            <Translate contentKey="global.menu.entities.orderMaterial" />
          </MenuItem>
          <MenuItem icon="asterisk" to="/entity/order-service">
            <Translate contentKey="global.menu.entities.orderService" />
          </MenuItem>
          {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
        </NavDropdown>
      </Col>
    </Row>
  </Container>
);
