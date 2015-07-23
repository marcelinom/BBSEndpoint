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
	private static int PONTOS_RESERVA_DEFAULT = 10;   
	private static int DISTANCIA_MAXIMA_ROTEIRO_CURTO = 12;  	// milhas nauticas   
	private static int PRAZO_RESERVA_ROTEIRO_LONGO = 15;   		// ao menos 15 dias de antecedencia p/navegar a grande distancia da sede

	public Reserva incluirReserva(String usuario, Reserva reserva) throws RegraNegocioException {
		if (reserva != null && reserva.getCota() != null) {
			Cota cota = new CotaDao().get(reserva.getCota());
			if (reservaValida(usuario, reserva, cota) && (usuario.equals(cota.getUsuario()) || usuario.equals(cota.getDependente()))) {
				new ReservaDao().salvaInclusao(reserva, cota);
				return reserva;
			} else {
				throw new RegraNegocioException("506");				
			}
		}
		throw new RegraNegocioException("505");
	}		
	
	public boolean reservaValida(String usuario, Reserva reserva, Cota cota) throws RegraNegocioException {
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
								reserva.setSolicitante(usuario);
								reserva.setCotista(cota.getUsuario());
								reserva.setBarco(cota.getBarco());
								reserva.setPontos(PONTOS_RESERVA_DEFAULT);
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
		if (barco != null && roteiro != null && roteiro.size() > 1) {
			double dist = 0;
			Local anterior = null;
			for (Local local : roteiro) {
			     dist += anterior!=null?Local.distancia(anterior, local):0;
			     anterior = local;
			}

			if (dist > DISTANCIA_MAXIMA_ROTEIRO_CURTO) {
				Barco embarcacao = new BarcoDao().get(barco);
				for (Local local : roteiro) {
					if (Local.distancia(local, embarcacao.getCidade()) > embarcacao.getMarina().getRaio()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public List<Cota> listaCotasUsuario(String usuario) {
		return new CotaDao().lista(usuario);
	}
		
	public List<Reserva> listarReservasBarco(String usuario, Long cota) {
		if (cota != null) {
			Cota cCota = new CotaDao().get(cota);
			if (cCota != null && (usuario.equals(cCota.getUsuario()) || usuario.equals(cCota.getDependente()))) {
				return new ReservaDao().listaReservas(usuario, cCota);
			}
		}
		return null;
	}

	public Reserva buscarReserva(String usuario, Long reserva) {
		if (reserva != null) {
			Reserva res = new ReservaDao().get(reserva);
			if (res != null && new CotaDao().temCotaDoBarco(usuario, res.getBarco())) {
				return new ReservaDao().get(reserva);					
			}
		}
		return null;
	}

	public Boolean cancelarReserva(String usuario, Long reserva) {
		if (reserva != null) {
			Reserva res = new ReservaDao().get(reserva);
			if (res != null) {
				Cota cota = new CotaDao().get(res.getCota());
				if (cota != null && (cota.getDependente().equals(usuario) || cota.getUsuario().equals(usuario))) {
					res.setStatus(Reserva.Status.CANCELADA);
					new ReservaDao().salvaCancelamento(res, cota);
					return true;
				}
			}
		}
		return false;
	}

	public Boolean oferecerPontosLeilao(String usuario, Long reserva, Integer pontos) {
		if (reserva != null) {
			Reserva res = new ReservaDao().get(reserva);
			if (res != null) {
				Cota cota = new CotaDao().get(res.getCota());
				if (cota != null && (cota.getDependente().equals(usuario) || cota.getUsuario().equals(usuario))) {
					new ReservaDao().salvaOfertaLeilao(res, cota, pontos);
					return true;
				}
			}
		}
		return false;
	}

	
}
