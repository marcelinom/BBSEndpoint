package com.brazilboatshare.exception;

import com.google.api.server.spi.ServiceException;


public class ParametroException extends ServiceException {

	private static final long serialVersionUID = 1L;
	
	public ParametroException(String errorCode) {
		super(412, errorCode);
	}

}
