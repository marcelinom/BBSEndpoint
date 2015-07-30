package com.brazilboatshare.model.entity;

import java.io.Serializable;

import com.google.appengine.api.datastore.Link;
import com.googlecode.objectify.annotation.Embed;

@Embed
public class Referencia implements Serializable {
	private static final long serialVersionUID = 1L;

	private String descricao;
	private Link site;
	private double raio;				// raio maximo de distancia (em Km) p/roteiro sem pre-aprovacao
	private double fuso;				// fuso horario do local
	
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Link getSite() {
		return site;
	}
	public void setSite(Link site) {
		this.site = site;
	}
	public double getFuso() {
		return fuso;
	}
	public void setFuso(double fuso) {
		this.fuso = fuso;
	}
	public double getRaio() {
		return raio;
	}
	public void setRaio(double raio) {
		this.raio = raio;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((descricao == null) ? 0 : descricao.hashCode());
		result = prime * result + ((site == null) ? 0 : site.hashCode());
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
		Referencia other = (Referencia) obj;
		if (descricao == null) {
			if (other.descricao != null)
				return false;
		} else if (!descricao.equals(other.descricao))
			return false;
		if (site == null) {
			if (other.site != null)
				return false;
		} else if (!site.equals(other.site))
			return false;
		return true;
	}
	
}
 
