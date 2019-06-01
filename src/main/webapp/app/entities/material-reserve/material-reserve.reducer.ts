import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IMaterialReserve, defaultValue } from 'app/shared/model/material-reserve.model';

export const ACTION_TYPES = {
  FETCH_MATERIALRESERVE_LIST: 'materialReserve/FETCH_MATERIALRESERVE_LIST',
  FETCH_MATERIALRESERVE: 'materialReserve/FETCH_MATERIALRESERVE',
  CREATE_MATERIALRESERVE: 'materialReserve/CREATE_MATERIALRESERVE',
  UPDATE_MATERIALRESERVE: 'materialReserve/UPDATE_MATERIALRESERVE',
  DELETE_MATERIALRESERVE: 'materialReserve/DELETE_MATERIALRESERVE',
  RESET: 'materialReserve/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IMaterialReserve>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type MaterialReserveState = Readonly<typeof initialState>;

// Reducer

export default (state: MaterialReserveState = initialState, action): MaterialReserveState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_MATERIALRESERVE_LIST):
    case REQUEST(ACTION_TYPES.FETCH_MATERIALRESERVE):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_MATERIALRESERVE):
    case REQUEST(ACTION_TYPES.UPDATE_MATERIALRESERVE):
    case REQUEST(ACTION_TYPES.DELETE_MATERIALRESERVE):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.FETCH_MATERIALRESERVE_LIST):
    case FAILURE(ACTION_TYPES.FETCH_MATERIALRESERVE):
    case FAILURE(ACTION_TYPES.CREATE_MATERIALRESERVE):
    case FAILURE(ACTION_TYPES.UPDATE_MATERIALRESERVE):
    case FAILURE(ACTION_TYPES.DELETE_MATERIALRESERVE):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.FETCH_MATERIALRESERVE_LIST):
      return {
        ...state,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_MATERIALRESERVE):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_MATERIALRESERVE):
    case SUCCESS(ACTION_TYPES.UPDATE_MATERIALRESERVE):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_MATERIALRESERVE):
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

const apiUrl = 'api/material-reserves';

// Actions

export const getEntities: ICrudGetAllAction<IMaterialReserve> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_MATERIALRESERVE_LIST,
    payload: axios.get<IMaterialReserve>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IMaterialReserve> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_MATERIALRESERVE,
    payload: axios.get<IMaterialReserve>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IMaterialReserve> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_MATERIALRESERVE,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IMaterialReserve> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_MATERIALRESERVE,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IMaterialReserve> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_MATERIALRESERVE,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
