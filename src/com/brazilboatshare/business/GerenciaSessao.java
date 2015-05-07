package com.brazilboatshare.business;

import com.brazilboatshare.exception.RegraNegocioException;
import com.brazilboatshare.model.dao.SessaoDao;
import com.brazilboatshare.model.entity.Sessao;

public class GerenciaSessao {
	public static Sessao login(String usuario, Object resposta, String ipUsuario) {
		SessaoDao sDao = new SessaoDao();
		// busca outra sessao ativa em uso (pode haver +de um acesso simultaneo do mesmo usuario...)
		Sessao sessao = sDao.get(usuario);
		if (sessao == null || sessao.expirada()) {
			sessao = new Sessao(usuario);
		}
		sessao.incluiEndereco(ipUsuario==null?"":ipUsuario);
		if (sessao.renova()) {
			sDao.save(sessao);
		}
		sessao.setResposta(resposta);
		return sessao;
	}

	public static Sessao renova(String usuario, String codigo, String ipUsuario) throws RegraNegocioException {
		SessaoDao sDao = new SessaoDao();
		Sessao sessao = sDao.get(usuario);
		if (sessao != null && !sessao.expirada()) {
			// chave (codigo) e ip DEVEM ser iguais aos obtidos no login
			if (sessao.getCodigo().equals(codigo)) {
				for (String ipLogin : sessao.getEndereco()) {
					if (ipLogin.equals(ipUsuario)) {
						if (sessao.renova()) {
							sDao.save(sessao);
						}
						return sessao;
					}
				}
			}
		} 
		
		throw new RegraNegocioException("519");
	}
		
}
