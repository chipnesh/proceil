import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IMaterialRequest, defaultValue } from 'app/shared/model/material-request.model';

export const ACTION_TYPES = {
  FETCH_MATERIALREQUEST_LIST: 'materialRequest/FETCH_MATERIALREQUEST_LIST',
  FETCH_MATERIALREQUEST: 'materialRequest/FETCH_MATERIALREQUEST',
  CREATE_MATERIALREQUEST: 'materialRequest/CREATE_MATERIALREQUEST',
  UPDATE_MATERIALREQUEST: 'materialRequest/UPDATE_MATERIALREQUEST',
  DELETE_MATERIALREQUEST: 'materialRequest/DELETE_MATERIALREQUEST',
  SET_BLOB: 'materialRequest/SET_BLOB',
  RESET: 'materialRequest/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IMaterialRequest>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type MaterialRequestState = Readonly<typeof initialState>;

// Reducer

export default (state: MaterialRequestState = initialState, action): MaterialRequestState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_MATERIALREQUEST_LIST):
    case REQUEST(ACTION_TYPES.FETCH_MATERIALREQUEST):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_MATERIALREQUEST):
    case REQUEST(ACTION_TYPES.UPDATE_MATERIALREQUEST):
    case REQUEST(ACTION_TYPES.DELETE_MATERIALREQUEST):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.FETCH_MATERIALREQUEST_LIST):
    case FAILURE(ACTION_TYPES.FETCH_MATERIALREQUEST):
    case FAILURE(ACTION_TYPES.CREATE_MATERIALREQUEST):
    case FAILURE(ACTION_TYPES.UPDATE_MATERIALREQUEST):
    case FAILURE(ACTION_TYPES.DELETE_MATERIALREQUEST):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.FETCH_MATERIALREQUEST_LIST):
      return {
        ...state,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_MATERIALREQUEST):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_MATERIALREQUEST):
    case SUCCESS(ACTION_TYPES.UPDATE_MATERIALREQUEST):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_MATERIALREQUEST):
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

const apiUrl = 'api/material-requests';

// Actions

export const getEntities: ICrudGetAllAction<IMaterialRequest> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_MATERIALREQUEST_LIST,
    payload: axios.get<IMaterialRequest>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IMaterialRequest> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_MATERIALREQUEST,
    payload: axios.get<IMaterialRequest>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IMaterialRequest> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_MATERIALREQUEST,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IMaterialRequest> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_MATERIALREQUEST,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IMaterialRequest> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_MATERIALREQUEST,
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
