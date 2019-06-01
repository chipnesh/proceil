import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IMaterialArrival, defaultValue } from 'app/shared/model/material-arrival.model';

export const ACTION_TYPES = {
  FETCH_MATERIALARRIVAL_LIST: 'materialArrival/FETCH_MATERIALARRIVAL_LIST',
  FETCH_MATERIALARRIVAL: 'materialArrival/FETCH_MATERIALARRIVAL',
  CREATE_MATERIALARRIVAL: 'materialArrival/CREATE_MATERIALARRIVAL',
  UPDATE_MATERIALARRIVAL: 'materialArrival/UPDATE_MATERIALARRIVAL',
  DELETE_MATERIALARRIVAL: 'materialArrival/DELETE_MATERIALARRIVAL',
  SET_BLOB: 'materialArrival/SET_BLOB',
  RESET: 'materialArrival/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IMaterialArrival>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type MaterialArrivalState = Readonly<typeof initialState>;

// Reducer

export default (state: MaterialArrivalState = initialState, action): MaterialArrivalState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_MATERIALARRIVAL_LIST):
    case REQUEST(ACTION_TYPES.FETCH_MATERIALARRIVAL):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_MATERIALARRIVAL):
    case REQUEST(ACTION_TYPES.UPDATE_MATERIALARRIVAL):
    case REQUEST(ACTION_TYPES.DELETE_MATERIALARRIVAL):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.FETCH_MATERIALARRIVAL_LIST):
    case FAILURE(ACTION_TYPES.FETCH_MATERIALARRIVAL):
    case FAILURE(ACTION_TYPES.CREATE_MATERIALARRIVAL):
    case FAILURE(ACTION_TYPES.UPDATE_MATERIALARRIVAL):
    case FAILURE(ACTION_TYPES.DELETE_MATERIALARRIVAL):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.FETCH_MATERIALARRIVAL_LIST):
      return {
        ...state,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_MATERIALARRIVAL):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_MATERIALARRIVAL):
    case SUCCESS(ACTION_TYPES.UPDATE_MATERIALARRIVAL):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_MATERIALARRIVAL):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {}
      };
    case ACTION_TYPES.SET_BLOB:
      const { name, data, contentType } = action.payload;
      return {
        ...state,
        entity: {
          ...state.entity,
          [name]: data,
          [name + 'ContentType']: contentType
        }
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState
      };
    default:
      return state;
  }
};

const apiUrl = 'api/material-arrivals';

// Actions

export const getEntities: ICrudGetAllAction<IMaterialArrival> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_MATERIALARRIVAL_LIST,
    payload: axios.get<IMaterialArrival>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IMaterialArrival> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_MATERIALARRIVAL,
    payload: axios.get<IMaterialArrival>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IMaterialArrival> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_MATERIALARRIVAL,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IMaterialArrival> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_MATERIALARRIVAL,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IMaterialArrival> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_MATERIALARRIVAL,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const setBlob = (name, data, contentType?) => ({
  type: ACTION_TYPES.SET_BLOB,
  payload: {
    name,
    data,
    contentType
  }
});

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
