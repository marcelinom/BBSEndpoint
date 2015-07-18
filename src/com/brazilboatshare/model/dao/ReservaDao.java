package com.brazilboatshare.model.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.brazilboatshare.model.entity.Cota;
import com.brazilboatshare.model.entity.Reserva;
import com.brazilboatshare.util.FiltroPesquisa;

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
	
	public List<Reserva> listaReservas(String usuario, Long cota) {
		if (usuario != null && cota != null) {
			Cota cCota = new CotaDao().get(cota);
			if (cCota != null && usuario.equals(cCota.getUsuario())) {
				List<FiltroPesquisa> filtro = new ArrayList<FiltroPesquisa>();
				filtro.add(new FiltroPesquisa("usuario", usuario));
				filtro.add(new FiltroPesquisa("barco", cCota.getBarco()));
				return list(filtro, null, null);
			}
		}
		return null;
	}
	
}
