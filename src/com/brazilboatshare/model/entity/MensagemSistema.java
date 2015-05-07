package com.brazilboatshare.model.entity;

import java.io.Serializable;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class MensagemSistema implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String SEPARADOR = "#";
	
	public enum Tipo {
		CANCELAMENTO_ASSINATURA, 
		TROCA_ASSINATURA, 
		NOVA_PROPOSTA, 
		NOVO_ACORDO,
		PROPOSTA_REJEITADA,
		PROPOSTA_ACEITA,
		NOVA_OFERTA}

	@Id private String chave;
	private String locale;
	private String mensagem;
	private Tipo tipo;
	
	public MensagemSistema() {
	}
	
	public MensagemSistema(Tipo tipo, String locale, String mensagem) {
		this.tipo = tipo;
		this.locale = locale;
		this.mensagem = mensagem;
		setChave(null);
	}
	
	public String getChave() {
		return chave;
	}
	public void setChave(String chave) {
		this.chave = geraChave(tipo, locale);
	}
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	public Tipo getTipo() {
		return tipo;
	}
	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}
	public static String geraChave(Tipo tipo, String locale) {
		return tipo.name() + SEPARADOR + locale;
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
		if (!(obj instanceof MensagemSistema))
			return false;
		MensagemSistema other = (MensagemSistema) obj;
		if (chave == null) {
			if (other.chave != null)
				return false;
		} else if (!chave.equals(other.chave))
			return false;
		return true;
	}
	
}
