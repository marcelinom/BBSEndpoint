package com.brazilboatshare.business;

import java.util.Date;
import java.util.List;

import com.brazilboatshare.exception.RegraNegocioException;
import com.brazilboatshare.model.dao.BarcoDao;
import com.brazilboatshare.model.dao.ProjetoDao;
import com.brazilboatshare.model.dto.ViewBarco;
import com.brazilboatshare.model.entity.Barco;
import com.brazilboatshare.model.entity.Projeto;

public class GerenciaBarco {

	public void salvar(Barco barco) throws RegraNegocioException {
		if (barco != null && barco.getNome() != null && barco.getModelo() != null) {
			if (barco.getCotas() > 0) {
				barco.setAtiva(true);
			}
			Projeto modelo = new ProjetoDao().get(barco.getModelo());
			if (modelo != null) {
				barco.setNome(barco.getNome().toLowerCase());
				barco.setCadastro(new Date());
				new BarcoDao().save(barco);
			} else {
				throw new RegraNegocioException("502");				
			}
		}		
	}
	
	public void salvar(Projeto modelo) {
		if (modelo != null && modelo.getNome() != null) {
			modelo.setNome(modelo.getNome().toLowerCase());
			new ProjetoDao().save(modelo);
		}		
	}
	
	public ViewBarco buscarViewBarco(String nome) {
		if (nome != null) {
			Barco barco = new BarcoDao().get(nome);
			if (barco != null) {
				return new ViewBarco(barco, new ProjetoDao().get(barco.getModelo()));
			}
		}		
		
		return null;
	}
	
	public Barco buscar(String nome) {
		if (nome != null) {
			return new BarcoDao().get(nome);
		}		
		
		return null;
	}
	
	public void excluir(Barco barco) {
		if (barco != null) {
			new BarcoDao().delete(barco);
		}		
	}
	
	public List<Barco> listar() {
		return new BarcoDao().list(null, null, null);
	}
		
}
