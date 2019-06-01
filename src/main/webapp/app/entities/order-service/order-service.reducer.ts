import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IOrderService, defaultValue } from 'app/shared/model/order-service.model';

export const ACTION_TYPES = {
  FETCH_ORDERSERVICE_LIST: 'orderService/FETCH_ORDERSERVICE_LIST',
  FETCH_ORDERSERVICE: 'orderService/FETCH_ORDERSERVICE',
  CREATE_ORDERSERVICE: 'orderService/CREATE_ORDERSERVICE',
  UPDATE_ORDERSERVICE: 'orderService/UPDATE_ORDERSERVICE',
  DELETE_ORDERSERVICE: 'orderService/DELETE_ORDERSERVICE',
  RESET: 'orderService/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IOrderService>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type OrderServiceState = Readonly<typeof initialState>;

// Reducer

export default (state: OrderServiceState = initialState, action): OrderServiceState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_ORDERSERVICE_LIST):
    case REQUEST(ACTION_TYPES.FETCH_ORDERSERVICE):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_ORDERSERVICE):
    case REQUEST(ACTION_TYPES.UPDATE_ORDERSERVICE):
    case REQUEST(ACTION_TYPES.DELETE_ORDERSERVICE):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.FETCH_ORDERSERVICE_LIST):
    case FAILURE(ACTION_TYPES.FETCH_ORDERSERVICE):
    case FAILURE(ACTION_TYPES.CREATE_ORDERSERVICE):
    case FAILURE(ACTION_TYPES.UPDATE_ORDERSERVICE):
    case FAILURE(ACTION_TYPES.DELETE_ORDERSERVICE):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.FETCH_ORDERSERVICE_LIST):
      return {
        ...state,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_ORDERSERVICE):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_ORDERSERVICE):
    case SUCCESS(ACTION_TYPES.UPDATE_ORDERSERVICE):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_ORDERSERVICE):
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

const apiUrl = 'api/order-services';

// Actions

export const getEntities: ICrudGetAllAction<IOrderService> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_ORDERSERVICE_LIST,
    payload: axios.get<IOrderService>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IOrderService> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_ORDERSERVICE,
    payload: axios.get<IOrderService>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IOrderService> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_ORDERSERVICE,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IOrderService> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_ORDERSERVICE,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IOrderService> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_ORDERSERVICE,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
