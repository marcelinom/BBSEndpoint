package com.brazilboatshare.model.dao;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;

import com.brazilboatshare.model.entity.ContaCorrente;
import com.brazilboatshare.util.FiltroPesquisa;
import com.googlecode.objectify.VoidWork;

public class ContaCorrenteDao extends ObjectifyDao<ContaCorrente> {

	public ContaCorrenteDao() {
		super(ContaCorrente.class);
	}

    public void debitar(final ShardedCounterDao sDao, final ContaCorrente cc) {
    	if (sDao != null && cc != null && cc.getValor() != null) {
            ofy().transact(new VoidWork() {	//mesma transacao!
                public void vrun() {
                	saveNow(cc);
    	            sDao.acrescentar(cc.getValor().negate());
                }
            });
    	}
    }	

    public void creditar(final ShardedCounterDao sDao, final ContaCorrente cc) {
    	if (sDao != null && cc != null && cc.getValor() != null) {
            ofy().transact(new VoidWork() {	//mesma transacao!
                public void vrun() {
                	saveNow(cc);
    	            sDao.acrescentar(cc.getValor());
                }
            });
    	}
    }	
    	
	public List<ContaCorrente> listarUtilizacao(String usuario) {
		List<FiltroPesquisa> filtro = new ArrayList<FiltroPesquisa>();		
		filtro.add(new FiltroPesquisa("usuario", usuario));

		List<String> order = new ArrayList<String>();		
		order.add("data");
		
		return list(filtro, order, null);
	}	
	
}
