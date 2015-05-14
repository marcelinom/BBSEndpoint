package com.brazilboatshare.model.entity;

import java.io.Serializable;
import java.util.List;

import com.google.appengine.api.datastore.Link;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Projeto implements Serializable {
	private static final long serialVersionUID = 1L;
	public enum Tipo {VELEIRO, LANCHA, TRAWLER};
	
	@Id private String nome;
	private Link link;				// link para site do fabricante
	@Index private Tipo tipo;
	@Index private int pes;
	private String titulo;			// caracteristica principal do barco
	private List<String> descricao;	// Linhas de descricao das caracteristicas
	private String autor;			// projetista e qualificacoes	
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Link getLink() {
		return link;
	}
	public void setLink(Link link) {
		this.link = link;
	}
	public Tipo getTipo() {
		return tipo;
	}
	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}
	public int getPes() {
		return pes;
	}
	public void setPes(int pes) {
		this.pes = pes;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public List<String> getDescricao() {
		return descricao;
	}
	public void setDescricao(List<String> descricao) {
		this.descricao = descricao;
	}
	public String getAutor() {
		return autor;
	}
	public void setAutor(String autor) {
		this.autor = autor;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
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
		Projeto other = (Projeto) obj;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}
	
}
