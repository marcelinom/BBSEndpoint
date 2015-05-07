package com.brazilboatshare.model.entity;

import java.io.Serializable;
import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Confirmacao implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id private Long chave;
	@Index private String usuario;
	private String info;
	private Date data;
	
	public Confirmacao(String usuario, String info) {
		this.usuario = usuario;
		this.info = info;
		this.data = new Date();
	}

	public Confirmacao(String usuario, String info, Long chave) {
		this.usuario = usuario;
		this.info = info;
		this.chave = chave;
		this.data = new Date();
	}

	public Confirmacao() {
		this.data = new Date();
	}
	
	public Long getChave() {
		return chave;
	}
	public void setChave(Long chave) {
		this.chave = chave;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
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
		result = prime * result + ((chave == null) ? 0 : chave.hashCode());
		result = prime * result + ((info == null) ? 0 : info.hashCode());
		result = prime * result + ((usuario == null) ? 0 : usuario.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Confirmacao))
			return false;
		Confirmacao other = (Confirmacao) obj;
		if (chave == null) {
			if (other.chave != null)
				return false;
		} else if (!chave.equals(other.chave))
			return false;
		if (info == null) {
			if (other.info != null)
				return false;
		} else if (!info.equals(other.info))
			return false;
		if (usuario == null) {
			if (other.usuario != null)
				return false;
		} else if (!usuario.equals(other.usuario))
			return false;
		return true;
	}

	
}
