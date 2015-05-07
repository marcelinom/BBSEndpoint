package com.brazilboatshare.util;

public class FiltroPesquisa {

	private String expressao;
	private Object valor;
	
	public FiltroPesquisa() {}	
	
	public FiltroPesquisa(String expressao, Object valor) {
		this.expressao = expressao;
		this.valor = valor;
	}
	
	public String getExpressao() {
		return expressao;
	}
	public void setExpressao(String expressao) {
		this.expressao = expressao;
	}
	public Object getValor() {
		return valor;
	}
	public void setValor(Object valor) {
		this.valor = valor;
	}
	
}
