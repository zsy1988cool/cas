package com.lucky.cas.client.config;

import org.jasig.cas.client.util.AssertionThreadLocalFilter;
import org.pac4j.oauth.client.CasOAuthWrapperClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.lucky.accounts.client.oauth2.AuthorizationCallbackFilter;
import com.lucky.accounts.client.oauth2.AuthorizationOAuthFilter;
import com.lucky.accounts.client.oauth2.MyOAuthWrapperClient;

@Configuration
@Component
public class AccountClientConfig {

	@Autowired
	AccountOAuth2Config oauth2Config;

	@Bean
	CasOAuthWrapperClient oauthClient() {
		String clientId = oauth2Config.getClientId();
		String clientSecret = oauth2Config.getClientSecret();
		String casServerUrlPrefix = oauth2Config.getCasServerUrlPrefix();
		String callbackUrl = oauth2Config.getCallbackUrl();

		CasOAuthWrapperClient client = new MyOAuthWrapperClient(clientId, clientSecret, casServerUrlPrefix);
		client.setCallbackUrl(callbackUrl);
		return client;
	}

	@SuppressWarnings("rawtypes")
	@Bean
	public FilterRegistrationBean authorizationOAuthFilter() {
		FilterRegistrationBean<AuthorizationOAuthFilter> filterRegistration = new FilterRegistrationBean<AuthorizationOAuthFilter>();
		filterRegistration.setFilter(new AuthorizationOAuthFilter(oauthClient()));
		filterRegistration.addUrlPatterns("/*");
		filterRegistration.setOrder(2);
		return filterRegistration;
	}

	@SuppressWarnings("rawtypes")
	@Bean
	public FilterRegistrationBean authorizationCallbackFilter() {
		FilterRegistrationBean<AuthorizationCallbackFilter> filterRegistration = new FilterRegistrationBean<AuthorizationCallbackFilter>();
		filterRegistration.setFilter(new AuthorizationCallbackFilter(oauthClient()));
		filterRegistration.addUrlPatterns("/oauth2callback");
		filterRegistration.setOrder(1);
		return filterRegistration;
	}

	@SuppressWarnings("rawtypes")
	@Bean
	public FilterRegistrationBean assertionThreadLocalFilter() {
		FilterRegistrationBean<AssertionThreadLocalFilter> filterRegistration = new FilterRegistrationBean<AssertionThreadLocalFilter>();
		filterRegistration.setFilter(new AssertionThreadLocalFilter());
		filterRegistration.setEnabled(true);
		filterRegistration.addUrlPatterns("/*");
		filterRegistration.setOrder(7);
		return filterRegistration;
	}
}
