package com.brazilboatshare.model.entity;

import java.io.Serializable;

import com.googlecode.objectify.annotation.Embed;

@Embed
public class Par<V,T> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private V chave;
	private T valor;
	
	public Par() {
	}
	
	public Par(V chave, T valor) {
		this.chave = chave;
		this.valor = valor;
	}

	public V getChave() {
		return chave;
	}
	public void setChave(V chave) {
		this.chave = chave;
	}
	public T getValor() {
		return valor;
	}
	public void setValor(T valor) {
		this.valor = valor;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chave == null) ? 0 : chave.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Par))
			return false;
		@SuppressWarnings("rawtypes")
		Par other = (Par) obj;
		if (chave == null) {
			if (other.chave != null)
				return false;
		} else if (!chave.equals(other.chave))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Par [chave=" + chave + ", valor=" + valor + "]";
	}
	
}
