package com.brazilboatshare.model.entity;

import java.io.Serializable;

import com.googlecode.objectify.annotation.Embed;

@Embed
public class Fone implements Serializable {
	private static final long serialVersionUID = 1L;
	public enum Tipo {FIXO, MOVEL, RADIO, SATELITE}
	
	private String ddi;
	private String ddd;
	private String numero;
	private Tipo tipo; 			 
	private String operadora;		// nome da operadora
	
	public Fone() {}
	
	public Fone(String ddi, String ddd, String numero) {
		this.ddi = ddi;
		this.ddd = ddd;
		this.numero = numero;
	}
	
	public Fone(Tipo tipo, String operadora) {
		this.tipo = tipo;
		this.operadora = operadora;
	}
	
	public String getDdi() {
		return ddi;
	}
	public void setDdi(String ddi) {
		this.ddi = ddi;
	}
	public String getDdd() {
		return ddd;
	}
	public void setDdd(String ddd) {
		this.ddd = ddd;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public String getOperadora() {
		return operadora;
	}

	public void setOperadora(String operadora) {
		this.operadora = operadora;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ddd == null) ? 0 : ddd.hashCode());
		result = prime * result + ((ddi == null) ? 0 : ddi.hashCode());
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Fone))
			return false;
		Fone other = (Fone) obj;
		if (ddd == null) {
			if (other.ddd != null)
				return false;
		} else if (!ddd.equals(other.ddd))
			return false;
		if (ddi == null) {
			if (other.ddi != null)
				return false;
		} else if (!ddi.equals(other.ddi))
			return false;
		if (numero == null) {
			if (other.numero != null)
				return false;
		} else if (!numero.equals(other.numero))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Fone [ddi=" + ddi + ", ddd=" + ddd + ", numero=" + numero
				+ ", tipo=" + tipo + ", operadora=" + operadora + "]";
	}
		
}
