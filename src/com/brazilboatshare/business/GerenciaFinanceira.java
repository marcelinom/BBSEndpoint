package com.brazilboatshare.business;

import java.math.BigDecimal;
import java.util.List;

import com.brazilboatshare.exception.RegraNegocioException;
import com.brazilboatshare.model.dao.ContaCorrenteDao;
import com.brazilboatshare.model.dao.LogAcessoDao;
import com.brazilboatshare.model.dao.Semaforo;
import com.brazilboatshare.model.dao.ShardedCounterDao;
import com.brazilboatshare.model.dao.TipoContabilDao;
import com.brazilboatshare.model.dao.UsuarioDao;
import com.brazilboatshare.model.entity.ContaCorrente;
import com.brazilboatshare.model.entity.LogAcesso;
import com.brazilboatshare.model.entity.TipoContabil;
import com.brazilboatshare.model.entity.Usuario;
import com.googlecode.objectify.Work;

public class GerenciaFinanceira {
	
	public TipoContabil buscaTipoContabil(String tipo) {
		if (tipo != null) {
			return new TipoContabilDao().get(tipo);
		}
		
		return null;
	}

	public void salvaTipoContabil(TipoContabil tipo) {
		if (tipo != null) {
			new TipoContabilDao().save(tipo);
		}
	}
	
	public ContaCorrente buscaLancamento(Long codigo) {
		if (codigo != null) {
			ContaCorrenteDao cDao = new ContaCorrenteDao();
			return cDao.get(new Long(codigo));
		}
		
		return null;
	}
	
	public void registraAcesso(String apelido) {
		if (apelido != null) {
			registraAcesso(new UsuarioDao().get(apelido));
		}
	}
	
	public void registraAcesso(Usuario usuario) {
		if (usuario != null) {
			new LogAcessoDao().save(new LogAcesso(usuario.getApelido()));
		}
	}
	
	public static ContaCorrente lancaDebito(Usuario usuario, String tipo, BigDecimal valor) throws RegraNegocioException {
		if (usuario != null) {
			final ContaCorrente cc = new ContaCorrente(usuario.getApelido());
			cc.setTipo(new TipoContabilDao().get(tipo));
            cc.setValor(valor);
			return new Semaforo<ContaCorrente>(Semaforo.ATUALIZAR_SALDO, cc.getUsuario()).ativar(
	    			new Work<ContaCorrente>() {
	    				public ContaCorrente run() {
			            	ShardedCounterDao sDao = new ShardedCounterDao(cc.getUsuario(), ShardedCounterDao.SALDO_CONTA);
			                new ContaCorrenteDao().debitar(sDao, cc);
							return cc;
	    				}
	    			});			
		}
		throw new RegraNegocioException("530");
	}
	
	public static ContaCorrente lancaCredito(Usuario usuario, String tipo, BigDecimal valor) throws RegraNegocioException {
		if (usuario != null) {
			final ContaCorrente cc = new ContaCorrente(usuario.getApelido());
			cc.setTipo(new TipoContabilDao().get(tipo));
            cc.setValor(valor);
			return new Semaforo<ContaCorrente>(Semaforo.ATUALIZAR_SALDO, cc.getUsuario()).ativar(
	    			new Work<ContaCorrente>() {
	    				public ContaCorrente run() {
			            	ShardedCounterDao sDao = new ShardedCounterDao(cc.getUsuario(), ShardedCounterDao.SALDO_CONTA);
			                new ContaCorrenteDao().creditar(sDao, cc);
							return cc;
	    				}
	    			});			
		}
		throw new RegraNegocioException("530");
	}
	
	public List<ContaCorrente> listarUtilizacao(String usuario) {
		if (usuario != null) {
			ContaCorrenteDao cDao = new ContaCorrenteDao();
			return cDao.listarUtilizacao(usuario);
		}
		return null;
	}

	public BigDecimal saldoContaCorrente(String usuario) {
		if (usuario != null) {
			Usuario user = new UsuarioDao().get(usuario);
			if (user != null) {
				return new ShardedCounterDao(usuario, ShardedCounterDao.SALDO_CONTA).getTotal();
			}
		}
		return null;
	}

}
