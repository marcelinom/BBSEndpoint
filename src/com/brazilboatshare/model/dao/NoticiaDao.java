package com.brazilboatshare.model.dao;
import java.util.ArrayList;
import java.util.List;

import com.brazilboatshare.model.entity.Noticia;
import com.brazilboatshare.util.FiltroPesquisa;

public class NoticiaDao extends ObjectifyDao<Noticia> {
	private static int LIMITE_MAX_NOTICIAS = 25;
	 
	    public NoticiaDao() {
	        super(Noticia.class);
	    }
	    
		public List<Noticia> listarPorRegiao(Noticia regiao) {
			if (regiao != null) {
				List<FiltroPesquisa> filtro = new ArrayList<FiltroPesquisa>();	
				filtro.add(new FiltroPesquisa("pais", regiao.getPais().trim().toUpperCase()));
				return list(filtro, null, LIMITE_MAX_NOTICIAS);
			}
			return null;
		}
			    
}
