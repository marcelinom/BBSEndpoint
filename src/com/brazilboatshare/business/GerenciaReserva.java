package com.brazilboatshare.business;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.brazilboatshare.exception.RegraNegocioException;
import com.brazilboatshare.model.dao.BarcoDao;
import com.brazilboatshare.model.dao.CotaDao;
import com.brazilboatshare.model.dao.ReservaDao;
import com.brazilboatshare.model.entity.Barco;
import com.brazilboatshare.model.entity.Cota;
import com.brazilboatshare.model.entity.Local;
import com.brazilboatshare.model.entity.Reserva;

public class GerenciaReserva {
	private static int PRAZO_RESERVA_ROTEIRO_LONGO = 15;   // ao menos 15 dias de antecedencia p/navegar a grande distancia da sede

	public Reserva incluirReserva(String usuario, Reserva reserva) throws RegraNegocioException {
		if (reserva != null && reserva.getCota() != null) {
			Cota cota = new CotaDao().get(reserva.getCota());
			if (reservaValida(reserva, cota) && (usuario.equals(cota.getUsuario()) || usuario.equals(cota.getDependente()))) {
				new ReservaDao().save(reserva);
				return reserva;
			} else {
				throw new RegraNegocioException("506");				
			}
		}
		throw new RegraNegocioException("505");
	}		
	
	public boolean reservaValida(Reserva reserva, Cota cota) throws RegraNegocioException {
		if (reserva != null && reserva.getCota() != null) {
			if (Cota.Status.OK.equals(cota.getStatus())) {
				if (reserva.getRoteiro() != null && reserva.getSaida() != null && reserva.getRetorno() != null) {
					Date agora = new Date();
					if (reserva.getSaida().before(reserva.getRetorno()) && reserva.getSaida().after(agora)) {
						ReservaDao rDao = new ReservaDao();
						if (!rDao.temReservaAtiva(reserva.getSaida())) {
							if (TimeUnit.DAYS.convert((reserva.getSaida().getTime()-agora.getTime()), TimeUnit.MILLISECONDS) < PRAZO_RESERVA_ROTEIRO_LONGO
									&& roteiroLongo(reserva.getRoteiro(), reserva.getBarco())) {
								throw new RegraNegocioException("509");							
							} else {
								reserva.setStatus(Reserva.Status.AGUARDANDO);
								reserva.setSolicitacao(agora);
								reserva.setCotista(cota.getUsuario());
								reserva.setBarco(cota.getBarco());
								reserva.setOrdem(0);
								return true;
							}
						} else {
							throw new RegraNegocioException("508");							
						}
					} else {
						throw new RegraNegocioException("507");
					}
				} else {
					throw new RegraNegocioException("505");
				}
			} 
		}
		throw new RegraNegocioException("506");
	}
	
	public boolean roteiroLongo(List<Local> roteiro, String barco) {
		if (barco != null && roteiro != null && roteiro.size() > 0) {
			Barco embarcacao = new BarcoDao().get(barco);
			for (Local local : roteiro) {
				if (Local.distancia(local, embarcacao.getCidade()) > embarcacao.getMarina().getRaio()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public List<Cota> listaCotasUsuario(String usuario) {
		return new CotaDao().lista(usuario);
	}
		
	public List<Reserva> listarReservasBarco(String usuario, Long cota) {
		return new ReservaDao().listaReservas(usuario, cota);
	}

}
