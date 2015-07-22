package com.brazilboatshare.endpoint;

import java.util.List;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import com.brazilboatshare.business.GerenciaBarco;
import com.brazilboatshare.business.GerenciaCota;
import com.brazilboatshare.business.GerenciaFinanceira;
import com.brazilboatshare.business.GerenciaNoticia;
import com.brazilboatshare.business.GerenciaReserva;
import com.brazilboatshare.business.GerenciaSessao;
import com.brazilboatshare.business.GerenciaUsuario;
import com.brazilboatshare.exception.ParametroException;
import com.brazilboatshare.exception.RegraNegocioException;
import com.brazilboatshare.model.dao.ObjectifyRegistering;
import com.brazilboatshare.model.dto.ViewBarco;
import com.brazilboatshare.model.entity.Noticia;
import com.brazilboatshare.model.entity.Reserva;
import com.brazilboatshare.model.entity.Sessao;
import com.brazilboatshare.model.entity.Usuario;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;

@Api(name = "web")
public class Web {

	static {
		ObjectifyRegistering.register();
	}
	
	@ApiMethod(name = "usuario.login",path = "usuario/login",httpMethod = HttpMethod.POST)
	public Sessao login(Usuario usuario, HttpServletRequest req) throws ParametroException, RegraNegocioException {
		if (usuario.getApelido() != null && usuario.getSenha() != null) {
			return new GerenciaUsuario().login(usuario.getApelido().toLowerCase(), usuario.getSenha(), req.getRemoteAddr(), false);
		} else {
			throw new ParametroException("102");
		}
	}

	@ApiMethod(name = "perfil.buscar",path = "perfil/buscar",httpMethod = HttpMethod.GET)
	public Sessao buscarPerfil(@Named("usuario") String usuario, @Named("perfil") String perfil, @Named("sessao") String sessao, HttpServletRequest req) throws ParametroException, RegraNegocioException {
		Sessao session = GerenciaSessao.renova(usuario, sessao, req.getRemoteAddr());
		session.setResposta(new GerenciaUsuario().buscarPerfil(perfil));
		return session;
	}
	
	@ApiMethod(name = "solicitar.reserva",path = "solicitar/reserva",httpMethod = HttpMethod.PUT)
	public Sessao solicitarReserva(@Named("usuario") String usuario, @Named("sessao") String sessao, Reserva solic, HttpServletRequest req) throws ParametroException, RegraNegocioException {
		Sessao session = GerenciaSessao.renova(usuario, sessao, req.getRemoteAddr());
		session.setResposta(new GerenciaReserva().incluirReserva(usuario, solic));
		return session;
	}
	
	@ApiMethod(name = "dependente.buscar",path = "dependente/buscar",httpMethod = HttpMethod.GET)
	public Sessao buscarDependente(@Named("usuario") String usuario, @Named("cota") Long cota, @Named("sessao") String sessao, HttpServletRequest req) throws ParametroException, RegraNegocioException {
		Sessao session = GerenciaSessao.renova(usuario, sessao, req.getRemoteAddr());
		session.setResposta(new GerenciaCota().buscarDependente(cota));
		return session;
	}
	
	@ApiMethod(name = "dependente.incluir",path = "dependente/incluir",httpMethod = HttpMethod.PUT)
	public Sessao incluirDependente(@Named("usuario") String usuario, @Named("cota") Long cota, @Named("dep") String dependente, @Named("sessao") String sessao, HttpServletRequest req) throws ParametroException, RegraNegocioException {
		Sessao session = GerenciaSessao.renova(usuario, sessao, req.getRemoteAddr());
		Usuario candidato = new GerenciaUsuario().buscarUsuario(dependente);
		session.setResposta(new GerenciaCota().incluirDependente(usuario, cota, candidato));
		return session;
	}
	
	@ApiMethod(name = "dependente.excluir",path = "dependente/excluir",httpMethod = HttpMethod.DELETE)
	public Sessao excluirDependente(@Named("usuario") String usuario, @Named("cota") Long cota, @Named("sessao") String sessao, HttpServletRequest req) throws ParametroException, RegraNegocioException {
		Sessao session = GerenciaSessao.renova(usuario, sessao, req.getRemoteAddr());
		new GerenciaCota().excluirDependente(cota);
		return session;
	}
	
	@ApiMethod(name = "cadastrar.usuario",path = "cadastrar/usuario",httpMethod = HttpMethod.POST)
	public void cadastrar(Usuario usuario, HttpServletRequest req) throws RegraNegocioException {
		new GerenciaUsuario().cadastrar(usuario, req);
	}
	
	// metodo para obter dados atualizados do usuario 
	@ApiMethod(name = "usuario.renovar",path = "usuario/renovar",httpMethod = HttpMethod.GET)
	public Sessao continuaCadastro(@Named("usuario") String usuario, @Named("sessao") String sessao, HttpServletRequest req) throws RegraNegocioException {
		Sessao session = GerenciaSessao.renova(usuario, sessao, req.getRemoteAddr());
		session.setResposta(new GerenciaUsuario().obterDadosUsuario(usuario));
		return session;
	}
	
	@ApiMethod(name = "usuario.cadastro.alterar",path = "usuario/cadastro/alterar",httpMethod = HttpMethod.PUT)
	public Sessao alterarCadastro(@Named("sessao") String sessao, Usuario usuario, HttpServletRequest req) throws ParametroException, RegraNegocioException  {
		Sessao session = GerenciaSessao.renova(usuario.getApelido(), sessao, req.getRemoteAddr());
		if (usuario.getApelido() != null) {
			session.setResposta(new GerenciaUsuario().alterarCadastro(usuario, req));
			return session;
		} else {
			throw new ParametroException("102");
		} 
	}
	
	@ApiMethod(name = "usuario.documento.alterar",path = "usuario/documento/alterar",httpMethod = HttpMethod.PUT)
	public Sessao alterarDocumento(@Named("sessao") String sessao, Usuario usuario, HttpServletRequest req) throws ParametroException, RegraNegocioException  {
		Sessao session = GerenciaSessao.renova(usuario.getApelido(), sessao, req.getRemoteAddr());
		if (usuario.getApelido() != null) {
			session.setResposta(new GerenciaUsuario().alterarDocumento(usuario, req));
			return session;
		} else {
			throw new ParametroException("102");
		} 
	}
	
	@ApiMethod(name = "usuario.endereco.alterar",path = "usuario/endereco/alterar",httpMethod = HttpMethod.PUT)
	public Sessao alterarEndereco(@Named("sessao") String sessao, Usuario usuario, HttpServletRequest req) throws ParametroException, RegraNegocioException  {
		Sessao session = GerenciaSessao.renova(usuario.getApelido(), sessao, req.getRemoteAddr());
		if (usuario.getApelido() != null) {
			session.setResposta(new GerenciaUsuario().alterarEndereco(usuario, req));
			return session;
		} else {
			throw new ParametroException("102");
		} 
	}
	
	@ApiMethod(name = "usuario.senha.alterar",path = "usuario/senha/alterar",httpMethod = HttpMethod.PUT)
	public void alterarSenha(@Named("sessao") String sessao, Usuario usuario, HttpServletRequest req) throws ParametroException, RegraNegocioException {
		GerenciaSessao.renova(usuario.getApelido(), sessao, req.getRemoteAddr());
		if (usuario.getApelido() != null && usuario.getSenha() != null) {
			GerenciaUsuario gu = new GerenciaUsuario();
			gu.alterarSenha(usuario.getApelido(), usuario.getSenha());
		} else {
			throw new ParametroException("102");
		} 
	}
	
	@ApiMethod(name = "usuario.senha.esqueci.alterar",path = "usuario/senha/esqueci/alterar",httpMethod = HttpMethod.PUT)
	public void alterarSenhaEsquecida(@Named("token") String token, Usuario usuario, HttpServletRequest req) throws ParametroException, RegraNegocioException {
		if (usuario.getApelido() != null && usuario.getSenha() != null && token != null) {
			new GerenciaUsuario().alterarSenhaEsquecida(usuario.getApelido(), usuario.getSenha(), token);
		} else {
			throw new ParametroException("102");
		} 
	}
	
	@ApiMethod(name = "usuario.senha.esqueci",path = "usuario/senha/esqueci",httpMethod = HttpMethod.PUT)
	public void esqueciSenha(Usuario usuario, HttpServletRequest req) throws ParametroException, RegraNegocioException {
    	if (usuario.getApelido() != null) {
			new GerenciaUsuario().esqueciSenha(usuario.getApelido(), req.getRemoteAddr());
		} else {
			throw new ParametroException("102");
		} 
	}
	
	@ApiMethod(name = "noticia.listar",path = "noticia/listar",httpMethod = HttpMethod.GET)
	public List<Noticia> listarNoticias(@Named("pais") String pais) {
		Noticia regiao = new Noticia();
		regiao.setPais(pais);
		return new GerenciaNoticia().listarPorRegiao(regiao);
	}
	
	@ApiMethod(name = "extrato.listar",path = "extrato/listar",httpMethod = HttpMethod.GET)
	public Sessao listarExtrato(@Named("usuario") String usuario, @Named("sessao") String sessao, HttpServletRequest req) throws ParametroException, RegraNegocioException {
		if (usuario != null) {
			Sessao session = GerenciaSessao.renova(usuario, sessao, req.getRemoteAddr());
			session.setResposta(new GerenciaFinanceira().listarUtilizacao(usuario));
			return session;
		} else {
			throw new ParametroException("102");
		}
	}
	
	@ApiMethod(name = "conta.saldo.listar",path = "conta/saldo/listar",httpMethod = HttpMethod.GET)
	public Sessao listarSaldoConta(@Named("usuario") String usuario, @Named("sessao") String sessao, HttpServletRequest req) throws ParametroException, RegraNegocioException {
		if (usuario != null) {
			Sessao session = GerenciaSessao.renova(usuario, sessao, req.getRemoteAddr());
			session.setResposta(new GerenciaFinanceira().saldoContaCorrente(usuario));
			return session;
		} else {
			throw new ParametroException("102");
		}
	}
	
	@ApiMethod(name = "cota.usuario.listar",path = "cota/usuario/listar",httpMethod = HttpMethod.GET)
	public Sessao listaCotasPorUsuario(@Named("usuario")String usuario, @Named("sessao") String sessao, HttpServletRequest req) throws ParametroException, RegraNegocioException {
		if (usuario != null) {
			Sessao session = GerenciaSessao.renova(usuario, sessao, req.getRemoteAddr());
			session.setResposta(new GerenciaCota().listaCotasUsuario(usuario));
			return session;
		} else {
			throw new ParametroException("102");
		}
	}
	
	@ApiMethod(name = "barco.buscar",path = "barco/buscar",httpMethod = HttpMethod.GET)
	public ViewBarco buscarBarco(@Named("nome") String barco) throws ParametroException {
		if (barco != null) {
			return new GerenciaBarco().buscarViewBarco(barco);
		} else {
			throw new ParametroException("102");
		}
	}
	
	@SuppressWarnings("rawtypes")
	@ApiMethod(name = "barcos.listar",path = "barcos/listar",httpMethod = HttpMethod.GET)
	public List listarBarcos() {
		return new GerenciaBarco().listarDisponiveis();
	}
	
	@ApiMethod(name = "cota.buscar",path = "cota/buscar",httpMethod = HttpMethod.GET)
	public Sessao buscarCota(@Named("usuario")String usuario, @Named("sessao") String sessao, @Named("cota") Long cota, HttpServletRequest req) throws ParametroException, RegraNegocioException {
		if (cota != null) {
			Sessao session = GerenciaSessao.renova(usuario, sessao, req.getRemoteAddr());
			session.setResposta(new GerenciaCota().buscarViewCota(cota));
			return session;
		} else {
			throw new ParametroException("102");
		}
	}
	
	@ApiMethod(name = "reservas.barco.listar",path = "reservas/barco/listar",httpMethod = HttpMethod.GET)
	public Sessao listarReservasBarco(@Named("usuario")String usuario, @Named("sessao") String sessao, @Named("cota") Long cota, HttpServletRequest req) throws ParametroException, RegraNegocioException {
		if (cota != null) {
			Sessao session = GerenciaSessao.renova(usuario, sessao, req.getRemoteAddr());
			session.setResposta(new GerenciaReserva().listarReservasBarco(usuario, cota));
			return session;
		} else {
			throw new ParametroException("102");
		}
	}
	
	@ApiMethod(name = "reserva.buscar",path = "reserva/buscar",httpMethod = HttpMethod.GET)
	public Sessao buscarReserva(@Named("usuario")String usuario, @Named("sessao") String sessao, @Named("reserva") Long reserva, HttpServletRequest req) throws ParametroException, RegraNegocioException {
		if (reserva != null) {
			Sessao session = GerenciaSessao.renova(usuario, sessao, req.getRemoteAddr());
			session.setResposta(new GerenciaReserva().buscarReserva(usuario, reserva));
			return session;
		} else {
			throw new ParametroException("102");
		}
	}
	
	
}
