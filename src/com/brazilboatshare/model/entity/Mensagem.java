package com.brazilboatshare.model.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Mensagem implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id private Long codigo;
	private Local local;
	private String mensagem; 			
	private Date data;
	private Usuario de;						
	private Usuario para;					
	
	public Mensagem() {
		this.data = new Date();
	}
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		// sanear a entrada para evitar texto em HTML malicioso
		PolicyFactory sanitizer = Sanitizers.FORMATTING;
		this.mensagem = sanitizer.sanitize(mensagem);
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public Usuario getDe() {
		return de;
	}
	public void setDe(Usuario de) {
		this.de = de;
	}
	public Usuario getPara() {
		return para;
	}
	public void setPara(Usuario para) {
		this.para = para;
	}
	public Local getLocal() {
		return local;
	}
	public void setLocal(Local local) {
		this.local = local;
	}
	public JSONObject toJSON() throws JSONException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		JSONObject jobj = new JSONObject();
		jobj.put("codigo", codigo);
		jobj.put("mensagem", mensagem);
		jobj.put("data", sdf.format(data));
		jobj.put("de", de.getApelido());
		jobj.put("para", para.getApelido());
		jobj.put("local", local==null?null:local.toJSON());

		return jobj;
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
		if (!(obj instanceof Mensagem))
			return false;
		Mensagem other = (Mensagem) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Contato [codigo=" + codigo + ", mensagem=" + mensagem + ", data=" + data + "]";
	}

	
}
