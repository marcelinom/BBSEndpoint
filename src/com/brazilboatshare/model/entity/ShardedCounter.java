package com.brazilboatshare.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

// "Sharded counter" (https://developers.google.com/appengine/articles/sharding_counters)

@Entity
public class ShardedCounter implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id private String codigo;
	@Index private String nome;
	private BigDecimal parcial;
	
	public ShardedCounter() {}

	public ShardedCounter(String nome, int sequencial) {
		this.nome = nome;
		setCodigo(String.valueOf(sequencial));
	}

	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String sequencial) {
		this.codigo = nome + sequencial;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public BigDecimal getParcial() {
		return parcial;
	}
	public void setParcial(BigDecimal parcial) {
		this.parcial = parcial;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ShardedCounter))
			return false;
		ShardedCounter other = (ShardedCounter) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}

}
