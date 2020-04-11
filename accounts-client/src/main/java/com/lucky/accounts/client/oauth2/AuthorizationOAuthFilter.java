package com.lucky.accounts.client.oauth2;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.oauth.client.CasOAuthWrapperClient;

/**
 * @Description OAuth2过滤器，判断是否已经获得授权
 * @Author seyi.zhou@luckincoffee.com
 * @Date 2020年4月10日 上午9:44:08
 */
public class AuthorizationOAuthFilter extends AbstractOAuthFilter {

	public AuthorizationOAuthFilter(CasOAuthWrapperClient client) {
		super(client);
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain)
			throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) req;
		final HttpServletResponse response = (HttpServletResponse) resp;

		try {
			if(tokenHandler.doAuthorization(request, response))
				filterChain.doFilter(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
			// throw new RuntimeException(e);
		}
	}
}
