import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IMeasurement, defaultValue } from 'app/shared/model/measurement.model';

export const ACTION_TYPES = {
  FETCH_MEASUREMENT_LIST: 'measurement/FETCH_MEASUREMENT_LIST',
  FETCH_MEASUREMENT: 'measurement/FETCH_MEASUREMENT',
  CREATE_MEASUREMENT: 'measurement/CREATE_MEASUREMENT',
  UPDATE_MEASUREMENT: 'measurement/UPDATE_MEASUREMENT',
  DELETE_MEASUREMENT: 'measurement/DELETE_MEASUREMENT',
  SET_BLOB: 'measurement/SET_BLOB',
  RESET: 'measurement/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IMeasurement>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type MeasurementState = Readonly<typeof initialState>;

// Reducer

export default (state: MeasurementState = initialState, action): MeasurementState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_MEASUREMENT_LIST):
    case REQUEST(ACTION_TYPES.FETCH_MEASUREMENT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_MEASUREMENT):
    case REQUEST(ACTION_TYPES.UPDATE_MEASUREMENT):
    case REQUEST(ACTION_TYPES.DELETE_MEASUREMENT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.FETCH_MEASUREMENT_LIST):
    case FAILURE(ACTION_TYPES.FETCH_MEASUREMENT):
    case FAILURE(ACTION_TYPES.CREATE_MEASUREMENT):
    case FAILURE(ACTION_TYPES.UPDATE_MEASUREMENT):
    case FAILURE(ACTION_TYPES.DELETE_MEASUREMENT):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.FETCH_MEASUREMENT_LIST):
      return {
        ...state,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_MEASUREMENT):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_MEASUREMENT):
    case SUCCESS(ACTION_TYPES.UPDATE_MEASUREMENT):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_MEASUREMENT):
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

const apiUrl = 'api/measurements';

// Actions

export const getEntities: ICrudGetAllAction<IMeasurement> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_MEASUREMENT_LIST,
    payload: axios.get<IMeasurement>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IMeasurement> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_MEASUREMENT,
    payload: axios.get<IMeasurement>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IMeasurement> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_MEASUREMENT,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IMeasurement> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_MEASUREMENT,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IMeasurement> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_MEASUREMENT,
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
