package com.brazilboatshare.model.dao;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;

import com.brazilboatshare.model.entity.Cota;
import com.brazilboatshare.model.entity.Dependente;
import com.brazilboatshare.util.FiltroPesquisa;
import com.googlecode.objectify.VoidWork;

public class DependenteDao extends ObjectifyDao<Dependente> {

	public DependenteDao() {
		super(Dependente.class);
	}

    public void salva(final Dependente dep, final Cota cota) {
        ofy().transact(new VoidWork() {	//mesma transacao!
            public void vrun() {
            	new CotaDao().save(cota);
                save(dep);
            }
        });
    }
    	
	public Dependente buscar(Long cota) {
		List<FiltroPesquisa> filtro = new ArrayList<FiltroPesquisa>();		
		filtro.add(new FiltroPesquisa("cota", cota));

		return first(filtro);
	}	
	
	
}
