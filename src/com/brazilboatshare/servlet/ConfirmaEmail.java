package com.brazilboatshare.servlet;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brazilboatshare.business.GerenciaUsuario;
import com.brazilboatshare.exception.RegraNegocioException;
import com.brazilboatshare.model.entity.Sessao;

public class ConfirmaEmail extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String URL_LOGIN = "http://brazilboatshare.appspot.com/entrar.html#/confirmaEmail/";
	private static final String URL_ERRO_VALIDACAO = "http://brazilboatshare.appspot.com/entrar.html#/naoConfirmaEmail/";
	private static final String PARAM_EMAIL = "email";
	private static final String PARAM_USUARIO = "usuario";
	private static final String PARAM_CODIGO = "codigo";

	public void doGet(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
    	String codigo = request.getParameter(PARAM_CODIGO);
    	String usuario = request.getParameter(PARAM_USUARIO);
    	String email = request.getParameter(PARAM_EMAIL);

    	try {
        	if (codigo != null && usuario != null && email != null) {
        		usuario = URLDecoder.decode(usuario, GerenciaUsuario.ENCODING);
        		email = URLDecoder.decode(email, GerenciaUsuario.ENCODING);
    			Sessao sessao = new GerenciaUsuario().confirmaEmail(usuario, email, new Long(codigo), request.getRemoteAddr());
    			if (sessao != null) {
    	    		response.sendRedirect(URL_LOGIN + sessao.getUsuario() + "/" + sessao.getCodigo());
    			} else {
    	    		response.sendRedirect(URL_ERRO_VALIDACAO);
    			}
       		}
    	} catch(RegraNegocioException rne) {
    		response.sendRedirect(URL_ERRO_VALIDACAO + rne.getMessage());
    	}
		
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}