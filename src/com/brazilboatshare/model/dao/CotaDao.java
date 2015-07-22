package com.brazilboatshare.model.dao;

import java.util.ArrayList;
import java.util.List;

import com.brazilboatshare.model.entity.Cota;
import com.brazilboatshare.util.FiltroPesquisa;

public class CotaDao extends ObjectifyDao<Cota> {

	public CotaDao() {
		super(Cota.class);
	}

	public List<Cota> lista(String usuario) {
		if (usuario != null) {
			List<FiltroPesquisa> filtro = new ArrayList<FiltroPesquisa>();
			filtro.add(new FiltroPesquisa("usuario", usuario));
			List<Cota> cotas = list(filtro, null, null);
			
			// acrescentar as cotas que tem acesso como dependente
			filtro.clear();
			filtro.add(new FiltroPesquisa("dependente", usuario));
			if (cotas == null) {
				cotas = list(filtro, null, null);
			} else {
				cotas.addAll(list(filtro, null, null));
			}
			
			if (cotas != null) {
				boolean primeiro = true;
				for (Cota cota : cotas) {
					if (primeiro) cota.setAtual("@");
					else cota.setAtual("#");
					primeiro = false;
				}
			}
			return cotas;
		} else {
			return null;
		}
	}
	
	public List<Cota> cotasAtivasDoBarco(String barco) {
		List<FiltroPesquisa> filtro = new ArrayList<FiltroPesquisa>();
		filtro.add(new FiltroPesquisa("barco", barco));
		filtro.add(new FiltroPesquisa("status !=", Cota.Status.CANCELADA));
		return list(filtro, null, null);
	}
	
	public boolean temCotaDoBarco(String usuario, String barco) {
		List<FiltroPesquisa> filtro = new ArrayList<FiltroPesquisa>();
		filtro.add(new FiltroPesquisa("barco", barco));
		filtro.add(new FiltroPesquisa("usuario", usuario));
		List<Cota> cotas = list(filtro, null, null);
		if (cotas == null || cotas.size() == 0) {
			// cotas que tem acesso como dependente
			filtro.clear();
			filtro.add(new FiltroPesquisa("barco", barco));
			filtro.add(new FiltroPesquisa("dependente", usuario));
			cotas = list(filtro, null, null);
			return cotas != null && cotas.size() > 0;
		} else {
			return true;
		}
	}
	
	
}
