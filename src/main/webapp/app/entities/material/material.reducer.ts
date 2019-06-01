import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IMaterial, defaultValue } from 'app/shared/model/material.model';

export const ACTION_TYPES = {
  FETCH_MATERIAL_LIST: 'material/FETCH_MATERIAL_LIST',
  FETCH_MATERIAL: 'material/FETCH_MATERIAL',
  CREATE_MATERIAL: 'material/CREATE_MATERIAL',
  UPDATE_MATERIAL: 'material/UPDATE_MATERIAL',
  DELETE_MATERIAL: 'material/DELETE_MATERIAL',
  RESET: 'material/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IMaterial>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type MaterialState = Readonly<typeof initialState>;

// Reducer

export default (state: MaterialState = initialState, action): MaterialState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_MATERIAL_LIST):
    case REQUEST(ACTION_TYPES.FETCH_MATERIAL):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_MATERIAL):
    case REQUEST(ACTION_TYPES.UPDATE_MATERIAL):
    case REQUEST(ACTION_TYPES.DELETE_MATERIAL):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.FETCH_MATERIAL_LIST):
    case FAILURE(ACTION_TYPES.FETCH_MATERIAL):
    case FAILURE(ACTION_TYPES.CREATE_MATERIAL):
    case FAILURE(ACTION_TYPES.UPDATE_MATERIAL):
    case FAILURE(ACTION_TYPES.DELETE_MATERIAL):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.FETCH_MATERIAL_LIST):
      return {
        ...state,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_MATERIAL):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_MATERIAL):
    case SUCCESS(ACTION_TYPES.UPDATE_MATERIAL):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_MATERIAL):
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

const apiUrl = 'api/materials';

// Actions

export const getEntities: ICrudGetAllAction<IMaterial> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_MATERIAL_LIST,
    payload: axios.get<IMaterial>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IMaterial> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_MATERIAL,
    payload: axios.get<IMaterial>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IMaterial> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_MATERIAL,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IMaterial> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_MATERIAL,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IMaterial> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_MATERIAL,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
