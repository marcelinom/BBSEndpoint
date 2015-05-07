package com.brazilboatshare.util;

import java.util.regex.Pattern;

public class Valida {
    // entre 6 a 15 caracteres, comecando por letra e contendo apenas letras minusculas e/ou numeros, sem espacos, numeros, hifen (-) ou underscore (_), entre 6 a 15 caracteres
	private static Pattern PATTERN_USERNAME = Pattern.compile("^[\\p{L}&&[^\\p{Lu}]]{1}[0-9_\\-\\p{L}&&[^\\p{Lu}]]{5,14}$", Pattern.UNICODE_CHARACTER_CLASS);
    // deve ter ao menos dois caracteres 
	private static Pattern PATTERN_HASHTAG = Pattern.compile("^.{2,}$", Pattern.UNICODE_CHARACTER_CLASS);
    // entre 2 a 30 caracteres, contendo apenas letras, espacos, hifen (-), ponto (.) ou barra (/)
	private static Pattern PATTERN_NOME = Pattern.compile("^[\\p{L}\\s\\-\\./]{2,30}$", Pattern.UNICODE_CHARACTER_CLASS);
	// validacao bem elastica para email
	private static Pattern PATTERN_EMAIL = Pattern.compile("^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)+$");
    // entre 6 a 20 caracteres, contendo ao menos uma letra e um numero
    private static Pattern PATTERN_SENHA = Pattern.compile("((?=.*\\d)(?=.*[\\p{L}]).{6,20})");
	private static Pattern PATTERN_FONE = Pattern.compile("^[0-9]{1,15}$");
	private static Pattern PATTERN_IDIOMA = Pattern.compile("^[\\p{L}\\-]{2,8}$");

	public static boolean email(final String texto) {
		return PATTERN_EMAIL.matcher(texto).matches();
	}
	public static boolean username(final String texto) {
		return PATTERN_USERNAME.matcher(texto).matches();
	}
	public static boolean nome(final String texto) {
		return PATTERN_NOME.matcher(texto).matches();
	}
	public static boolean fone(final String texto) {
		return PATTERN_FONE.matcher(texto).matches();
	}
	public static boolean senha(final String texto) {
		return PATTERN_SENHA.matcher(texto).matches();
	}
	public static boolean idioma(final String texto) {
		return PATTERN_IDIOMA.matcher(texto).matches();
	}
	public static boolean hashtag(final String texto) {
		return PATTERN_HASHTAG.matcher(texto).matches();
	}
	
}
