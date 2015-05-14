package com.brazilboatshare.model.dao;

import com.brazilboatshare.model.entity.Projeto;

public class ProjetoDao extends ObjectifyDao<Projeto> {

	public ProjetoDao() {
		super(Projeto.class);
	}

}
