import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IOrderMaterial, defaultValue } from 'app/shared/model/order-material.model';

export const ACTION_TYPES = {
  FETCH_ORDERMATERIAL_LIST: 'orderMaterial/FETCH_ORDERMATERIAL_LIST',
  FETCH_ORDERMATERIAL: 'orderMaterial/FETCH_ORDERMATERIAL',
  CREATE_ORDERMATERIAL: 'orderMaterial/CREATE_ORDERMATERIAL',
  UPDATE_ORDERMATERIAL: 'orderMaterial/UPDATE_ORDERMATERIAL',
  DELETE_ORDERMATERIAL: 'orderMaterial/DELETE_ORDERMATERIAL',
  RESET: 'orderMaterial/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IOrderMaterial>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type OrderMaterialState = Readonly<typeof initialState>;

// Reducer

export default (state: OrderMaterialState = initialState, action): OrderMaterialState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_ORDERMATERIAL_LIST):
    case REQUEST(ACTION_TYPES.FETCH_ORDERMATERIAL):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_ORDERMATERIAL):
    case REQUEST(ACTION_TYPES.UPDATE_ORDERMATERIAL):
    case REQUEST(ACTION_TYPES.DELETE_ORDERMATERIAL):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.FETCH_ORDERMATERIAL_LIST):
    case FAILURE(ACTION_TYPES.FETCH_ORDERMATERIAL):
    case FAILURE(ACTION_TYPES.CREATE_ORDERMATERIAL):
    case FAILURE(ACTION_TYPES.UPDATE_ORDERMATERIAL):
    case FAILURE(ACTION_TYPES.DELETE_ORDERMATERIAL):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.FETCH_ORDERMATERIAL_LIST):
      return {
        ...state,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_ORDERMATERIAL):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_ORDERMATERIAL):
    case SUCCESS(ACTION_TYPES.UPDATE_ORDERMATERIAL):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_ORDERMATERIAL):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {}
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState
      };
    default:
      return state;
  }
};

const apiUrl = 'api/order-materials';

// Actions

export const getEntities: ICrudGetAllAction<IOrderMaterial> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_ORDERMATERIAL_LIST,
    payload: axios.get<IOrderMaterial>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IOrderMaterial> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_ORDERMATERIAL,
    payload: axios.get<IOrderMaterial>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IOrderMaterial> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_ORDERMATERIAL,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IOrderMaterial> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_ORDERMATERIAL,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IOrderMaterial> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_ORDERMATERIAL,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
