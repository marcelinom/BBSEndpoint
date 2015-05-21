package com.brazilboatshare.model.entity;

import java.io.Serializable;
import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Cota implements Serializable {
	private static final long serialVersionUID = 1L;
	public enum Status {OK, INADIMPLENTE, CANCELADA};
	
	@Id private Long codigo;
	@Index private String usuario;				// proprietario da cota
	@Index private String barco;
	private Status status;
	private int pontos;					
	private Date compra;
	@Index private String dependente;			// dependente do proprietario
	@Ignore private String atual;				// auxilio para navegacao no site
	
	
	public String getBarco() {
		return barco;
	}
	public void setBarco(String barco) {
		this.barco = barco;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public String getDependente() {
		return dependente;
	}
	public void setDependente(String dependente) {
		this.dependente = dependente;
	}
	public Long getCodigo() {
		return codigo;
	}
	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}
	public String getAtual() {
		return atual;
	}
	public void setAtual(String atual) {
		this.atual = atual;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public int getPontos() {
		return pontos;
	}
	public void setPontos(int pontos) {
		this.pontos = pontos;
	}
	public Date getCompra() {
		return compra;
	}
	public void setCompra(Date compra) {
		this.compra = compra;
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
		Cota other = (Cota) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}

	
}
