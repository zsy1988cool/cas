package com.lucky.cas.client.config;

//import org.jasig.cas.client.configuration.ConfigurationKeys;
//import org.jasig.cas.client.session.SingleSignOutFilter;
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

//	@SuppressWarnings("rawtypes")
//	@Bean
//	public FilterRegistrationBean singleSignOutFilter() {
//		FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
//
//		filterRegistration.setFilter(new SingleSignOutFilter());
//		filterRegistration.setEnabled(true);
//		filterRegistration.addUrlPatterns("/*");
//
//		filterRegistration.addInitParameter(ConfigurationKeys.CAS_SERVER_URL_PREFIX.getName(),
//		 "http://sso.lucky.net:8080/sso-server");
//		filterRegistration.addInitParameter(ConfigurationKeys.ARTIFACT_PARAMETER_NAME.getName(),
//				 "access_token");
//		filterRegistration.addInitParameter(ConfigurationKeys.CAS_SERVER_LOGIN_URL.getName(),
//				 "http://sso.lucky.net:8080/sso-server/logout?service=?");
//		filterRegistration.addInitParameter(ConfigurationKeys.ARTIFACT_PARAMETER_OVER_POST.getName(),  "1");
//		filterRegistration.setOrder(3);
//		return filterRegistration;
//	}

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
