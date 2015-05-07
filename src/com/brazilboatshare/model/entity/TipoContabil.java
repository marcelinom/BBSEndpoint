package com.brazilboatshare.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class TipoContabil implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String PONTOS_GOOGLE_WALLET = "011";
	
	@Id private String codigo;
	private String descricao;
	private BigDecimal conversao;			// taxa de conversao $ -> pontos, se for o caso
	
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public BigDecimal getConversao() {
		return conversao;
	}
	public void setConversao(BigDecimal conversao) {
		this.conversao = conversao;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		result = prime * result
				+ ((conversao == null) ? 0 : conversao.hashCode());
		result = prime * result
				+ ((descricao == null) ? 0 : descricao.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof TipoContabil))
			return false;
		TipoContabil other = (TipoContabil) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		if (conversao == null) {
			if (other.conversao != null)
				return false;
		} else if (!conversao.equals(other.conversao))
			return false;
		if (descricao == null) {
			if (other.descricao != null)
				return false;
		} else if (!descricao.equals(other.descricao))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "TipoContabil [codigo=" + codigo + ", descricao=" + descricao
				+ ", conversao=" + conversao + "]";
	}
	
}
