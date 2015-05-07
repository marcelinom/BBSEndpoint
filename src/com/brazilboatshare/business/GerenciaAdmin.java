package com.brazilboatshare.business;

import java.util.ArrayList;
import java.util.List;

import com.brazilboatshare.exception.RegraNegocioException;
import com.brazilboatshare.model.dao.AdministradorDao;
import com.brazilboatshare.model.entity.Administrador;
import com.brazilboatshare.model.entity.Sessao;
import com.brazilboatshare.util.Criptografa;
import com.brazilboatshare.util.FiltroPesquisa;

public class GerenciaAdmin {

	private static final String EMAIL = "email";
	private static final String PREFIXO = "ADMIN#";

	public void cadastrarAdmin(Administrador admin) throws RegraNegocioException {
		if (admin.getApelido() != null && admin.getSenha() != null && admin.getEmail() != null) {
			if (admin.getPssMst().equals("kPd@3#25")) {
				admin.setApelido(admin.getApelido().toLowerCase());
				admin.setEmail(admin.getEmail().toLowerCase());
				admin.setSalt(Criptografa.generateSalt());
				admin.setCriptografada(Criptografa.getEncryptedPassword(admin.getSenha(), admin.getSalt()));
				new AdministradorDao().save(admin);
			}
		}
	}
	
	private Administrador buscarAdmin(String codigo) {
		if (codigo != null) {
			codigo = codigo.toLowerCase();
	        if (codigo.contains("@")) {
	    		ArrayList<FiltroPesquisa> filtro = new ArrayList<FiltroPesquisa>();
	    		filtro.add(new FiltroPesquisa(EMAIL, codigo));
	    		List<Administrador> lista = new AdministradorDao().get(filtro);
	    		if (lista != null && !lista.isEmpty()) {
	    			return lista.get(0);
	    		}
	        } else {
	    		return new AdministradorDao().get(codigo);
	        }
		}
		return null;
	}
	
	public Sessao login(String admin, String senha, String ip) throws RegraNegocioException {
		if (admin != null && senha != null) {
			Administrador user = buscarAdmin(admin);
			if (user != null && Criptografa.authenticate(senha, user.getCriptografada(), user.getSalt())) {
				return GerenciaSessao.login(PREFIXO+user.getApelido(), user, ip);
			}
		}
		throw new RegraNegocioException("517");
	}	
	
	public Sessao renovaAdmin(String admin, String sessao, String ipUsuario) throws RegraNegocioException {
		return GerenciaSessao.renova(PREFIXO+admin, sessao, ipUsuario);
	}
	
	
}
