package com.brazilboatshare.model.dao;

import com.brazilboatshare.model.entity.Administrador;
import com.brazilboatshare.model.entity.Barco;
import com.brazilboatshare.model.entity.Componente;
import com.brazilboatshare.model.entity.Confirmacao;
import com.brazilboatshare.model.entity.ContaCorrente;
import com.brazilboatshare.model.entity.Cota;
import com.brazilboatshare.model.entity.Lancamento;
import com.brazilboatshare.model.entity.LogAcesso;
import com.brazilboatshare.model.entity.MensagemSistema;
import com.brazilboatshare.model.entity.Noticia;
import com.brazilboatshare.model.entity.Ocorrencia;
import com.brazilboatshare.model.entity.Pais;
import com.brazilboatshare.model.entity.Reserva;
import com.brazilboatshare.model.entity.Sessao;
import com.brazilboatshare.model.entity.ShardedCounter;
import com.brazilboatshare.model.entity.TextoPadrao;
import com.brazilboatshare.model.entity.TipoContabil;
import com.brazilboatshare.model.entity.Usuario;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.impl.translate.opt.BigDecimalLongTranslatorFactory;

public class ObjectifyRegistering {
	private static boolean registrado = false;
	
	public static void register() {
		if (!registrado) {
			// traduz BigDecimais para Long... para contornar falta de suporte do GAE a BigDecimal 
			ObjectifyService.factory().getTranslators().add(new BigDecimalLongTranslatorFactory());			
			
	        ObjectifyService.register(Administrador.class);
	        ObjectifyService.register(Barco.class);
	        ObjectifyService.register(Componente.class);
	        ObjectifyService.register(Confirmacao.class);
	        ObjectifyService.register(ContaCorrente.class);
	        ObjectifyService.register(Cota.class);
	        ObjectifyService.register(Lancamento.class);
	        ObjectifyService.register(LogAcesso.class);
	        ObjectifyService.register(MensagemSistema.class);
	        ObjectifyService.register(Noticia.class);
	        ObjectifyService.register(Ocorrencia.class);
			ObjectifyService.register(Pais.class);
	        ObjectifyService.register(Reserva.class);
	        ObjectifyService.register(ShardedCounter.class);
	        ObjectifyService.register(Sessao.class);
	        ObjectifyService.register(TextoPadrao.class);
	        ObjectifyService.register(TipoContabil.class);
	        ObjectifyService.register(Usuario.class);
	        registrado = true;
		}
	}

}
