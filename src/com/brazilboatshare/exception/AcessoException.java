package com.brazilboatshare.exception;

import com.google.api.server.spi.ServiceException;

public class AcessoException extends ServiceException {

	private static final long serialVersionUID = 1L;
	
	public AcessoException(String errorCode) {
		super(401, errorCode);
	}
}
