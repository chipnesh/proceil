import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IMaterialMeasurement, defaultValue } from 'app/shared/model/material-measurement.model';

export const ACTION_TYPES = {
  FETCH_MATERIALMEASUREMENT_LIST: 'materialMeasurement/FETCH_MATERIALMEASUREMENT_LIST',
  FETCH_MATERIALMEASUREMENT: 'materialMeasurement/FETCH_MATERIALMEASUREMENT',
  CREATE_MATERIALMEASUREMENT: 'materialMeasurement/CREATE_MATERIALMEASUREMENT',
  UPDATE_MATERIALMEASUREMENT: 'materialMeasurement/UPDATE_MATERIALMEASUREMENT',
  DELETE_MATERIALMEASUREMENT: 'materialMeasurement/DELETE_MATERIALMEASUREMENT',
  RESET: 'materialMeasurement/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IMaterialMeasurement>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type MaterialMeasurementState = Readonly<typeof initialState>;

// Reducer

export default (state: MaterialMeasurementState = initialState, action): MaterialMeasurementState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_MATERIALMEASUREMENT_LIST):
    case REQUEST(ACTION_TYPES.FETCH_MATERIALMEASUREMENT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_MATERIALMEASUREMENT):
    case REQUEST(ACTION_TYPES.UPDATE_MATERIALMEASUREMENT):
    case REQUEST(ACTION_TYPES.DELETE_MATERIALMEASUREMENT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.FETCH_MATERIALMEASUREMENT_LIST):
    case FAILURE(ACTION_TYPES.FETCH_MATERIALMEASUREMENT):
    case FAILURE(ACTION_TYPES.CREATE_MATERIALMEASUREMENT):
    case FAILURE(ACTION_TYPES.UPDATE_MATERIALMEASUREMENT):
    case FAILURE(ACTION_TYPES.DELETE_MATERIALMEASUREMENT):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.FETCH_MATERIALMEASUREMENT_LIST):
      return {
        ...state,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_MATERIALMEASUREMENT):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_MATERIALMEASUREMENT):
    case SUCCESS(ACTION_TYPES.UPDATE_MATERIALMEASUREMENT):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_MATERIALMEASUREMENT):
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

const apiUrl = 'api/material-measurements';

// Actions

export const getEntities: ICrudGetAllAction<IMaterialMeasurement> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_MATERIALMEASUREMENT_LIST,
    payload: axios.get<IMaterialMeasurement>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IMaterialMeasurement> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_MATERIALMEASUREMENT,
    payload: axios.get<IMaterialMeasurement>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IMaterialMeasurement> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_MATERIALMEASUREMENT,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IMaterialMeasurement> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_MATERIALMEASUREMENT,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IMaterialMeasurement> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_MATERIALMEASUREMENT,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
