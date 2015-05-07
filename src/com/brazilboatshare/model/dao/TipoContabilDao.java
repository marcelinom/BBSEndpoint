package com.brazilboatshare.model.dao;
import com.brazilboatshare.model.entity.TipoContabil;
import com.googlecode.objectify.Key;

public class TipoContabilDao extends ObjectifyDao<TipoContabil> {
	 
    public TipoContabilDao() {
        super(TipoContabil.class);
    }
 
    public void delete(String codigo) {
		delete(Key.create(null, TipoContabil.class, codigo));
    }
	    
}
