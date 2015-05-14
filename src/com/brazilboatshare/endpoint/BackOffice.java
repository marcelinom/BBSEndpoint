package com.brazilboatshare.endpoint;

import javax.servlet.http.HttpServletRequest;

import com.brazilboatshare.business.GerenciaAdmin;
import com.brazilboatshare.business.GerenciaBarco;
import com.brazilboatshare.business.GerenciaCota;
import com.brazilboatshare.business.GerenciaFinanceira;
import com.brazilboatshare.business.GerenciaNoticia;
import com.brazilboatshare.business.GerenciaPais;
import com.brazilboatshare.exception.ParametroException;
import com.brazilboatshare.exception.RegraNegocioException;
import com.brazilboatshare.model.dao.ObjectifyRegistering;
import com.brazilboatshare.model.entity.Administrador;
import com.brazilboatshare.model.entity.Barco;
import com.brazilboatshare.model.entity.Cota;
import com.brazilboatshare.model.entity.Noticia;
import com.brazilboatshare.model.entity.Pais;
import com.brazilboatshare.model.entity.Projeto;
import com.brazilboatshare.model.entity.Sessao;
import com.brazilboatshare.model.entity.TipoContabil;
import com.brazilboatshare.model.entity.Usuario;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;

@Api(name = "backoffice")
public class BackOffice {

	static {
		ObjectifyRegistering.register();
	}
	
	@ApiMethod(name = "pais.cadastrar",path = "pais/cadastrar",httpMethod = HttpMethod.POST)
	public void salvarPais(Pais pais) throws ParametroException {
		if (pais.getCodigo() != null) {
			new GerenciaPais().salvar(pais);
		} else {
			throw new ParametroException("102");
		}
	}
	
	@ApiMethod(name = "barco.cadastrar",path = "barco/cadastrar",httpMethod = HttpMethod.POST)
	public void salvarBarco(Barco barco) throws ParametroException, RegraNegocioException {
		if (barco.getNome() != null) {
			new GerenciaBarco().salvar(barco);
		} else {
			throw new ParametroException("102");
		}
	}
	
	@ApiMethod(name = "projeto.cadastrar",path = "projeto/cadastrar",httpMethod = HttpMethod.POST)
	public void salvarProjeto(Projeto modelo) throws ParametroException {
		if (modelo.getNome() != null) {
			new GerenciaBarco().salvar(modelo);
		} else {
			throw new ParametroException("102");
		}
	}
	
	@ApiMethod(name = "cota.cadastrar",path = "cota/cadastrar",httpMethod = HttpMethod.POST)
	public void criarCota(Cota cota) throws ParametroException {
		if (cota.getUsuario() != null) {
			new GerenciaCota().criar(cota);
		} else {
			throw new ParametroException("102");
		}
	}
	
	@ApiMethod(name = "tipocontabil.cadastrar",path = "tipocontabil/cadastrar",httpMethod = HttpMethod.POST)
	public void salvarTipoContabil(TipoContabil tipo) throws ParametroException {
		if (tipo.getCodigo() != null && tipo.getDescricao() != null) {
			new GerenciaFinanceira().salvaTipoContabil(tipo);
		} else {
			throw new ParametroException("102");
		}
	}
		
	@ApiMethod(name = "noticia.postar",path = "noticia/postar",httpMethod = HttpMethod.POST)
	public Noticia postarNoticia(Noticia noticia) throws ParametroException {
		if (noticia.getTexto() != null) {
			return new GerenciaNoticia().postar(noticia);
		} else {
			throw new ParametroException("102");
		}
	}
	
	@ApiMethod(name = "cadastrar.admin",path = "cadastrar/admin",httpMethod = HttpMethod.POST)
	public void cadastrarAdmin(Administrador admin) throws RegraNegocioException {
		new GerenciaAdmin().cadastrarAdmin(admin);
	}
		
	@ApiMethod(name = "usuario.login",path = "usuario/login",httpMethod = HttpMethod.POST)
	public Sessao login(Usuario usuario, HttpServletRequest req) throws ParametroException, RegraNegocioException {
		if (usuario.getApelido() != null && usuario.getSenha() != null) {
			return new GerenciaAdmin().login(usuario.getApelido().toLowerCase(), usuario.getSenha(), req.getRemoteAddr());
		} else {
			throw new ParametroException("102");
		}
	}
	
}
