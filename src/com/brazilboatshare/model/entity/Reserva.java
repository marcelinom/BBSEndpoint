package com.brazilboatshare.model.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Reserva implements Serializable {
	private static final long serialVersionUID = 1L;
	public enum Status {AGUARDANDO, 					// recem-criada, aguardando validacao/confirmacao
						VALIDADA, 						// roteiro foi validado
						CONFIRMADA, 					// usuario pode usar o barco
						CONDOMINIO,						// realizada pelo condominio e ja confirmada formalmente
						IMPUGNADA, 						// roteiro nao foi aceito
						CANCELADA, 						// condomino ou o condominio cancelou a reserva
						ATIVA,							// condomino fez check-on
						REALIZADA						// condomino fez check-off
						};
	
	@Id private Long codigo;
	@Index private String barco;			// nome do barco da reserva
	private Long cota;						// cota relacionada a esta reserva
	@Index private String cotista;			// condomino proprietario da cota
	private String solicitante;				// quem solicitou a reserva (o condomino ou o dependente
	private int pontos;						// pontos oferecidos para reserva
	private int penalidade;					// pontos de penalizacao (em caso de cancelamento, noshow, etc)
	private int ordem;						// prioridade da reserva
	@Index private Status status;
	@Index private Date saida;				// data prevista para check-on
	@Index private Date retorno;			// data prevista para devolver o barco (check-off)
	private Date rsaida;					// data real do check-on
	private Date rretorno;					// data real da devolucao do barco (check-off)
	private Date solicitacao;				// data da solicitacao da reserva
	private List<Local> roteiro;			// waypoints do roteiro 
	
	public Long getCodigo() {
		return codigo;
	}
	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}
	public Long getCota() {
		return cota;
	}
	public void setCota(Long cota) {
		this.cota = cota;
	}	
	public String getSolicitante() {
		return solicitante;
	}
	public void setSolicitante(String solicitante) {
		this.solicitante = solicitante;
	}
	public String getCotista() {
		return cotista;
	}
	public void setCotista(String cotista) {
		this.cotista = cotista;
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
	public Date getRsaida() {
		return rsaida;
	}
	public void setRsaida(Date rsaida) {
		this.rsaida = rsaida;
	}
	public Date getRretorno() {
		return rretorno;
	}
	public void setRretorno(Date rretorno) {
		this.rretorno = rretorno;
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
