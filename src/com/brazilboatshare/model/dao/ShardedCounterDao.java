package com.brazilboatshare.model.dao;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.brazilboatshare.model.entity.ShardedCounter;
import com.googlecode.objectify.cmd.Query;

public class ShardedCounterDao extends ObjectifyDao<ShardedCounter> {
	private static final String SEPARADOR = "#";
    
	public static final String SALDO_CONTA = "saldo";    
    public static final String SALDO_PONTO = "pontos";
	
    private static final int NUM_SHARDS = 5;
    private final Random generator = new Random();
    
    private String nome;
    
    public ShardedCounterDao(final String usuario, final String contador) {
        super(ShardedCounter.class);
        this.nome = geraNome(usuario, contador);
    }

    public BigDecimal getTotal() {
        Query<ShardedCounter> q = ofy().load().type(ShardedCounter.class);
        List<ShardedCounter> shards = q.filter("nome", nome).list();
        if (shards != null && !shards.isEmpty()) {
	        BigDecimal sum = null;
            for (ShardedCounter shard : shards) {
                sum = sum==null?shard.getParcial():sum.add(shard.getParcial());
            }
	        return sum;
        } else {
        	return null;
        }
    }
    
    /**
     * Increment the value of this sharded counter.
     */
    public void acrescentar(final BigDecimal valor) {
    	int random = generator.nextInt(NUM_SHARDS); 
        ShardedCounter shard = get(nome + random);
        if (shard == null) {
        	shard = new ShardedCounter(nome, random); 
        }
        shard.setParcial(shard.getParcial().add(valor));
        saveNow(shard);
    }    

    public void remove() {
        Query<ShardedCounter> q = ofy().load().type(ShardedCounter.class);
        List<ShardedCounter> shards = q.filter("nome", nome).list();
        if (shards != null && !shards.isEmpty()) {
            ofy().transactionless().delete().entities(shards);
        }
    }
    
    public void inicializa(final BigDecimal valor) {
        Query<ShardedCounter> q = ofy().load().type(ShardedCounter.class);
        List<ShardedCounter> shards = q.filter("nome", nome).list();
        if (shards == null || shards.isEmpty()) {
        	shards = new ArrayList<ShardedCounter>();
       		shards.add(new ShardedCounter(nome, 0));
        }

        boolean primeiro = true;
        for (ShardedCounter shard : shards) {
            shard.setParcial(primeiro?valor:BigDecimal.ZERO);
            primeiro = false;
        }
        ofy().save().entities(shards);
    }
    
	private String geraNome(String usuario, String contador) {
		return usuario + SEPARADOR + contador;
	}	    

}
