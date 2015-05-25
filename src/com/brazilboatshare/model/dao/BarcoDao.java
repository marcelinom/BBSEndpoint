package com.brazilboatshare.model.dao;

import java.util.ArrayList;
import java.util.List;

import com.brazilboatshare.model.entity.Barco;
import com.brazilboatshare.util.FiltroPesquisa;

public class BarcoDao extends ObjectifyDao<Barco> {

	public BarcoDao() {
		super(Barco.class);
	}

	public List<Barco> lista() {
		List<FiltroPesquisa> filtro = new ArrayList<FiltroPesquisa>();
		filtro.add(new FiltroPesquisa("ativa", true));
		return list(filtro, null, null);
	}

	
}
