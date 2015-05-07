package com.brazilboatshare.util;

import javax.servlet.http.HttpServletRequest;

public class URLUtil {

	public static String getURLRequest(HttpServletRequest request) {
    	StringBuffer url =  new StringBuffer();
        
    	url.append(request.getScheme()).append("://").append(request.getServerName());
        int serverPort = request.getServerPort();    
        if ((serverPort != 80) && (serverPort != 443)) {
            url.append(":").append(serverPort);
        }    	
        
        return url.toString();
	}
	
}
