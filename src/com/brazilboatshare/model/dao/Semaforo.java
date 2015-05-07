package com.brazilboatshare.model.dao;

import java.util.ConcurrentModificationException;
import java.util.Date;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import com.googlecode.objectify.Work;

public class Semaforo<R> {
	public static final String ATUALIZAR_COTA = "atualizarCota";
	public static final String ATUALIZAR_SALDO = "atualizarSaldo";
	public static final String VOTAR_NOTICIA = "votarNoticia";
	public static final String CADASTRAR_EMAIL = "cadastrarEmail";
	public static final String CADASTRAR_APELIDO = "cadastrarApelido";

	private static final String SEPARADOR = "#";
	private static int DELAY = 1000;		//tempo de espera, em milisegundos
	
	private String chave;
	
	/*
	 * Deve ter cuidado ao formar a chave do mutex
	 * Nao deve colidir com chave de atividade distinta!
	 */
	public Semaforo(String... nome) {
		StringBuffer chave = new StringBuffer();
		for (int i = 0; i < nome.length; i++) {
			chave.append(nome[i]);
			chave.append(SEPARADOR);
		}
		this.chave = chave.toString();
	}

	/*
	 *  metodo para implementar algoritmo "semaforo" no GAE
	 */
	public R ativar(Work<R> work) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key mutexKey = KeyFactory.createKey("Mutex", chave);
		Entity mutex = new Entity("Mutex", mutexKey);
		mutex.setProperty("data", new Date());		// para rastrear mutex eventualmente "presos" no datastore
		
		while (true) {
			Transaction txSave = datastore.beginTransaction();		
			try {
				datastore.get(txSave, mutexKey);
			} catch (EntityNotFoundException e) {
				R result = null;
				boolean executou = false;
				try {
					datastore.put(txSave, mutex);
					txSave.commit();
					result = work.run();
					executou = true;
					Transaction txDelete = datastore.beginTransaction();		
					datastore.delete(txDelete, mutex.getKey());
					txDelete.commit();
					return result;
				} catch (ConcurrentModificationException cme) {
					if (executou) {	// nunca deve acontecer mas... tentar novamente para evitar prender o monitor
						Transaction txDelete = datastore.beginTransaction();		
						datastore.delete(txDelete, mutex.getKey());
						txDelete.commit();
						return result;
					}
				}
			}
			aguardar(DELAY);
		}
	}
	
	private void aguardar(int tempo) {
		try {	
			Thread.sleep(tempo);
		} catch (InterruptedException ex) {
			throw new RuntimeException();
		}
	}

}
