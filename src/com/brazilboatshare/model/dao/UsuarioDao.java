package com.brazilboatshare.model.dao;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;

import com.brazilboatshare.model.entity.Confirmacao;
import com.brazilboatshare.model.entity.Usuario;
import com.googlecode.objectify.cmd.Query;

public class UsuarioDao extends ObjectifyDao<Usuario> {

	public UsuarioDao() {
		super(Usuario.class);
	}

	public void salvaLinkConfirmacao(Confirmacao link) {
        ofy().save().entity(link).now();
	}
	
	public Confirmacao buscaConfirmacao(Long chave) {
		return ofy().load().type(Confirmacao.class).id(chave).now();
	}
	
	public List<Confirmacao> buscaConfirmacao(String usuario) {
    	Query<Confirmacao> q = ofy().load().type(Confirmacao.class);
        return q.filter("usuario", usuario).list();
	}
	    
	public void excluiConfirmacao(Confirmacao confirmacao) {
		ofy().delete().entity(confirmacao);
	}
	
}
