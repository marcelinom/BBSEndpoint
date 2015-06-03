package com.brazilboatshare.business;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.brazilboatshare.email.AcessaEmail;
import com.brazilboatshare.exception.RegraNegocioException;
import com.brazilboatshare.model.dao.BarcoDao;
import com.brazilboatshare.model.dao.CotaDao;
import com.brazilboatshare.model.dao.DependenteDao;
import com.brazilboatshare.model.dao.UsuarioDao;
import com.brazilboatshare.model.entity.Barco;
import com.brazilboatshare.model.entity.Cota;
import com.brazilboatshare.model.entity.Dependente;
import com.brazilboatshare.model.entity.Usuario;
import com.brazilboatshare.util.Tradutor;

public class GerenciaCota {
	private static int ANO = 365;
	private static int PONTOS_ANUAL = 600;
	private static final String PATH_ATIVA_COTA = "http://brazilboatshare.com/entrar.html?login#/";

	public void criar(Cota cota) throws RegraNegocioException {
		if (cota != null && cota.getUsuario() != null & cota.getBarco() != null) {
			UsuarioDao uDao = new UsuarioDao();
			Usuario usuario = uDao.get(cota.getUsuario());
			if (usuario != null) {
				Barco barco = new BarcoDao().get(cota.getBarco());
				if (barco != null && barco.isAtiva()) {
					CotaDao cDao = new CotaDao();
					List<Cota> cotas = cDao.cotasAtivasDoBarco(barco.getNome());
					if (cotas == null || cotas.size() < barco.getCotas()) {
						cota.setCompra(new Date());
						cota.setStatus(Cota.Status.OK);
						cota.setPontos(calculaPontos());
						cDao.saveNow(cota);
						if (Usuario.Status.INATIVO.equals(usuario.getStatus())) {
							usuario.setStatus(Usuario.Status.OK);
							uDao.save(usuario);
						}
						AcessaEmail.sistemaEnvia(usuario, Tradutor.traduza(usuario.getLocale(),"cota.assunto.ativacao"), Tradutor.traduza(usuario.getLocale(),"cota.mensagem.ativacao", PATH_ATIVA_COTA));					
					} else {
						throw new RegraNegocioException("504");
					}
				}
			}
		}		
	}
	
	public int calculaPontos() {
		GregorianCalendar hoje = new GregorianCalendar();
		int dia = hoje.get(Calendar.DAY_OF_YEAR);
		double resta = ANO > dia ? ANO-dia+1d : 1d;
		return (int) (resta/ANO*PONTOS_ANUAL);
	}
	
	public void excluir(Cota cota) {
		if (cota != null) {
			new CotaDao().delete(cota);
		}		
	}
	
	public Usuario buscarDependente(Long cota) {
		Usuario dependente = null;
		if (cota != null) {
			Cota prop = new CotaDao().get(cota);
			if (prop != null && prop.getDependente() != null) {
				dependente = new UsuarioDao().get(prop.getDependente());	
				if (dependente != null) {
					dependente.eliminaSensiveis();
				}
			}
		}
		
		return dependente;
	}		
	
	public Usuario incluirDependente(String usuario, Long cota, Usuario dependente) throws RegraNegocioException {
		if (dependente != null) {
			if (cota != null) {
				Cota prop = new CotaDao().get(cota);
				if (prop != null && usuario.equals(prop.getUsuario()) && prop.getDependente() == null) {
					if (!prop.getUsuario().equals(dependente.getApelido())) {
						prop.setDependente(dependente.getApelido());
						new DependenteDao().salva(new Dependente(dependente.getApelido(), prop.getCodigo()), prop);
						dependente.setStatus(Usuario.Status.OK);
						new UsuarioDao().save(dependente);
						dependente.eliminaSensiveis();
						return dependente;
					} else {
						throw new RegraNegocioException("504");
					}
				}
			}
			return null;
		} else {
			throw new RegraNegocioException("543");
		}
	}		
	
	public void excluirDependente(Long cota) {
		if (cota != null) {
			Cota prop = new CotaDao().get(cota);
			if (prop != null) {
				prop.setDependente(null);
				
				DependenteDao dDao = new DependenteDao();
				Dependente dep = dDao.buscar(prop.getCodigo());
				if (dep != null) {
					dep.setExclusao(new Date());
					dDao.salva(dep, prop);
				}
			}
		}
	}		
	
	public List<Cota> listaCotasUsuario(String usuario) {
		return new CotaDao().lista(usuario);
	}
		
}
