package com.lucky.accounts.client.oauth2;

import javax.servlet.Filter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import org.apache.log4j.Logger;
import org.pac4j.oauth.client.CasOAuthWrapperClient;

import com.lucky.accounts.client.bind.OAuth2TokenHandler;

/**
 * @Description OAuth2过滤器基类
 * @Author seyi.zhou@luckincoffee.com
 * @Date 2020年4月10日 上午9:44:08
 */
public abstract class AbstractOAuthFilter implements Filter {

	protected Logger logger = Logger.getLogger(this.getClass());
	protected OAuth2TokenHandler tokenHandler;

	public AbstractOAuthFilter(CasOAuthWrapperClient client) {
		tokenHandler = new OAuth2TokenHandler(client);
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}
}
