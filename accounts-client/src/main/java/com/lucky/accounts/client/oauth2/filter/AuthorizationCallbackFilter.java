package com.lucky.accounts.client.oauth2.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.oauth.client.CasOAuthWrapperClient;

/**
 * @Description ＯＡｕｔｈ２端口回调过滤器
 * @Author seyi.zhou@luckincoffee.com
 * @Date 2020年4月11日 下午4:58:06
 */
public class AuthorizationCallbackFilter extends AbstractOAuthFilter {

	public AuthorizationCallbackFilter(CasOAuthWrapperClient client) {
		super(client);
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain)
			throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest)req;
		final HttpServletResponse response = (HttpServletResponse)resp;
		
		try {
			if(tokenHandler.doAuthorizationCallback(request, response))
				filterChain.doFilter(request, response);
		} catch (Exception e) {
			e.printStackTrace();
        	response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
	}

}
