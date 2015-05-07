package com.brazilboatshare.model.entity;

import java.io.Serializable;
import java.util.Date;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import com.google.appengine.api.datastore.Link;
import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Noticia implements Serializable {
	private static final long serialVersionUID = 1L;

	public enum Tipo {
		ALERTA, INFORMACAO, PROPAGANDA
	}
	
	@Id private Long codigo;
	private String texto; 			
	private Text foto;								// foto da noticia
	private Text thumb;								// thumbnail da foto
	private Link link;								// link para noticia original
	private Tipo tipo;
	private Date data;
	@Index private String pais;						// pais do evento
	
	public Noticia() {}

	public Long getCodigo() {
		return codigo;
	}
	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		// sanear a entrada para evitar texto em HTML malicioso
		PolicyFactory sanitizer = Sanitizers.FORMATTING;
		this.texto = sanitizer.sanitize(texto);
	}
	public Tipo getTipo() {
		return tipo;
	}
	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}

	public String getPais() {
		return pais;
	}

	public Text getFoto() {
		return foto;
	}

	public void setFoto(Text foto) {
		this.foto = foto;
	}

	public Text getThumb() {
		return thumb;
	}

	public void setThumb(Text thumb) {
		this.thumb = thumb;
	}

	public void setPais(String pais) {
		this.pais = pais;
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
		if (!(obj instanceof Noticia))
			return false;
		Noticia other = (Noticia) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}
	
}
