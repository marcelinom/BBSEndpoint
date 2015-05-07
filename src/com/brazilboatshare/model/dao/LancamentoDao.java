package com.brazilboatshare.model.dao;

import com.brazilboatshare.model.entity.Lancamento;

public class LancamentoDao extends ObjectifyDao<Lancamento> {

	public LancamentoDao() {
		super(Lancamento.class);
	}

}
