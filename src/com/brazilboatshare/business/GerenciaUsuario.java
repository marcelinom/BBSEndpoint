package com.brazilboatshare.business;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.brazilboatshare.email.AcessaEmail;
import com.brazilboatshare.exception.RegraNegocioException;
import com.brazilboatshare.model.dao.PaisDao;
import com.brazilboatshare.model.dao.Semaforo;
import com.brazilboatshare.model.dao.TextoPadraoDao;
import com.brazilboatshare.model.dao.UsuarioDao;
import com.brazilboatshare.model.entity.Confirmacao;
import com.brazilboatshare.model.entity.Fone;
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
    private static final Logger LOG = Logger.getLogger(GerenciaUsuario.class.getName());
	private static final String URL_CONSULTA_TELEIN = "http://consultaoperadora1.telein.com.br/sistema/consulta_detalhada.php?chave=2e1bf9020a4cf5602da5"; 	
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
	    		    						// enviar email ao administrador do sistema
	    		    						AcessaEmail.sistemaEnvia(AcessaEmail.EMAIL_ADMINISTRADOR, AcessaEmail.NOME_ADMINISTRADOR, 
	    		    								Tradutor.traduza(user.getLocale(),"cadastro.adm.assunto.email"), 
	    		    								Tradutor.traduza(user.getLocale(),"cadastro.adm.email", user.getApelido(), user.getNome(), user.getSobrenome()));
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
		usuario.setStatus(Usuario.Status.INATIVO);
		
		usuario.setSalt(Criptografa.generateSalt());
		usuario.setCriptografada(Criptografa.getEncryptedPassword(usuario.getSenha(), usuario.getSalt()));

		usuario.setLocale(pais.getIdioma().codigo());	
		usuario.setEmailConfirmado(false);

		usuario.getFone().setDdi(pais.getCodPaisFone());
		atualizaFone(usuario.getFone());
		
		return usuario;		
	}
	
	public Fone atualizaFone(Fone fone) {
		if (fone != null) {
			// identifica tipo telefone e operadora
			if (fone.getDdd() != null) {
				Fone oper = consultaOperadora(fone.getDdd(), fone.getNumero());
				if (oper != null) {
					fone.setOperadora(oper.getOperadora());
					fone.setTipo(oper.getTipo());
				}
			}	    		    						
		}
		return fone;
	}
	
	public static Fone consultaOperadora(String ddd, String numero) {
    	Fone fone = new Fone();
	    try {
		    String resposta;
		    String telefone = "&numero="+ddd+numero;
		    URL url = new URL(URL_CONSULTA_TELEIN+telefone);
		    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		    if ((resposta = reader.readLine()) != null) {
		    	if (!resposta.contains("Telein.com.br")) {
		    		fone = identificaOperadora(resposta.split("#")[0].trim());
		    	} else {
		    		LOG.info("******* Falha ao consultar Operadora de Telefone: "+resposta);
		    	}
	    		fone.setDdd(ddd);
	    		fone.setNumero(numero);
		    }
	    	reader.close();
		} catch (Exception e) {
    		fone.setDdd(ddd);
    		fone.setNumero(numero);
		}
    	return fone;
	}	
	
	public static Fone identificaOperadora(String codigo) {
		if ("551052".equals(codigo)) {
			return new Fone(Fone.Tipo.RADIO, "IPCORP TELECOM");
		} else if ("551066".equals(codigo)) {
			return new Fone(Fone.Tipo.RADIO, "Nextel");
		} else if ("551087".equals(codigo)) {
			return new Fone(Fone.Tipo.RADIO, "Superchip Telecom");
		} else if ("551099".equals(codigo)) {
			return new Fone(Fone.Tipo.RADIO, "TELEMACOMEX");
		} else if ("552002".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "51 Brasil");
		} else if ("552003".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Aerotech");
		} else if ("552004".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Alpamayo");
		} else if ("552005".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "America Net");
		} else if ("552006".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Amigo Telecom");
		} else if ("552007".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Ava");
		} else if ("552008".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "BaydeNET");
		} else if ("552009".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "BBS OPTIONS");
		} else if ("552010".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "BR Group");
		} else if ("552011".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "BRASTEL");
		} else if ("552012".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "BRITISH TELECOM");
		} else if ("552013".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Cabo Telecom");
		} else if ("552014".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Cambridge");
		} else if ("552015".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Cia Itabirana");
		} else if ("552017".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Conecta");
		} else if ("552018".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Convergia");
		} else if ("552019".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "CTBC");
		} else if ("552020".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Datora");
		} else if ("552021".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Desktop");
		} else if ("552022".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Dialdata Telecomunic");
		} else if ("552023".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Digi Soluções");
		} else if ("552024".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Dipelnet");
		} else if ("552025".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Directcall Brasil");
		} else if ("552026".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "DOLLARPHONE");
		} else if ("552027".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "DSLI");
		} else if ("552028".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "EAD");
		} else if ("552029".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Easytone");
		} else if ("552030".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Embratel");
		} else if ("552031".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Encanto");
		} else if ("552032".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "ENGEVOX");
		} else if ("552033".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Ensite Telecom");
		} else if ("552034".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "EPSILON");
		} else if ("552035".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "ETML");
		} else if ("552036".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Fidelity");
		} else if ("552037".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "FONAR");
		} else if ("552038".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "G30");
		} else if ("552039".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "GLOBAL CROSSING");
		} else if ("552041".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Goiás Telecom");
		} else if ("552042".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "GOLDEN LINE TELECOM");
		} else if ("552043".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Grupo G1");
		} else if ("552044".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "GT GROUP");
		} else if ("552045".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "GTI Telecom");
		} else if ("552046".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "GVT");
		} else if ("552047".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Hello Brazil");
		} else if ("552048".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Hit Telecom");
		} else if ("552049".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Hoje Participações");
		} else if ("552050".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "IDT Brasil");
		} else if ("552051".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Intelig Telecom");
		} else if ("552052".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "IPCORP TELECOM");
		} else if ("552054".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Itavoice");
		} else if ("552055".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "iVATi");
		} else if ("552056".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "LIFE Telecom");
		} else if ("552057".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Ligue Telecom");
		} else if ("552058".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Local");
		} else if ("552059".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Locaweb Telecom");
		} else if ("552060".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Maha-Tel Telecom");
		} else if ("552061".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Metroweb");
		} else if ("552062".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Mundivox");
		} else if ("552063".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "NE BALESTRA");
		} else if ("552064".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Neo Telecom");
		} else if ("552065".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "NETGLOBALIS");
		} else if ("552067".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "NEXUS");
		} else if ("552068".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "NORTELPA S.A.");
		} else if ("552069".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "NWI");
		} else if ("552070".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "OI");
		} else if ("552072".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "OpçãoNet");
		} else if ("552073".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "OPTION TELECOM");
		} else if ("552074".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Osi Telecom");
		} else if ("552075".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Ostara Telecom");
		} else if ("552076".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Plumium");
		} else if ("552078".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Porto Velho Telecom");
		} else if ("552079".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Rede Networks");
		} else if ("552080".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Remota Comunicações");
		} else if ("552081".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "RSTSUL");
		} else if ("552082".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Sercomtel");
		} else if ("552084".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Smart Telecom");
		} else if ("552085".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Spin Telecom");
		} else if ("552086".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Sul Internet");
		} else if ("552088".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "SuperTV");
		} else if ("552089".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Suporte Tecnologia");
		} else if ("552090".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "T-LESTE TELECOM");
		} else if ("552091".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "T.P.A");
		} else if ("552093".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Telecall");
		} else if ("552094".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "TELECOM 65");
		} else if ("552095".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "TELECOMDADOS");
		} else if ("552097".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Telefônica Brasil");
		} else if ("552098".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Telefree");
		} else if ("552100".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Teletel Callip");
		} else if ("552101".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "TESA");
		} else if ("552102".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "TIM");
		} else if ("552103".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Tinerhir");
		} else if ("552104".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Tmais");
		} else if ("552105".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Transit");
		} else if ("552106".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Ultranet Telecomunic");
		} else if ("552108".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "VIA REAL TELECOM");
		} else if ("552109".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Viacom");
		} else if ("552110".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Vipnet");
		} else if ("552111".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Vipway");
		} else if ("552112".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Visãonet");
		} else if ("552113".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "Voitel");
		} else if ("552114".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "VOX");
		} else if ("552115".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "VOXBRAS");
		} else if ("552116".equals(codigo)) {
			return new Fone(Fone.Tipo.FIXO, "TVN");
		} else if ("553009".equals(codigo)) {
			return new Fone(Fone.Tipo.MOVEL, "BBS OPTIONS");
		} else if ("553016".equals(codigo)) {
			return new Fone(Fone.Tipo.MOVEL, "Claro");
		} else if ("553019".equals(codigo)) {
			return new Fone(Fone.Tipo.MOVEL, "CTBC");
		} else if ("553020".equals(codigo)) {
			return new Fone(Fone.Tipo.MOVEL, "Datora");
		} else if ("553066".equals(codigo)) {
			return new Fone(Fone.Tipo.MOVEL, "Nextel");
		} else if ("553070".equals(codigo)) {
			return new Fone(Fone.Tipo.MOVEL, "OI");
		} else if ("553077".equals(codigo)) {
			return new Fone(Fone.Tipo.MOVEL, "Porto Conecta");
		} else if ("553082".equals(codigo)) {
			return new Fone(Fone.Tipo.MOVEL, "Sercomtel");
		} else if ("553083".equals(codigo)) {
			return new Fone(Fone.Tipo.MOVEL, "SISTEER");
		} else if ("553097".equals(codigo)) {
			return new Fone(Fone.Tipo.MOVEL, "Telefônica Brasil");
		} else if ("553102".equals(codigo)) {
			return new Fone(Fone.Tipo.MOVEL, "TIM");
		} else if ("553107".equals(codigo)) {
			return new Fone(Fone.Tipo.MOVEL, "UNICEL");
		} else if ("554040".equals(codigo)) {
			return new Fone(Fone.Tipo.SATELITE, "GLOBALSTAR");
		} else {
			return new Fone();
		}
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
			novo.setLocale(antigo.getLocale());
			if (!novo.igual(antigo)) {
				if (!antigo.getFone().equals(novo.getFone())) {
					atualizaFone(novo.getFone());
				}
				
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
	
	public Usuario buscarPerfil(String usuario) {
		Usuario perfil = null;
		if (usuario != null) {
			perfil = buscarUsuario(usuario);
			if (perfil != null) {
				perfil.eliminaSensiveis();;
			}
		}
		
		return perfil;
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
			if (Usuario.Status.INATIVO.equals(user.getStatus())) {
				throw new RegraNegocioException("501");
			} else {
				new GerenciaFinanceira().registraAcesso(user);
				// evitar envio de dados sensiveis ou desnecessarios
				user.eliminaSensiveis();
				return user;
			}
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
