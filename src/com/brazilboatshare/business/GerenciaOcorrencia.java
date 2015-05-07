package com.brazilboatshare.business;

import java.util.Date;

import com.brazilboatshare.model.dao.OcorrenciaDao;
import com.brazilboatshare.model.entity.Ocorrencia;

public class GerenciaOcorrencia {
	
	public void enviaProvidencia(Ocorrencia ocorr) {
		if (ocorr.getCodigo() != null && ocorr.getProvidencia() != null) {
			OcorrenciaDao pDao = new OcorrenciaDao();
			Ocorrencia original = pDao.get(ocorr.getCodigo());
			if (original != null) {
				original.setProvidencia(ocorr.getProvidencia());
				original.setDataProvidencia(new Date());
				pDao.save(original);
			}
		}
	}

}
