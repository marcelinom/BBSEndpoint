package com.brazilboatshare.business;

import java.util.Date;
import java.util.List;

import com.brazilboatshare.model.dao.BarcoDao;
import com.brazilboatshare.model.entity.Barco;

public class GerenciaBarco {

	public void salvar(Barco barco) {
		if (barco != null && barco.getNome() != null) {
			if (barco.getCotas() > 0) {
				barco.setAtiva(true);
			}
			barco.setNome(barco.getNome().toLowerCase());
			barco.setCadastro(new Date());
			new BarcoDao().save(barco);
		}		
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
