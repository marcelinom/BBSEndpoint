package com.brazilboatshare.model.dao;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.brazilboatshare.util.FiltroPesquisa;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.LoadResult;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.Result;
import com.googlecode.objectify.cmd.Query;

 
public class ObjectifyDao<T> {
    private Class<T> clazz;
 
	static {
		ObjectifyRegistering.register();
	}

	/**
     * We've got to get the associated domain class somehow
     *
     * @param clazz
     */
    protected ObjectifyDao(Class<T> clazz) {
        this.clazz = clazz;
    }
 
    public Result<Key<T>> save(T entity) {
        return ofy().save().entity(entity);
    }
 
    public Key<T> saveNow(T entity) {
        return ofy().save().entity(entity).now();
    }
 
    public Result<Key<Iterable<T>>> save(Iterable<T> entities) {
        return ofy().save().entity(entities);
    }
 
    public void delete(T entity) {
    	ofy().delete().entity(entity);    // async without the now()
    }
 
    public void delete(Iterable<T> entities) {
    	ofy().delete().entities(entities);
    }
 
    public void deleteNow(T entity) {
    	ofy().delete().entity(entity).now(); 
    }
 
    public void delete(Key<T> entityKey) {
    	ofy().delete().key(entityKey);
    }
 
    public T get(Long id) {
    	return ofy().load().type(clazz).id(id).now();
    }
 
    public T get(String id) {
    	return ofy().load().type(clazz).id(id).now();
    }
 
    public T get(Key<T> key) {
    	return ofy().load().key(key).now();
    }
 
    @SuppressWarnings("unchecked")
	public T get(Ref<T> key) {
    	return (T) ofy().load().value(key).now();
    }
 
    public Map<Key<T>,T> get(Iterable<Ref<T>> iter) {
    	return ofy().load().values(iter);
    }
 
    public T getByProperty(String propName, Object propValue) {
    	return ofy().load().type(clazz).filter(propName, propValue).first().now();
    }
 
    public List<T> listByProperty(String propName, Object propValue) {
    	return ofy().load().type(clazz).filter(propName, propValue).list();
    }
 
    public List<T> get(List<FiltroPesquisa> filtro) {
    	Query<T> q = ofy().load().type(clazz);

    	if (filtro != null) {
        	// Find non-null properties and add to query
            for (FiltroPesquisa criterio : filtro) {
                q = q.filter(criterio.getExpressao(), criterio.getValor());
            }
    	}
        
        return q.list();
    }
 
    public T first(List<FiltroPesquisa> filtro) {
    	Query<T> q = ofy().load().type(clazz);

    	if (filtro != null) {
        	// Find non-null properties and add to query
            for (FiltroPesquisa criterio : filtro) {
                q = q.filter(criterio.getExpressao(), criterio.getValor());
            }
    	}
        
        return q.first().now();
    }
 
    public QueryResultIterator<T> list(Cursor cursor, int limite) {
    	Query<T> query = ofy().load().type(clazz).limit(limite);

        if (cursor != null) {
            query = query.startAt(cursor);
        }

        return query.iterator();
    }
 
    public List<T> list(List<FiltroPesquisa> filtro, List<String> order, Integer limite) {
    	Query<T> q = ofy().load().type(clazz);

    	if (filtro != null) {
        	for (FiltroPesquisa criterio : filtro) {
        		if (criterio.getExpressao() != null && criterio.getValor() != null) {
                    q = q.filter(criterio.getExpressao(), criterio.getValor());
        		}
            }
    	}
    	
    	if (order != null) {
        	for (String propriedade : order) {
                q = q.order(propriedade);
            }
    		
    	}
        
    	if (limite != null) {
    		q = q.limit(limite);
    	}
    	
        return q.list();
    }
 
    public LoadResult<T> getByExample(T u, String... matchProperties) {
    	Query<T> q = ofy().load().type(clazz);

    	// Find non-null properties and add to query
        for (String propName : matchProperties) {
            Object propValue = getPropertyValue(u, propName);
            q = q.filter(propName, propValue);
        }
        
        return q.first();
    }
 
    public List<T> listByExample(T u, String... matchProperties) {
    	Query<T> q = ofy().load().type(clazz);

    	// Find non-null properties and add to query
        for (String propName : matchProperties) {
            Object propValue = getPropertyValue(u, propName);
            q = q.filter(propName, propValue);
        }
        
        return q.list();
    }
 
    private Object getPropertyValue(Object obj, String propertyName) {
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(obj.getClass());
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
        
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String propName = propertyDescriptor.getName();
            if (propName.equals(propertyName)) {
                Method readMethod = propertyDescriptor.getReadMethod();
                try {
                    return readMethod.invoke(obj, new Object[] {});
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }
 
}