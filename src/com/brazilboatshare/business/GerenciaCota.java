package com.brazilboatshare.business;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.brazilboatshare.email.AcessaEmail;
import com.brazilboatshare.model.dao.BarcoDao;
import com.brazilboatshare.model.dao.CotaDao;
import com.brazilboatshare.model.dao.UsuarioDao;
import com.brazilboatshare.model.entity.Barco;
import com.brazilboatshare.model.entity.Cota;
import com.brazilboatshare.model.entity.Usuario;
import com.brazilboatshare.util.Tradutor;

public class GerenciaCota {
	private static int ANO = 365;
	private static int PONTOS_ANUAL = 600;
	private static final String PATH_ATIVA_COTA = "http://brazilboatshare.com/entrar.html?login#/";

	public void criar(Cota cota) {
		if (cota != null && cota.getUsuario() != null & cota.getBarco() != null) {
			UsuarioDao uDao = new UsuarioDao();
			Usuario usuario = uDao.get(cota.getUsuario());
			if (usuario != null) {
				Barco barco = new BarcoDao().get(cota.getBarco());
				if (barco != null && barco.isAtiva()) {
					cota.setCompra(new Date());
					cota.setStatus(Cota.Status.OK);
					cota.setPontos(calculaPontos());
					new CotaDao().saveNow(cota);
					
					if (Usuario.Status.INATIVO.equals(usuario.getStatus())) {
						usuario.setStatus(Usuario.Status.OK);
						uDao.save(usuario);
					}
					
					AcessaEmail.sistemaEnvia(usuario, Tradutor.traduza(usuario.getLocale(),"cota.assunto.ativacao"), Tradutor.traduza(usuario.getLocale(),"cota.mensagem.ativacao", PATH_ATIVA_COTA));					
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
	
	public List<Cota> listar() {
		return new CotaDao().list(null, null, null);
	}
		
}
