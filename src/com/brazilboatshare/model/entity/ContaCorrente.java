package com.brazilboatshare.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

@Entity
public class ContaCorrente implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id private Long codigo;
	@Index private String usuario;
	private BigDecimal valor;					
	private String descricao;
	@Load private Ref<TipoContabil> tipo;	 
	@Load private Ref<Reserva> reserva;	 
	@Index private Date data;
	
	public ContaCorrente() {
		this.data = new Date();
	}
	
	public ContaCorrente(String usuario) {
		this.usuario = usuario;
		this.data = new Date();
	}

	public Reserva getReserva() {
		return reserva==null?null:reserva.get();
	}
	public void setReserva(Reserva reserva) {
		this.reserva = reserva==null?null:Ref.create(reserva);
	}
	public TipoContabil getTipo() {
		return tipo==null?null:tipo.get();
	}
	public void setTipo(TipoContabil tipo) {
		this.tipo = tipo==null?null:Ref.create(tipo);
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public Long getCodigo() {
		return codigo;
	}
	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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
		if (!(obj instanceof ContaCorrente))
			return false;
		ContaCorrente other = (ContaCorrente) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}


}
