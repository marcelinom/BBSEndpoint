package com.brazilboatshare.model.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

@Entity
public class Reserva implements Serializable {
	private static final long serialVersionUID = 1L;
	public enum Status {AGUARDANDO, CONFIRMADA, IMPUGNADA, VALIDADA, CANCELADA, REALIZADA};
	
	@Id private Long codigo;
	@Index private String barco;			// nome do barco da reserva
	@Index @Load private Ref<Cota> cota;	// cota relacionada a esta reserva
	private int pontos;						// pontos oferecidos para reserva
	private int penalidade;					// pontos de penalizacao (em caso de cancelamento, noshow, etc)
	private int ordem;						// prioridade da reserva
	@Index private Status status;
	@Index private Date saida;				// data para check-on
	@Index private Date retorno;			// data para devolver o barco
	private Date solicitacao;				// data da solicitacao da reserva
	private List<Local> roteiro;			// waypoints do roteiro 
	
	public Long getCodigo() {
		return codigo;
	}
	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}
	public Cota getCota() {
		return cota==null?null:cota.get();
	}
	public void setCota(Cota cota) {
		this.cota = cota==null?null:Ref.create(cota);
	}	
	public String getBarco() {
		return barco;
	}
	public void setBarco(String barco) {
		this.barco = barco;
	}
	public int getPontos() {
		return pontos;
	}
	public void setPontos(int pontos) {
		this.pontos = pontos;
	}
	public int getPenalidade() {
		return penalidade;
	}
	public void setPenalidade(int penalidade) {
		this.penalidade = penalidade;
	}
	public int getOrdem() {
		return ordem;
	}
	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Date getSaida() {
		return saida;
	}
	public void setSaida(Date saida) {
		this.saida = saida;
	}
	public Date getRetorno() {
		return retorno;
	}
	public void setRetorno(Date retorno) {
		this.retorno = retorno;
	}
	public Date getSolicitacao() {
		return solicitacao;
	}
	public void setSolicitacao(Date solicitacao) {
		this.solicitacao = solicitacao;
	}
	public List<Local> getRoteiro() {
		return roteiro;
	}
	public void setRoteiro(List<Local> roteiro) {
		this.roteiro = roteiro;
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
		Reserva other = (Reserva) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}
	
}
