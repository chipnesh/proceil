import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IMaterialAvailability, defaultValue } from 'app/shared/model/material-availability.model';

export const ACTION_TYPES = {
  FETCH_MATERIALAVAILABILITY_LIST: 'materialAvailability/FETCH_MATERIALAVAILABILITY_LIST',
  FETCH_MATERIALAVAILABILITY: 'materialAvailability/FETCH_MATERIALAVAILABILITY',
  CREATE_MATERIALAVAILABILITY: 'materialAvailability/CREATE_MATERIALAVAILABILITY',
  UPDATE_MATERIALAVAILABILITY: 'materialAvailability/UPDATE_MATERIALAVAILABILITY',
  DELETE_MATERIALAVAILABILITY: 'materialAvailability/DELETE_MATERIALAVAILABILITY',
  RESET: 'materialAvailability/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IMaterialAvailability>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type MaterialAvailabilityState = Readonly<typeof initialState>;

// Reducer

export default (state: MaterialAvailabilityState = initialState, action): MaterialAvailabilityState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_MATERIALAVAILABILITY_LIST):
    case REQUEST(ACTION_TYPES.FETCH_MATERIALAVAILABILITY):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_MATERIALAVAILABILITY):
    case REQUEST(ACTION_TYPES.UPDATE_MATERIALAVAILABILITY):
    case REQUEST(ACTION_TYPES.DELETE_MATERIALAVAILABILITY):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.FETCH_MATERIALAVAILABILITY_LIST):
    case FAILURE(ACTION_TYPES.FETCH_MATERIALAVAILABILITY):
    case FAILURE(ACTION_TYPES.CREATE_MATERIALAVAILABILITY):
    case FAILURE(ACTION_TYPES.UPDATE_MATERIALAVAILABILITY):
    case FAILURE(ACTION_TYPES.DELETE_MATERIALAVAILABILITY):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.FETCH_MATERIALAVAILABILITY_LIST):
      return {
        ...state,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_MATERIALAVAILABILITY):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_MATERIALAVAILABILITY):
    case SUCCESS(ACTION_TYPES.UPDATE_MATERIALAVAILABILITY):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_MATERIALAVAILABILITY):
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

const apiUrl = 'api/material-availabilities';

// Actions

export const getEntities: ICrudGetAllAction<IMaterialAvailability> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_MATERIALAVAILABILITY_LIST,
    payload: axios.get<IMaterialAvailability>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IMaterialAvailability> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_MATERIALAVAILABILITY,
    payload: axios.get<IMaterialAvailability>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IMaterialAvailability> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_MATERIALAVAILABILITY,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IMaterialAvailability> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_MATERIALAVAILABILITY,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IMaterialAvailability> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_MATERIALAVAILABILITY,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
