package com.brazilboatshare.model.entity;

import java.io.Serializable;

import com.brazilboatshare.util.Idioma;
import com.brazilboatshare.util.Moeda;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Pais implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String PAIS_DEFAULT = "BR";
	public static final String PAIS_US = "US";
	
	@Id private String codigo;
	private String nome;
	private Moeda moeda;
	private Idioma idioma;
	private boolean sistemaMetrico;
	private String codPaisFone;
	private String codPais;			// codigo ISO3166-1 (http://en.wikipedia.org/wiki/ISO_3166-1_numeric)
	private String timezone;		// Windows timezone string. A coluna "Time Zone" de http://technet.microsoft.com/en-us/library/cc749073%28v=ws.10%29.aspx 
	
	
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Moeda getMoeda() {
		return moeda;
	}
	public void setMoeda(Moeda moeda) {
		this.moeda = moeda;
	}
	public String getCodPais() {
		return codPais;
	}
	public void setCodPais(String codPais) {
		this.codPais = codPais;
	}
	public String getTimezone() {
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	public boolean isSistemaMetrico() {
		return sistemaMetrico;
	}
	public void setSistemaMetrico(boolean sistemaMetrico) {
		this.sistemaMetrico = sistemaMetrico;
	}
	public Idioma getIdioma() {
		return idioma;
	}
	public void setIdioma(Idioma idioma) {
		this.idioma = idioma;
	}
	public String getCodPaisFone() {
		return codPaisFone;
	}
	public void setCodPaisFone(String codPaisFone) {
		this.codPaisFone = codPaisFone;
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
		if (!(obj instanceof Pais))
			return false;
		Pais other = (Pais) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}
	
	
}
