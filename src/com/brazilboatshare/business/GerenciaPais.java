package com.brazilboatshare.business;

import java.util.List;

import com.brazilboatshare.model.dao.PaisDao;
import com.brazilboatshare.model.entity.Pais;

public class GerenciaPais {

	public void salvar(Pais pais) {
		if (pais.getCodigo() != null && pais.getCodPaisFone() != null
				&& pais.getIdioma() != null && pais.getMoeda() != null && pais.getNome() != null) {
			new PaisDao().save(pais);
		}		
	}
	
	public void excluir(Pais pais) {
		if (pais != null) {
			new PaisDao().delete(pais);
		}		
	}
	
	public List<Pais> listar() {
		return new PaisDao().list(null, null, null);
	}
		
}
