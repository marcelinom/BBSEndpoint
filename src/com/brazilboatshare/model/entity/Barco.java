package com.brazilboatshare.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Barco implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id private String nome;
	@Index private String modelo;	// chave para projeto do barco
	private boolean ativa;
	private int cotas;				// numero total de cotistas		
	private Date cadastro;			// quando foi incluido no sistema
	private BigDecimal valor;		// valor de venda da cota
	private BigDecimal taxa;		// valor do condominio
	private int ano;				// ano de contrucao. Negativo se novo
	private int fotos;				// numero de fotos
	private Referencia marina;		// marina onde fica o barco
	@Index private Local cidade;	// cidade da marina
	private List<String> descricao;	// Linhas de descricao dos itens exclusivos
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getModelo() {
		return modelo;
	}
	public void setModelo(String modelo) {
		this.modelo = modelo;
	}
	public Referencia getMarina() {
		return marina;
	}
	public void setMarina(Referencia marina) {
		this.marina = marina;
	}
	public int getFotos() {
		return fotos;
	}
	public void setFotos(int fotos) {
		this.fotos = fotos;
	}
	public List<String> getDescricao() {
		return descricao;
	}
	public void setDescricao(List<String> descricao) {
		this.descricao = descricao;
	}
	public boolean isAtiva() {
		return ativa;
	}
	public void setAtiva(boolean ativa) {
		this.ativa = ativa;
	}
	public int getCotas() {
		return cotas;
	}
	public void setCotas(int cotas) {
		this.cotas = cotas;
	}
	public Date getCadastro() {
		return cadastro;
	}
	public void setCadastro(Date cadastro) {
		this.cadastro = cadastro;
	}
	public Local getCidade() {
		return cidade;
	}
	public void setCidade(Local cidade) {
		this.cidade = cidade;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	public BigDecimal getTaxa() {
		return taxa;
	}
	public void setTaxa(BigDecimal taxa) {
		this.taxa = taxa;
	}
	public int getAno() {
		return ano;
	}
	public void setAno(int ano) {
		this.ano = ano;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
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
		Barco other = (Barco) obj;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}
	

}
