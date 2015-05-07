package com.brazilboatshare.email;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.brazilboatshare.model.entity.Usuario;

public class AcessaEmail {
	
	public static String EMAIL_ORIGEM = "marcelinom@gmail.com";
	public static String EMAIL_REPLY = "contato@brazilboatshare.com";
	public static String NOME_ORIGEM = "Administrador Brazil Boat Share";
	public static String EMAIL_ATENDIMENTO = "contato@brazilboatshare.com";
	public static String NOME_ATENDIMENTO = "Atendimento Brazil Boat Share";
		
	public static void sistemaEnvia(String email, String nome, String assunto, String texto) {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		try {
			if (email != null) {
			    Message msg = new MimeMessage(session);
			    msg.setFrom(new InternetAddress(EMAIL_ORIGEM, NOME_ORIGEM));
			    msg.setReplyTo(new InternetAddress[]{new InternetAddress(EMAIL_REPLY, NOME_ORIGEM)});
			    msg.addRecipient(Message.RecipientType.TO, new InternetAddress(email, nome));
			    msg.setSubject(assunto);
			    msg.setText(texto);
			    Transport.send(msg);
			}
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}		
	}

	public static void sistemaEnvia(Usuario para, String assunto, String texto) {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		try {
			if (para != null && para.getEmail() != null) {
			    Message msg = new MimeMessage(session);
			    msg.setFrom(new InternetAddress(EMAIL_ORIGEM, NOME_ORIGEM));
			    msg.setReplyTo(new InternetAddress[]{new InternetAddress(EMAIL_REPLY, NOME_ORIGEM)});
			    msg.addRecipient(Message.RecipientType.TO, new InternetAddress(para.getEmail(), para.getNome()));
			    msg.setSubject(assunto);
			    msg.setText(texto);
			    Transport.send(msg);
			}
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}		
	}

	public static void sistemaEnviaHTML(Usuario para, String assunto, String texto) {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		try {
			if (para != null) {
			    Message msg = new MimeMessage(session);
			    msg.setFrom(new InternetAddress(EMAIL_ORIGEM, NOME_ORIGEM));
			    msg.setReplyTo(new InternetAddress[]{new InternetAddress(EMAIL_REPLY, NOME_ORIGEM)});
			    msg.addRecipient(Message.RecipientType.TO, new InternetAddress(para.getEmail(), para.getNome()));
			    msg.setSubject(assunto);
			    msg.setContent(texto, "text/html");
			    Transport.send(msg);
			}
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}		
	}

}
