package com.brazilboatshare.exception;

import com.google.api.server.spi.ServiceException;


public class RegraNegocioException extends ServiceException {

	private static final long serialVersionUID = 1L;
	
	public RegraNegocioException(String errorCode) {
		super(412, errorCode);
	}

}
