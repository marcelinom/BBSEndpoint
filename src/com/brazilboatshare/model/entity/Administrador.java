package com.brazilboatshare.model.entity;

import java.io.Serializable;
import java.util.Date;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Administrador implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id	private String apelido;
	private String nome;
	private String sobrenome;
	private String senha;							// senha original, em texto limpo
	@Ignore private String pssMst;					// senha master para cadastramento
	private byte[] criptografada;					// senha criptografada
	private byte[] salt;							// added to the user’s password as part of the hashing
	@Index private String email;
	private Date cadastro;
	
	public Administrador() {
		this.cadastro = new Date();
	}
	
	public Administrador(String apelido) {
		this.apelido = apelido;
	}
	
	public Administrador(Administrador paraPerfil) {
		this.apelido = paraPerfil.getApelido();
		this.nome = paraPerfil.getNome();
		this.sobrenome = paraPerfil.getSobrenome();
		this.cadastro = paraPerfil.getCadastro();
	}

	public String getApelido() {
		return apelido;
	}
	public void setApelido(String apelido) {
		this.apelido = apelido;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String name) {
		// sanear a entrada para evitar texto em HTML malicioso
		PolicyFactory sanitizer = Sanitizers.FORMATTING;
		this.nome = sanitizer.sanitize(name);
	}
	public String getSobrenome() {
		return sobrenome;
	}
	public void setSobrenome(String sobrenome) {
		// sanear a entrada para evitar texto em HTML malicioso
		PolicyFactory sanitizer = Sanitizers.FORMATTING;
		this.sobrenome = sanitizer.sanitize(sobrenome);
	}

	public String getPssMst() {
		return pssMst;
	}

	public void setPssMst(String pssMst) {
		this.pssMst = pssMst;
	}

	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	public byte[] getCriptografada() {
		return criptografada;
	}
	public void setCriptografada(byte[] criptografada) {
		this.criptografada = criptografada;
	}
	public byte[] getSalt() {
		return salt;
	}
	public void setSalt(byte[] salt) {
		this.salt = salt;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getCadastro() {
		return cadastro;
	}
	public void setCadastro(Date cadastro) {
		this.cadastro = cadastro;
	}
	public void eliminaSensiveis() {
		this.senha = null;				
		this.criptografada = null;		
		this.salt = null;				
		this.cadastro = null;
	}
	public JSONObject toJSON() throws JSONException {
		JSONObject jobj = new JSONObject();
		jobj.put("nome", nome);
		jobj.put("sobrenome", sobrenome);
		jobj.put("email", email);
		return jobj;
	}
	public JSONObject toSecureJSON() throws JSONException {
		JSONObject jobj = new JSONObject();
		jobj.put("apelido", apelido);
		jobj.put("nome", nome);
		jobj.put("sobrenome", sobrenome);
		jobj.put("email", email);
		return jobj;
	}
	public boolean igual(Administrador usuario) {
		if (this == usuario)
			return true;
		if (usuario == null)
			return false;
		if (apelido == null) {
			if (usuario.apelido != null)
				return false;
		} else if (!apelido.equals(usuario.apelido))
			return false;
		if (email == null) {
			if (usuario.email != null)
				return false;
		} else if (!email.equals(usuario.email))
			return false;
		if (nome == null) {
			if (usuario.nome != null)
				return false;
		} else if (!nome.equals(usuario.nome))
			return false;
		if (sobrenome == null) {
			if (usuario.sobrenome != null)
				return false;
		} else if (!sobrenome.equals(usuario.sobrenome))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((apelido == null) ? 0 : apelido.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Administrador))
			return false;
		Administrador other = (Administrador) obj;
		if (apelido == null) {
			if (other.apelido != null)
				return false;
		} else if (!apelido.equals(other.apelido))
			return false;
		return true;
	}

}
 
