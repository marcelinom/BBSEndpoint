package com.brazilboatshare.model.dao;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.brazilboatshare.exception.RegraNegocioException;
import com.brazilboatshare.model.entity.Cota;
import com.brazilboatshare.model.entity.Reserva;
import com.brazilboatshare.util.FiltroPesquisa;
import com.googlecode.objectify.VoidWork;

public class ReservaDao extends ObjectifyDao<Reserva> {

	public ReservaDao() {
		super(Reserva.class);
	}

	public List<Reserva> listaReservas(String usuario, Cota cota) {
		if (usuario != null && cota != null) {
			List<FiltroPesquisa> filtro = new ArrayList<FiltroPesquisa>();
			//filtro.add(new FiltroPesquisa("cotista", cota.getUsuario()));
			filtro.add(new FiltroPesquisa("barco", cota.getBarco()));
			return list(filtro, null, null);
		}
		return null;
	}
	
	public Reserva buscaReservaAtivaPeriodo(String barco, String saida, String retorno) throws RegraNegocioException {
		Reserva res1 = buscaReservaAtivaPeriodo_fase1(barco, saida, retorno);
		Reserva res2 = buscaReservaAtivaPeriodo_fase2(barco, saida, retorno);
		return res1!=null?res1:res2;
	}
	
	private Reserva buscaReservaAtivaPeriodo_fase1(String barco, String saida, String retorno) throws RegraNegocioException {
		Reserva resultado = null;
		if (barco != null && saida != null && retorno != null) {
			Reserva.Status[] statuses = new Reserva.Status[] {Reserva.Status.AGUARDANDO_CONFIRMACAO, Reserva.Status.AGUARDANDO_VALIDACAO, Reserva.Status.ATIVA, Reserva.Status.CONDOMINIO, Reserva.Status.CONFIRMADA};
			List<FiltroPesquisa> filtro = new ArrayList<FiltroPesquisa>();
			filtro.add(new FiltroPesquisa("barco", barco));
			filtro.add(new FiltroPesquisa("status IN ", statuses));
			filtro.add(new FiltroPesquisa("saida >=", saida));
			List<Reserva> reservas =  list(filtro, Arrays.asList("saida"), null);
			if (reservas != null && reservas.size() > 0) {
				for (Reserva reserva : reservas) {
					if (reserva.getSaida().compareTo(retorno) <= 0) {
						if (reserva.getStatus().equals(Reserva.Status.CONDOMINIO)) {
							throw new RegraNegocioException("510");
						} else if (reserva.getStatus().equals(Reserva.Status.ATIVA)) {
							throw new RegraNegocioException("508");
						} else {
							resultado = reserva;
						}
					} else {
						break;
					}
				}
			}
		}
		return resultado;
	}
	
	private Reserva buscaReservaAtivaPeriodo_fase2(String barco, String saida, String retorno) throws RegraNegocioException {
		Reserva resultado = null;
		if (barco != null && saida != null && retorno != null) {
			Reserva.Status[] statuses = new Reserva.Status[] {Reserva.Status.AGUARDANDO_CONFIRMACAO, Reserva.Status.AGUARDANDO_VALIDACAO, Reserva.Status.ATIVA, Reserva.Status.CONDOMINIO, Reserva.Status.CONFIRMADA};
			List<FiltroPesquisa> filtro = new ArrayList<FiltroPesquisa>();
			filtro.add(new FiltroPesquisa("barco", barco));
			filtro.add(new FiltroPesquisa("status IN ", statuses));
			filtro.add(new FiltroPesquisa("retorno <=", retorno));
			List<Reserva> reservas =  list(filtro, Arrays.asList("-retorno"), null);
			if (reservas != null && reservas.size() > 0) {
				for (Reserva reserva : reservas) {
					if (reserva.getRetorno().compareTo(saida) >= 0) {
						if (reserva.getStatus().equals(Reserva.Status.CONDOMINIO)) {
							throw new RegraNegocioException("510");
						} else if (reserva.getStatus().equals(Reserva.Status.ATIVA)) {
							throw new RegraNegocioException("508");
						} else {
							resultado = reserva;
						}
					} else {
						break;
					}
				}
			}
		}
		return resultado;
	}
	
    public void salvaInclusao(final Reserva reserva, final Cota cota) {
        ofy().transact(new VoidWork() {	//mesma transacao!
            public void vrun() {
                save(reserva);
				cota.setPontos(cota.getPontos()-reserva.getPontos());
                new CotaDao().save(cota);
            }
        });
    }
    	
    public void salvaCancelamento(final Reserva reserva, final Cota cota) {
        ofy().transact(new VoidWork() {	//mesma transacao!
            public void vrun() {
				cota.setPontos(cota.getPontos()+reserva.getPontos());
				reserva.setPontos(0);
                save(reserva);
                new CotaDao().save(cota);
            }
        });
    }
    	
    public void salvaOfertaLeilao(final Reserva reserva, final Cota cota, final Integer pontos) {
        ofy().transact(new VoidWork() {	//mesma transacao!
            public void vrun() {
				cota.setPontos(cota.getPontos()+reserva.getPontos()-pontos);
				reserva.setPontos(pontos);
                save(reserva);
                new CotaDao().save(cota);
            }
        });
    }
    	
	
}
