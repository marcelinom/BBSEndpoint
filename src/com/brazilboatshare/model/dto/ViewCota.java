package com.brazilboatshare.model.dto;

import java.io.Serializable;

import com.brazilboatshare.model.entity.Cota;

public class ViewCota implements Serializable {
	private static final long serialVersionUID = 1L;

	private Cota cota;
	private ViewBarco barco;
	
	public ViewCota() {}
	
	public ViewCota(Cota cota, ViewBarco barco) {
		this.barco = barco;
		this.cota = cota;
	}

	public Cota getCota() {
		return cota;
	}

	public void setCota(Cota cota) {
		this.cota = cota;
	}

	public ViewBarco getBarco() {
		return barco;
	}

	public void setBarco(ViewBarco barco) {
		this.barco = barco;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cota == null) ? 0 : cota.hashCode());
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
		ViewCota other = (ViewCota) obj;
		if (cota == null) {
			if (other.cota != null)
				return false;
		} else if (!cota.equals(other.cota))
			return false;
		return true;
	}
	
}
 
