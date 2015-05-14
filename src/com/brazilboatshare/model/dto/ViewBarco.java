package com.brazilboatshare.model.dto;

import java.io.Serializable;

import com.brazilboatshare.model.entity.Barco;
import com.brazilboatshare.model.entity.Projeto;

public class ViewBarco implements Serializable {
	private static final long serialVersionUID = 1L;

	private Barco barco;
	private Projeto modelo;
	
	public ViewBarco() {}
	
	public ViewBarco(Barco barco, Projeto modelo) {
		this.barco = barco;
		this.modelo = modelo;
	}
	
	public Barco getBarco() {
		return barco;
	}
	public void setBarco(Barco barco) {
		this.barco = barco;
	}
	public Projeto getModelo() {
		return modelo;
	}
	public void setModelo(Projeto modelo) {
		this.modelo = modelo;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((barco == null) ? 0 : barco.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ViewBarco other = (ViewBarco) obj;
		if (barco == null) {
			if (other.barco != null)
				return false;
		} else if (!barco.equals(other.barco))
			return false;
		return true;
	}


}
 
