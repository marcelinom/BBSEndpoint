package com.brazilboatshare.util;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

public class Tradutor {

	private static final String ARQUIVO_MENSAGENS = "MessagesBundle";
	private static HashMap<String,ResourceBundle> resources = new HashMap<String,ResourceBundle>();
	
	public static String leia(String chave, String... param) {
		ResourceBundle bundle = carregaBundle(null);
		if (param != null) {
			return MessageFormat.format(bundle.getString(chave), (Object[])param);
		} else {
			return bundle.getString(chave);
		}
	}
	
	public static String traduza(String idioma, String chave, String... param) {
		ResourceBundle bundle = carregaBundle(idioma);
		if (param != null) {
			return MessageFormat.format(bundle.getString(chave), (Object[])param);
		} else {
			return bundle.getString(chave);
		}
	}
	
	private static ResourceBundle carregaBundle(String idioma) {
		ResourceBundle bundle = resources.get(idioma);
        if (bundle == null) {	//vai carregando progressivamente os arquivos apropriados de mensagens no servidor
	        if (idioma == null) {
	        	bundle = ResourceBundle.getBundle(ARQUIVO_MENSAGENS);
	        } else {
	        	bundle = ResourceBundle.getBundle(ARQUIVO_MENSAGENS, new Locale(idioma));
	        }
	       	resources.put(idioma, bundle);
        }
        return bundle;
	}
	
}
