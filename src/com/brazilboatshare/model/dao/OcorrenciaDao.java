package com.brazilboatshare.model.dao;

import com.brazilboatshare.model.entity.Ocorrencia;

public class OcorrenciaDao extends ObjectifyDao<Ocorrencia> {

	public OcorrenciaDao() {
		super(Ocorrencia.class);
	}

}
