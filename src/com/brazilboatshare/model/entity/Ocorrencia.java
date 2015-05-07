package com.brazilboatshare.model.entity;

import java.io.Serializable;
import java.util.Date;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

@Entity
public class Ocorrencia implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id private Long codigo;
	@Index private String barco;
	@Index @Load private Ref<Reserva> reserva;	 
	@Load private Ref<Componente> componente;				
	@Index private Date data;
	private String descricao;
	private boolean providenciar;				// se a ocorrencia exige alguma providencia
	private String providencia;					// descricao da providencia tomada
	private Date dataProvidencia;
	
	public Long getCodigo() {
		return codigo;
	}
	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}
	public Date getDataProvidencia() {
		return dataProvidencia;
	}
	public void setDataProvidencia(Date dataProvidencia) {
		this.dataProvidencia = dataProvidencia;
	}
	public String getBarco() {
		return barco;
	}
	public void setBarco(String barco) {
		this.barco = barco;
	}
	public Reserva getReserva() {
		return reserva==null?null:reserva.get();
	}
	public void setReserva(Reserva reserva) {
		this.reserva = reserva==null?null:Ref.create(reserva);
	}
	public Componente getComponente() {
		return componente==null?null:componente.get();
	}
	public void setComponente(Componente componente) {
		this.componente = componente==null?null:Ref.create(componente);
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public boolean isProvidenciar() {
		return providenciar;
	}
	public void setProvidenciar(boolean providenciar) {
		this.providenciar = providenciar;
	}
	public String getProvidencia() {
		return providencia;
	}
	public void setProvidencia(String providencia) {
		this.providencia = providencia;
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
		if (getClass() != obj.getClass())
			return false;
		Ocorrencia other = (Ocorrencia) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}
	
	
}
