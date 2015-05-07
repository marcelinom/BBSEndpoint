package com.brazilboatshare.business;

import com.brazilboatshare.email.AcessaEmail;
import com.brazilboatshare.model.entity.Usuario;
import com.brazilboatshare.util.Tradutor;

public class GerenciaMensagem {
		
	public void enviaEmail(Usuario para, String chaveAssunto, String chave, String... param) {
		AcessaEmail.sistemaEnvia(para, Tradutor.traduza(para.getLocale(),chaveAssunto), Tradutor.traduza(para.getLocale(),chave,param));
	}
	
}
