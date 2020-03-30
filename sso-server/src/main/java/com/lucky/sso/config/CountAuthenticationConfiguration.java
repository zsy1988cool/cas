package com.lucky.sso.config;

import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lucky.sso.authentication.AccoutAuthentication;

@Configuration("CountAuthenticationConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CountAuthenticationConfiguration implements AuthenticationEventExecutionPlanConfigurer  {
	@Autowired
	private CasConfigurationProperties casProperties;

	@Autowired
	@Qualifier("servicesManager")
	private ServicesManager servicesManager;

	@SuppressWarnings("deprecation")
	@Bean
	public AuthenticationHandler myAuthenticationHandler() {
		return new AccoutAuthentication(AccoutAuthentication.class.getName(),
				servicesManager, new DefaultPrincipalFactory(), 1);
	}

	@Override
	public void configureAuthenticationExecutionPlan(final AuthenticationEventExecutionPlan plan) {
		plan.registerAuthenticationHandler(myAuthenticationHandler());
	}
}
