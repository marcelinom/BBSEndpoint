package com.brazilboatshare.model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.brazilboatshare.util.Criptografa;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;

@Entity
public class Sessao implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final int TIMEOUT_SESSAO = 15; 		// minutos para expiracao da sessao do usuario 
	private static final int MINUTO = 1 * 60 * 1000;  

	@Id private String usuario;
	private String codigo;				// identificador unico da sessao 
	private Date expira;
	private List<String> endereco;		// o(s) ip(s) de acesso
	@Ignore private Object resposta;
	
	public Sessao() {
		this.endereco = new ArrayList<String>();
	}
	
	public Sessao(String usuario) {
		this.usuario = usuario;
		this.endereco = new ArrayList<String>();
		this.codigo = Criptografa.geraSHACodigo(20);
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
	}

	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public List<String> getEndereco() {
		return endereco;
	}

	public void setEndereco(List<String> endereco) {
		this.endereco = endereco;
	}

	public void incluiEndereco(String ip) {
		for (String addr : getEndereco()) {
			if (addr.equals(ip)) {
				return;
			}
		}
		getEndereco().add(ip);
	}

	public Date getExpira() {
		return expira;
	}

	public void setExpira(Date expira) {
		this.expira = expira;
	}

	public Object getResposta() {
		return resposta;
	}
	public void setResposta(Object resposta) {
		this.resposta = resposta;
	}
	public boolean expirada() {
		return getExpira().before(new Date());
	}
	public boolean renova() {
		// renova token em intervalo minimo de 1 min, para poupar datastore...
		if (expira == null || expira.getTime()+MINUTO > System.currentTimeMillis()) {
			GregorianCalendar gc = new GregorianCalendar();
			gc.add(Calendar.MINUTE, TIMEOUT_SESSAO);
			setExpira(gc.getTime());
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((usuario == null) ? 0 : usuario.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Sessao))
			return false;
		Sessao other = (Sessao) obj;
		if (usuario == null) {
			if (other.usuario != null)
				return false;
		} else if (!usuario.equals(other.usuario))
			return false;
		return true;
	}
	
}
