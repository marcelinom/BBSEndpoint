package com.brazilboatshare.business;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.brazilboatshare.email.AcessaEmail;
import com.brazilboatshare.exception.RegraNegocioException;
import com.brazilboatshare.model.dao.PaisDao;
import com.brazilboatshare.model.dao.Semaforo;
import com.brazilboatshare.model.dao.TextoPadraoDao;
import com.brazilboatshare.model.dao.UsuarioDao;
import com.brazilboatshare.model.entity.Confirmacao;
import com.brazilboatshare.model.entity.Local;
import com.brazilboatshare.model.entity.Pais;
import com.brazilboatshare.model.entity.Sessao;
import com.brazilboatshare.model.entity.TextoPadrao;
import com.brazilboatshare.model.entity.Usuario;
import com.brazilboatshare.model.entity.Usuario.Invalido;
import com.brazilboatshare.util.Criptografa;
import com.brazilboatshare.util.FiltroPesquisa;
import com.brazilboatshare.util.Tradutor;
import com.brazilboatshare.util.URLUtil;
import com.googlecode.objectify.Work;

public class GerenciaUsuario {
	private enum Cadastro {OK, ERRO_EMAIL, ERRO_APELIDO};
	private static final int LIMITE_DEFAULT_LISTA = 50;
	private static final int PRAZO_ALTERACAO = 20 * 60 * 1000;				// 20 min  
	private static final int PRAZO_CONFIRMACAO = 2 * 24 * 60 * 60 * 1000;	// 2 dias   
	private static final String EMAIL = "email";
	private static final String[] PATH_CONFIRMA_EMAIL = new String[]{"/confirmaEmail?usuario=","&email=","&codigo="};
	private static final String[] PATH_ESQUECI_SENHA = new String[]{"http://brazilboatshare.com/entrar.html?login#/esqueci/","/"};
	public static final String ENCODING = "UTF-8";
	private static final Local LOCAL_DEFAULT = new Local("Sao Paulo", -23.5505199432, -46.6333084106); 

	public Usuario cadastrar(final Usuario user, HttpServletRequest req) throws RegraNegocioException {
		Invalido erro = user.invalido();
		if (erro == null) {
			final String url = URLUtil.getURLRequest(req);
			inicializaUsuarioPreferencias(user, req);
			Cadastro status = new Semaforo<Cadastro>(Semaforo.CADASTRAR_EMAIL, user.getEmail()).ativar(
	    		new Work<Cadastro>() {
	    			public Cadastro run() {
	    				if (!existeEmailCadastrado(user.getEmail())) {
	    					return new Semaforo<Cadastro>(Semaforo.CADASTRAR_APELIDO, user.getApelido()).ativar(
	    			    		new Work<Cadastro>() {
	    			    			public Cadastro run() {
	    		    					if (!existeApelidoCadastrado(user.getApelido())) {
	    		    						// salva usuario com preferencias default
	    		    						new UsuarioDao().save(user);
	    		    						String link = geraPathConfirmacaoEmail(user);
	    		    						AcessaEmail.sistemaEnvia(user, Tradutor.traduza(user.getLocale(),"cadastro.assunto.email"), Tradutor.traduza(user.getLocale(),"cadastro.email", url+link));
	    		    	    				return Cadastro.OK;
	    		    					} 
    		    	    				return Cadastro.ERRO_APELIDO;
	    		    				}
	    			    		});
	    				} 
	    				return Cadastro.ERRO_EMAIL;
    				}
	    		});
			if (Cadastro.ERRO_EMAIL.equals(status)) throw new RegraNegocioException("521");
			if (Cadastro.ERRO_APELIDO.equals(status)) throw new RegraNegocioException("523");
			if (Cadastro.OK.equals(status)) {
				new GerenciaFinanceira().registraAcesso(user);
				return user;
			}
		} else {
			procesaErroValidacao(erro);
		}
		return null;
	}
	
	private Usuario inicializaUsuarioPreferencias(Usuario usuario, HttpServletRequest req) {
		Pais pais = obtemPaisRequest(req);

		Locale locale = new Locale(pais.getIdioma().codigo());
		usuario.setApelido(usuario.getApelido().toLowerCase(locale));
		usuario.setEmail(usuario.getEmail().toLowerCase(locale));
		
		usuario.setSalt(Criptografa.generateSalt());
		usuario.setCriptografada(Criptografa.getEncryptedPassword(usuario.getSenha(), usuario.getSalt()));

		usuario.setEmailConfirmado(false);
		if (usuario.getFone() != null) {
			usuario.getFone().setDdi(pais.getCodPaisFone());
		}
		usuario.setLocale(pais.getIdioma().codigo());
		
		return usuario;		
	}
	
	public String geraSenha() {
		char[] caracteres = new char[]{'a','b','c','d','e','f','g','h','i','j','k','l',
				'm','n','o','q','r','s','t','u','v','x','y','w','z',
				'A','B','C','D','E','F','G','H','J','K','L','M','N',
				'O','P','Q','R','S','T','U','V','X','Y','W','Z',
				'0','1','2','3','4','5','6','7','8','9','_','-','@'};
		
		StringBuffer senha = new StringBuffer();
		int comp = (int)Math.round(Math.random()*10) + 3;
		for(int j=0; j<comp; j++) {
			int indice = (int)Math.ceil(Math.random()*(caracteres.length-1));
			senha.append(caracteres[indice]);
		}
		senha.append("Ip");
		
		return senha.toString();
	}
	
	public static Local obtemCidadeRequest(HttpServletRequest req) {
		if (req == null) {
			return LOCAL_DEFAULT;
		} else {
			String[] latlong = req.getHeader("X-AppEngine-CityLatLong").split(",");
			return new Local(req.getHeader("X-AppEngine-City"),new Float(latlong[0]),new Float(latlong[1]));
		}
	}
	
	public static Pais obtemPaisRequest(HttpServletRequest req) {
		if (req == null) {
			return obtemPais(null);
		} else {
			return obtemPais(req.getHeader("X-AppEngine-Country"));
		}
	}
	
	public static Pais obtemPais(String codPais) {
		PaisDao pDao = new PaisDao();
		Pais pais = null;
		if (codPais != null) {
			pais = pDao.get(codPais);
		}
		
		if (pais == null) {
			pais = pDao.get(Pais.PAIS_DEFAULT);
		}
		return pais;
	}
	
	private boolean existeEmailCadastrado(String email) {
		Usuario anterior =  new UsuarioDao().getByProperty(EMAIL, email);
		if (anterior != null) {
			if (anterior.isEmailConfirmado()) {
				return true;
			} else {
				return usuarioPodeConfirmarEmail(anterior);
			}
		}
		return false;
	}
	
	private static boolean existeApelidoCadastrado(String apelido) {
		Usuario anterior =  new UsuarioDao().get(apelido);
		if (anterior != null) {
			if (anterior.isEmailConfirmado()) {
				return true;
			} else {
				return usuarioPodeConfirmarEmail(anterior);
			}
		}
		return false;
	}
	
	private static boolean usuarioPodeConfirmarEmail(Usuario anterior) {
		UsuarioDao uDao = new UsuarioDao();
		List<Confirmacao> confs = uDao.buscaConfirmacao(anterior.getApelido());
		if (confs != null) {
			for (Confirmacao conf : confs) {
				if (System.currentTimeMillis() < conf.getData().getTime() + PRAZO_CONFIRMACAO) {
					// usuario anterior ainda dentro do prazo para confirmar seu email...
					return true;
				} else {
					uDao.excluiConfirmacao(conf);
				}
			}
		}
		// todas solicitacoes para confirmar email expiradas...
		excluiUsuario(anterior);
		return false;
	}
	
	/*
	 *  Metodo para excluir apenas usuario que nunca acessaram o sistema (email nao confirmado) 
	 */
	private static void excluiUsuario(Usuario usuario) {
		new UsuarioDao().delete(usuario);
	}
	
	private String geraPathConfirmacaoEmail(Usuario usuario) {
		try {
			Confirmacao link = new Confirmacao(usuario.getApelido(), usuario.getEmail());
			new UsuarioDao().salvaLinkConfirmacao(link);
			String encodedUsuario = URLEncoder.encode(link.getUsuario(), ENCODING);
			String encodedEmail = URLEncoder.encode(link.getInfo(), ENCODING);
			return PATH_CONFIRMA_EMAIL[0] + encodedUsuario + PATH_CONFIRMA_EMAIL[1] + encodedEmail + PATH_CONFIRMA_EMAIL[2] + link.getChave();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Sessao confirmaEmail(String usuario, String email, Long chave, String ip) throws RegraNegocioException {
		Confirmacao confirmacao = new Confirmacao(usuario, email, chave);
		UsuarioDao uDao = new UsuarioDao();
		Confirmacao salva = uDao.buscaConfirmacao(confirmacao.getChave());
		if (salva != null && salva.equals(confirmacao)) {
			uDao.excluiConfirmacao(salva);
			Usuario user = uDao.get(confirmacao.getUsuario());
			user.setEmailConfirmado(true);
			uDao.save(user);
			return GerenciaSessao.login(usuario, concedeAcesso(user), ip);
		}
		return null;
	}
	
	public void esqueciSenha(String usuario, String ip) throws RegraNegocioException {
		Usuario user = buscarUsuario(usuario);
		if (user != null && user.isEmailConfirmado()) {
			try {
				Confirmacao token = new Confirmacao(user.getApelido(), ip);
				new UsuarioDao().salvaLinkConfirmacao(token);
				String link = PATH_ESQUECI_SENHA[0] + URLEncoder.encode(user.getApelido(), ENCODING) + PATH_ESQUECI_SENHA[1] + token.getChave();
				TextoPadrao mensagem = new TextoPadraoDao().get("senha_esqueci_"+user.getLocale());
				if (mensagem != null) {
					AcessaEmail.sistemaEnviaHTML(user, mensagem.getAssunto(), String.format(mensagem.getMensagem(),link));
				} else {
					AcessaEmail.sistemaEnviaHTML(user, Tradutor.traduza(user.getLocale(),"senha.assunto.esqueci"), Tradutor.traduza(user.getLocale(),"senha.esqueci", link));
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else if (user != null) {
			// reenvia solicitacao de confirmacao de email
			geraPathConfirmacaoEmail(user);
			throw new RegraNegocioException("533");
		} else {
			throw new RegraNegocioException("543");
		}
	}
	
	private void procesaErroValidacao(Invalido erro) throws RegraNegocioException {
		if (erro.equals(Invalido.IDIOMA)) 
			throw new RegraNegocioException("520");
		if (erro.equals(Invalido.NOME)) 
			throw new RegraNegocioException("522");
		if (erro.equals(Invalido.APELIDO)) 
			throw new RegraNegocioException("524");
		if (erro.equals(Invalido.EMAIL)) 
			throw new RegraNegocioException("525");
		if (erro.equals(Invalido.SENHA))
			throw new RegraNegocioException("526");		
		if (erro.equals(Invalido.FONE))
			throw new RegraNegocioException("527");
	}
	
	public Usuario alterarCadastroCaminhoneiro(Usuario perfil, HttpServletRequest req) throws RegraNegocioException {
		Usuario vuser = alterarCadastro(perfil, req);
		if (vuser != null) {
			return vuser;
		}
		
		return perfil;
	}
	
	public Usuario alterarCadastro(final Usuario novo, HttpServletRequest req) throws RegraNegocioException {
		final Usuario antigo = buscar(novo.getApelido());
		if (antigo != null) {
			Locale locale = new Locale(antigo.getLocale());
			novo.setEmail(novo.getEmail().toLowerCase(locale));
			novo.setCadastro(antigo.getCadastro());
			novo.setCriptografada(antigo.getCriptografada());
			novo.setSalt(antigo.getSalt());
			novo.setApelido(antigo.getApelido());
			novo.setEmailConfirmado(antigo.isEmailConfirmado());
			novo.setFone(antigo.getFone());
			novo.setLocale(antigo.getLocale());
			if (!novo.igual(antigo)) {
				if (!antigo.getEmail().equals(novo.getEmail())) {
					final String url = URLUtil.getURLRequest(req);
					Cadastro status = new Semaforo<Cadastro>(Semaforo.CADASTRAR_EMAIL, novo.getEmail()).ativar(
			    		new Work<Cadastro>() {
			    			public Cadastro run() {
			    				if (buscaPorPropriedade(EMAIL, novo.getEmail()) == null) {
			    					novo.setEmailConfirmado(false);
				    				new UsuarioDao().saveNow(novo);
		    						String link = geraPathConfirmacaoEmail(novo);
		    						AcessaEmail.sistemaEnvia(novo, Tradutor.traduza(novo.getLocale(),"cadastro.assunto.email"), Tradutor.traduza(novo.getLocale(),"cadastro.email", url+link));
			    					return Cadastro.OK;
			    				}
		    					return Cadastro.ERRO_EMAIL;
		    				}
			    		});
					if (Cadastro.ERRO_EMAIL.equals(status)) throw new RegraNegocioException("521");
				} else {
					new UsuarioDao().save(novo);
				}
				return concedeAcesso(novo);
			}
		}
		return null;
	}
	
	public void alterarSenha(String usuario, String novaSenha) throws RegraNegocioException {
		Usuario antigo = buscarUsuario(usuario);
		if (antigo != null && novaSenha != null) {
			// senha nova deve ser diferente da antiga
			if (!Criptografa.authenticate(novaSenha, antigo.getCriptografada(), antigo.getSalt())) {
				antigo.setCriptografada(Criptografa.getEncryptedPassword(novaSenha, antigo.getSalt()));
				new UsuarioDao().save(antigo);
			} else {
				throw new RegraNegocioException("534");
			}
		}
	}
	
	public void alterarSenhaEsquecida(String usuario, String novaSenha, String token) throws RegraNegocioException {
		Usuario user = buscarUsuario(usuario);
		UsuarioDao dao = new UsuarioDao();
		Confirmacao stuck = dao.buscaConfirmacao(new Long(token));
		if (stuck != null && stuck.getUsuario().equals(user.getApelido())) {
			if (System.currentTimeMillis() < stuck.getData().getTime() + PRAZO_ALTERACAO) {
				alterarSenha(user.getApelido(), novaSenha);
				dao.excluiConfirmacao(stuck);
			} else {
				dao.excluiConfirmacao(stuck);
				throw new RegraNegocioException("535");
			}
		}
	}
	
	public static Usuario buscar(String id) {
		if (id != null) {
			UsuarioDao dao = new UsuarioDao();
			return dao.get(id);
		}
		
		return null;
	}
	
	private Usuario buscarUsuario(String codigo) {
		if (codigo != null) {
			codigo = codigo.toLowerCase();
	        if (codigo.contains("@")) {
	    		ArrayList<FiltroPesquisa> filtro = new ArrayList<FiltroPesquisa>();
	    		filtro.add(new FiltroPesquisa(EMAIL, codigo));
	    		List<Usuario> lista = new UsuarioDao().get(filtro);
	    		if (lista != null && !lista.isEmpty()) {
	    			return lista.get(0);
	    		}
	        } else {
	    		return buscar(codigo);
	        }
		}
		return null;
	}
	
	public Sessao login(String usuario, String senha, String ip, boolean registroMobile) throws RegraNegocioException {
		if (usuario != null && senha != null) {
			Usuario user = buscarUsuario(usuario);
			if (user != null && Criptografa.authenticate(senha, user.getCriptografada(), user.getSalt())) {
				return GerenciaSessao.login(user.getApelido(), concedeAcesso(user), ip);
			}
		}
		throw new RegraNegocioException("517");
	}
	
	private Usuario concedeAcesso(Usuario user) throws RegraNegocioException {
		if (user.isEmailConfirmado()) {
			new GerenciaFinanceira().registraAcesso(user);
			// evitar envio de dados sensiveis ou desnecessarios
			user.setSalt(null);
			user.setCriptografada(null);
			user.setSenha(null);			
			return user;
		} else {
			throw new RegraNegocioException("533");
		}
	}
	
	public Usuario obterDadosUsuario(String usuario) throws RegraNegocioException {
		return concedeAcesso(new UsuarioDao().get(usuario));
	}
	
	public List<Usuario> listar(String prefixo) {
		ArrayList<FiltroPesquisa> filtro = new ArrayList<FiltroPesquisa>();
        if (prefixo.contains("@")) {
    		filtro.add(new FiltroPesquisa(EMAIL, prefixo));
        } else {
    		// simular operador like (apelido like 'patri%')
    		filtro.add(new FiltroPesquisa("apelido >=", prefixo));
    		filtro.add(new FiltroPesquisa("apelido <", prefixo + "\uFFFD"));
        }
        List<Usuario> users = new UsuarioDao().list(filtro, null, LIMITE_DEFAULT_LISTA);
        if (users != null && !users.isEmpty()) {
            ArrayList<Usuario> lista = new ArrayList<Usuario>();
        	for (Usuario user : users) {
        		lista.add(user);
        	}
        	return lista;
        }
        return null;
	}
	
	public Usuario buscaPorPropriedade(String propriedade, String valor) {
		if (propriedade != null && valor != null) {
			return new UsuarioDao().getByProperty(propriedade, valor);
		}
		return null;
	}
	
	public List<Usuario> listar(List<FiltroPesquisa> filtro, List<String> order, Integer limite) {
		UsuarioDao userDao = new UsuarioDao();
		List<Usuario> result = userDao.list(filtro, order, limite==null?LIMITE_DEFAULT_LISTA:limite);
		return result;
	}
	
}
