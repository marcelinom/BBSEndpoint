package com.brazilboatshare.model.dao;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.brazilboatshare.model.entity.Cota;
import com.brazilboatshare.model.entity.Reserva;
import com.brazilboatshare.util.FiltroPesquisa;
import com.googlecode.objectify.VoidWork;

public class ReservaDao extends ObjectifyDao<Reserva> {

	public ReservaDao() {
		super(Reserva.class);
	}

	public boolean temReservaAtiva(Date checkon) {
		if (checkon != null) {
			List<FiltroPesquisa> filtro = new ArrayList<FiltroPesquisa>();
			filtro.add(new FiltroPesquisa("status", Reserva.Status.ATIVA));
			List<Reserva> reservas = list(filtro, null, null);
			filtro.clear();
			filtro.add(new FiltroPesquisa("status", Reserva.Status.CONDOMINIO));
			reservas.addAll(list(filtro, null, null));
			if (reservas != null) {
				for (Reserva reserva : reservas) {
					//so deve haver no maximo uma reserva ativa por vez. Em todo o caso...
					if (checkon.after(reserva.getRsaida()) && checkon.before(reserva.getRetorno())) {
						return true;
					}
				}
			}
		}
		return false;
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
