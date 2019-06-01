import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IAttachedImage, defaultValue } from 'app/shared/model/attached-image.model';

export const ACTION_TYPES = {
  FETCH_ATTACHEDIMAGE_LIST: 'attachedImage/FETCH_ATTACHEDIMAGE_LIST',
  FETCH_ATTACHEDIMAGE: 'attachedImage/FETCH_ATTACHEDIMAGE',
  CREATE_ATTACHEDIMAGE: 'attachedImage/CREATE_ATTACHEDIMAGE',
  UPDATE_ATTACHEDIMAGE: 'attachedImage/UPDATE_ATTACHEDIMAGE',
  DELETE_ATTACHEDIMAGE: 'attachedImage/DELETE_ATTACHEDIMAGE',
  SET_BLOB: 'attachedImage/SET_BLOB',
  RESET: 'attachedImage/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IAttachedImage>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type AttachedImageState = Readonly<typeof initialState>;

// Reducer

export default (state: AttachedImageState = initialState, action): AttachedImageState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_ATTACHEDIMAGE_LIST):
    case REQUEST(ACTION_TYPES.FETCH_ATTACHEDIMAGE):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_ATTACHEDIMAGE):
    case REQUEST(ACTION_TYPES.UPDATE_ATTACHEDIMAGE):
    case REQUEST(ACTION_TYPES.DELETE_ATTACHEDIMAGE):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.FETCH_ATTACHEDIMAGE_LIST):
    case FAILURE(ACTION_TYPES.FETCH_ATTACHEDIMAGE):
    case FAILURE(ACTION_TYPES.CREATE_ATTACHEDIMAGE):
    case FAILURE(ACTION_TYPES.UPDATE_ATTACHEDIMAGE):
    case FAILURE(ACTION_TYPES.DELETE_ATTACHEDIMAGE):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.FETCH_ATTACHEDIMAGE_LIST):
      return {
        ...state,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_ATTACHEDIMAGE):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_ATTACHEDIMAGE):
    case SUCCESS(ACTION_TYPES.UPDATE_ATTACHEDIMAGE):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_ATTACHEDIMAGE):
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

const apiUrl = 'api/attached-images';

// Actions

export const getEntities: ICrudGetAllAction<IAttachedImage> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_ATTACHEDIMAGE_LIST,
    payload: axios.get<IAttachedImage>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IAttachedImage> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_ATTACHEDIMAGE,
    payload: axios.get<IAttachedImage>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IAttachedImage> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_ATTACHEDIMAGE,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IAttachedImage> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_ATTACHEDIMAGE,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IAttachedImage> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_ATTACHEDIMAGE,
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
