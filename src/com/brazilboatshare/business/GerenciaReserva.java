package com.brazilboatshare.business;

import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
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
	private static int PRAZO_CANCELAMENTO = 4;					// prazo maximo para cancelar reserva confirmada

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
				if (reserva.getRoteiro() != null && reserva.getSaida() != null && reserva.getRetorno() != null && reserva.getHoraSaida() != null && reserva.getHoraRetorno() != null) {
					Barco embarcacao = new BarcoDao().get(cota.getBarco());
					GregorianCalendar agora = new GregorianCalendar();
					agora.add(Calendar.SECOND, new Double(embarcacao.getMarina().getFuso()*60*60).intValue());
					Date saida = montaData(reserva.getSaida(), reserva.getHoraSaida());
					Date retorno = montaData(reserva.getRetorno(), reserva.getHoraRetorno());
					if (saida.before(retorno) && saida.after(agora.getTime())) {
						ReservaDao rDao = new ReservaDao();
						boolean longo = roteiroLongo(reserva.getRoteiro(), embarcacao);
						long prazo =  TimeUnit.DAYS.convert((saida.getTime()-agora.getTimeInMillis()), TimeUnit.MILLISECONDS);
						if (longo && prazo < PRAZO_RESERVA_ROTEIRO_LONGO) {
							throw new RegraNegocioException("509");							
						} else {
							if (!longo && (prazo < PRAZO_RESERVA_ROTEIRO_CURTO) && rDao.buscaReservaAtivaPeriodo(cota.getBarco(), reserva.getSaida(), reserva.getRetorno()) == null) {
								reserva.setStatus(Reserva.Status.CONFIRMADA);
							} else {
								reserva.setStatus(longo?Reserva.Status.AGUARDANDO_VALIDACAO:Reserva.Status.AGUARDANDO_CONFIRMACAO);									
							}
							reserva.setRsaida(null);
							reserva.setRretorno(null);
							reserva.setSolicitante(usuario);
							reserva.setCotista(cota.getUsuario());
							reserva.setBarco(cota.getBarco());
							reserva.setPontos(PONTOS_RESERVA_DEFAULT);
							reserva.setOrdem(0);
							agora.add(Calendar.SECOND, new Double(embarcacao.getMarina().getFuso()*60*60).intValue()*(-1));
							reserva.setSolicitacao(agora.getTime());
							return true;
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
	
	private Date montaData(String data, String hora) {
		return new GregorianCalendar(new Integer(data.substring(0, 4)), 
				new Integer(data.substring(4, 6))-1, 
				new Integer(data.substring(6)),
				new Integer(hora.substring(0,2)), 
				new Integer(hora.substring(3))).getTime();
	}
	
	public boolean roteiroLongo(List<Local> roteiro, Barco embarcacao) {
		if (embarcacao != null && roteiro != null && roteiro.size() > 1) {
			double dist = 0;
			boolean foraRaio = false;
			Local anterior = null;
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

	public Boolean cancelarReserva(String usuario, Long reserva) throws RegraNegocioException {
		if (reserva != null) {
			Reserva res = new ReservaDao().get(reserva);
			if (res != null) {
				Cota cota = new CotaDao().get(res.getCota());
				if (cota != null && (usuario.equals(cota.getUsuario()) || usuario.equals(cota.getDependente()))) {
					Set<Reserva.Status> validos = EnumSet.of(Reserva.Status.AGUARDANDO_CONFIRMACAO, Reserva.Status.AGUARDANDO_VALIDACAO, Reserva.Status.CONDOMINIO, Reserva.Status.CONFIRMADA);
					if (validos.contains(res.getStatus())) {
						res.setStatus(Reserva.Status.CANCELADA);
						Set<Reserva.Status> devolvem = EnumSet.of(Reserva.Status.AGUARDANDO_CONFIRMACAO, Reserva.Status.AGUARDANDO_VALIDACAO);
						if (devolvem.contains(res.getStatus())) {
							new ReservaDao().salvaCancelamento(res, cota);
						} else if (res.getStatus().equals(Reserva.Status.CONFIRMADA)) {
							return cancelarReservaConfirmada(res);
						} else {
							new ReservaDao().save(res);
						}
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private boolean cancelarReservaConfirmada(Reserva reserva) throws RegraNegocioException {
		Date saida = montaData(reserva.getSaida(), reserva.getHoraSaida());
		if (TimeUnit.DAYS.convert((new Date().getTime()-saida.getTime()), TimeUnit.MILLISECONDS) < PRAZO_CANCELAMENTO) {
			ReservaDao rDao = new ReservaDao();
			long prazo =  TimeUnit.DAYS.convert((saida.getTime()-new Date().getTime()), TimeUnit.MILLISECONDS);
			Reserva proxima = rDao.buscaReservaAtivaPeriodo(reserva.getBarco(), reserva.getSaida(), reserva.getRetorno());
			if (proxima == null) {
				
			} else if (prazo < PRAZO_RESERVA_ROTEIRO_CURTO && Reserva.Status.AGUARDANDO_CONFIRMACAO.equals(proxima.getStatus())) {
				reserva.setStatus(Reserva.Status.EM_CANCELAMENTO);
				proxima.setStatus(Reserva.Status.EM_CONFIRMACAO);
			} else if (Reserva.Status.AGUARDANDO_CONFIRMACAO.equals(proxima.getStatus())) {
				reserva.setStatus(Reserva.Status.EM_CANCELAMENTO);
				proxima.setStatus(Reserva.Status.EM_CONFIRMACAO);				
			}
			rDao.save(reserva);
			return true;
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
