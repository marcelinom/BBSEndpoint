package com.brazilboatshare.model.entity;

import java.io.Serializable;

import com.googlecode.objectify.annotation.Embed;

@Embed
public class Identidade implements Serializable {
	private static final long serialVersionUID = 1L;
	public enum Tipo {CI, CPF, CNH, PASSAPORTE};

	private String codigo;
	private String emissor;
	private Tipo tipo;
	
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getEmissor() {
		return emissor;
	}
	public void setEmissor(String emissor) {
		this.emissor = emissor;
	}
	public Tipo getTipo() {
		return tipo;
	}
	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		result = prime * result + ((emissor == null) ? 0 : emissor.hashCode());
		result = prime * result + ((tipo == null) ? 0 : tipo.hashCode());
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
		Identidade other = (Identidade) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		if (emissor == null) {
			if (other.emissor != null)
				return false;
		} else if (!emissor.equals(other.emissor))
			return false;
		if (tipo != other.tipo)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Identidade [codigo=" + codigo + ", emissor=" + emissor
				+ ", tipo=" + tipo + "]";
	}
		
}
 
