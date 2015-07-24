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
	private static int PRAZO_RESERVA_ROTEIRO_CURTO = 7;   		// reservas imediatas

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
							boolean longo = roteiroLongo(reserva.getRoteiro(), reserva.getBarco());
							long prazo =  TimeUnit.DAYS.convert((reserva.getSaida().getTime()-agora.getTime()), TimeUnit.MILLISECONDS);
							if (longo && prazo < PRAZO_RESERVA_ROTEIRO_LONGO) {
								throw new RegraNegocioException("509");							
							} else {
								if (!longo && (prazo < PRAZO_RESERVA_ROTEIRO_CURTO) && !temReservasPeriodo(reserva, cota)) {
									reserva.setStatus(Reserva.Status.CONFIRMADA);
								} else {
									reserva.setStatus(longo?Reserva.Status.AGUARDANDO_VALIDACAO:Reserva.Status.AGUARDANDO_CONFIRMACAO);									
								}
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
	
	public boolean temReservasPeriodo(Reserva reserva, Cota cota) {
		return false;
	}
	
	public boolean roteiroLongo(List<Local> roteiro, String barco) {
		if (barco != null && roteiro != null && roteiro.size() > 1) {
			double dist = 0;
			boolean foraRaio = false;
			Local anterior = null;
			Barco embarcacao = new BarcoDao().get(barco);
			for (Local local : roteiro) {
				if (Local.distancia(local, embarcacao.getCidade()) > embarcacao.getMarina().getRaio()) {
					foraRaio = true;
				}
			    dist += anterior!=null?Local.distancia(anterior, local):0;
			    anterior = local;
			}

			if (dist > DISTANCIA_MAXIMA_ROTEIRO_CURTO && foraRaio) {
				return true;
			}
		}
		
		return false;
	}
	
	public List<Cota> listaCotasUsuario(String usuario) {
		return new CotaDao().lista(usuario);
	}
		
	public List<Reserva> listarReservasBarcoPeriodo(String barco, Date saida, Date retorno) {
		return new ReservaDao().listaReservasPeriodo(barco,saida,retorno);
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
				if (cota != null && (usuario.equals(cota.getUsuario()) || usuario.equals(cota.getDependente()))) {
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
				if (cota != null && (usuario.equals(cota.getUsuario()) || usuario.equals(cota.getDependente()))) {				
					new ReservaDao().salvaOfertaLeilao(res, cota, pontos);
					return true;
				}
			}
		}
		return false;
	}

	
}
