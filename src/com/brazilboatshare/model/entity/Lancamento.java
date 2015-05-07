package com.brazilboatshare.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

@Entity
public class Lancamento implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id private Long codigo;
	@Index private String barco;
	private BigDecimal valor;					
	private String descricao;
	@Load private Ref<TipoContabil> tipo;	 
	@Index private Date data;
	
	public Lancamento() {
		this.data = new Date();
	}
	
	public Lancamento(String barco) {
		this.barco = barco;
		this.data = new Date();
	}

	
	public Long getCodigo() {
		return codigo;
	}
	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}
	public String getBarco() {
		return barco;
	}
	public void setBarco(String barco) {
		this.barco = barco;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Ref<TipoContabil> getTipo() {
		return tipo;
	}
	public void setTipo(Ref<TipoContabil> tipo) {
		this.tipo = tipo;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
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
		if (getClass() != obj.getClass())
			return false;
		Lancamento other = (Lancamento) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}
	

}
