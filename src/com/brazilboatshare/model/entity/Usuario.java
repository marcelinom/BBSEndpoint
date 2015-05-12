package com.brazilboatshare.model.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import com.brazilboatshare.util.Valida;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.condition.IfNotNull;

@Entity
public class Usuario implements Serializable {
	private static final long serialVersionUID = 1L;
	public enum Invalido {APELIDO, NOME, SENHA, EMAIL, FONE, IDIOMA};
	public enum Status {OK, INATIVO};

	@Id	private String apelido;
	private String nome;
	private String sobrenome;
	private Text foto;
	private Usuario.Status status;
	@Ignore private String senha;					// senha original, em texto limpo
	private byte[] criptografada;					// senha criptografada
	private byte[] salt;							// added to the user’s password as part of the hashing
	@Index(IfNotNull.class) private String email;
	private boolean emailConfirmado;				// se o email ja foi confirmado pelo usuario
	private Fone fone;
	private Date cadastro;
	private String locale;							//identificar idioma do usuario Ex.:'pt'
	
	public Usuario() {
		this.cadastro = new Date();
		this.locale = Locale.getDefault().getLanguage();
	}
	
	public Usuario(String apelido) {
		this.apelido = apelido;
	}
	
	public Usuario(Usuario paraPerfil) {
		this.apelido = paraPerfil.getApelido();
		this.nome = paraPerfil.getNome();
		this.sobrenome = paraPerfil.getSobrenome();
		this.cadastro = paraPerfil.getCadastro();
	}

	public Usuario.Status getStatus() {
		return status;
	}

	public void setStatus(Usuario.Status status) {
		this.status = status;
	}

	public Text getFoto() {
		return foto;
	}

	public void setFoto(Text foto) {
		this.foto = foto;
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
	public void setNome(String nome) {
		// sanear a entrada para evitar texto em HTML malicioso
		PolicyFactory sanitizer = Sanitizers.FORMATTING;
		this.nome = sanitizer.sanitize(nome);
	}
	public String getSobrenome() {
		return sobrenome;
	}
	public void setSobrenome(String sobrenome) {
		// sanear a entrada para evitar texto em HTML malicioso
		PolicyFactory sanitizer = Sanitizers.FORMATTING;
		this.sobrenome = sanitizer.sanitize(sobrenome);
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
	public boolean isEmailConfirmado() {
		return emailConfirmado;
	}
	public void setEmailConfirmado(boolean emailConfirmado) {
		this.emailConfirmado = emailConfirmado;
	}

	public Fone getFone() {
		return fone;
	}

	public void setFone(Fone fone) {
		this.fone = fone;
	}

	public Date getCadastro() {
		return cadastro;
	}
	public void setCadastro(Date cadastro) {
		this.cadastro = cadastro;
	}
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public void eliminaSensiveis() {
		this.senha = null;		
		this.criptografada = null;	
		this.salt = null;				
	}
	public Invalido invalido() {
		if (apelido == null || !Valida.username(apelido)) 
			return Invalido.APELIDO;
		if (email == null || !Valida.email(email)) 
			return Invalido.EMAIL;
		if (nome==null || sobrenome==null || !Valida.nome(nome) || !Valida.nome(sobrenome)) 
			return Invalido.NOME;
		if (fone != null)
//			if (codPaisFone==null || codAreaFone==null || !Valida.fone(codPaisFone) || !Valida.fone(codAreaFone) || !Valida.fone(fone))
			if (fone==null || !Valida.fone(fone.getDdd()) || !Valida.fone(fone.getNumero()))
				return Invalido.FONE;
		if (senha == null || !Valida.senha(senha))
			return Invalido.SENHA;
//		if (locale == null || !Valida.idioma(locale))
//			return Invalido.IDIOMA;

		return null;
	}
	public JSONObject toJSON() throws JSONException {
		JSONObject jobj = new JSONObject();
		jobj.put("fone", fone);
		jobj.put("nome", nome);
		jobj.put("sobrenome", sobrenome);
		jobj.put("email", email);
		return jobj;
	}
	public JSONObject toSecureJSON() throws JSONException {
		JSONObject jobj = new JSONObject();
		jobj.put("apelido", apelido);
		jobj.put("fone", fone);
		jobj.put("nome", nome);
		jobj.put("sobrenome", sobrenome);
		jobj.put("email", email);
		jobj.put("emailConfirmado", emailConfirmado);
		jobj.put("locale", locale);
		return jobj;
	}
	public String apelidoVoip() {
		return this.apelido+"voip";
	}
	public boolean igual(Usuario usuario) {
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
		if (fone == null) {
			if (usuario.fone != null)
				return false;
		} else if (!fone.equals(usuario.fone))
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
		if (!(obj instanceof Usuario))
			return false;
		Usuario other = (Usuario) obj;
		if (apelido == null) {
			if (other.apelido != null)
				return false;
		} else if (!apelido.equals(other.apelido))
			return false;
		return true;
	}

	
}
 
