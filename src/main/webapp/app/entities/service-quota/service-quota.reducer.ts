import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IServiceQuota, defaultValue } from 'app/shared/model/service-quota.model';

export const ACTION_TYPES = {
  FETCH_SERVICEQUOTA_LIST: 'serviceQuota/FETCH_SERVICEQUOTA_LIST',
  FETCH_SERVICEQUOTA: 'serviceQuota/FETCH_SERVICEQUOTA',
  CREATE_SERVICEQUOTA: 'serviceQuota/CREATE_SERVICEQUOTA',
  UPDATE_SERVICEQUOTA: 'serviceQuota/UPDATE_SERVICEQUOTA',
  DELETE_SERVICEQUOTA: 'serviceQuota/DELETE_SERVICEQUOTA',
  RESET: 'serviceQuota/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IServiceQuota>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type ServiceQuotaState = Readonly<typeof initialState>;

// Reducer

export default (state: ServiceQuotaState = initialState, action): ServiceQuotaState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_SERVICEQUOTA_LIST):
    case REQUEST(ACTION_TYPES.FETCH_SERVICEQUOTA):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_SERVICEQUOTA):
    case REQUEST(ACTION_TYPES.UPDATE_SERVICEQUOTA):
    case REQUEST(ACTION_TYPES.DELETE_SERVICEQUOTA):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.FETCH_SERVICEQUOTA_LIST):
    case FAILURE(ACTION_TYPES.FETCH_SERVICEQUOTA):
    case FAILURE(ACTION_TYPES.CREATE_SERVICEQUOTA):
    case FAILURE(ACTION_TYPES.UPDATE_SERVICEQUOTA):
    case FAILURE(ACTION_TYPES.DELETE_SERVICEQUOTA):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.FETCH_SERVICEQUOTA_LIST):
      return {
        ...state,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_SERVICEQUOTA):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_SERVICEQUOTA):
    case SUCCESS(ACTION_TYPES.UPDATE_SERVICEQUOTA):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_SERVICEQUOTA):
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

const apiUrl = 'api/service-quotas';

// Actions

export const getEntities: ICrudGetAllAction<IServiceQuota> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_SERVICEQUOTA_LIST,
    payload: axios.get<IServiceQuota>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IServiceQuota> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_SERVICEQUOTA,
    payload: axios.get<IServiceQuota>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IServiceQuota> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_SERVICEQUOTA,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IServiceQuota> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_SERVICEQUOTA,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IServiceQuota> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_SERVICEQUOTA,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
