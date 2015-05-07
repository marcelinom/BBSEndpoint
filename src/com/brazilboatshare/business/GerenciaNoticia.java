package com.brazilboatshare.business;

import java.util.Date;
import java.util.List;

import com.brazilboatshare.model.dao.NoticiaDao;
import com.brazilboatshare.model.entity.Noticia;
import com.brazilboatshare.util.Valida;

public class GerenciaNoticia {

	public List<Noticia> listarPorRegiao(Noticia regiao) {
		if (regiao != null) {
			List<Noticia> noticias = new NoticiaDao().listarPorRegiao(regiao);
			if (noticias != null) {
				// nao enviar a foto neste momento, apenas o thumbnail
				for (Noticia not : noticias) {
					not.setFoto(null);
				}
				return noticias;
			}
		}
		return null;
	}
	
	public Noticia buscarNoticia(Long noticiaId) {
		if (noticiaId != null) {
			Noticia news = new NoticiaDao().get(noticiaId);
			if (news != null) {
				// enviar apenas a foto
				Noticia noticia = new Noticia();
				noticia.setFoto(news.getFoto());
				return noticia;
			}
		}
		return null;
	}
	
	public Noticia postar(Noticia noticia) {
		if (noticia.getTexto() != null && noticia.getPais() != null) {
			NoticiaDao nDao = new NoticiaDao();
			noticia.setData(new Date());
			if (noticia.getTipo() == null) {
				noticia.setTipo(Noticia.Tipo.INFORMACAO);
			}

			String pais = noticia.getPais().trim().toUpperCase();
			if (Valida.hashtag(pais)) {
				// inicializa ranking com um voto
				noticia.setPais(pais);
				nDao.save(noticia);
				return noticia;
			}
		}
		return null;
	}
	
}
