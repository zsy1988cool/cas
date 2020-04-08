package com.lucky.sso.config;

import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.CoreAuthenticationUtils;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalNameTransformerUtils;
import org.apereo.cas.authentication.support.password.PasswordEncoderUtils;
import org.apereo.cas.authentication.support.password.PasswordPolicyConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.jdbc.JdbcAuthenticationProperties;
import org.apereo.cas.configuration.model.support.jdbc.QueryEncodeJdbcAuthenticationProperties;
import org.apereo.cas.configuration.support.JpaBeans;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;

import com.lucky.sso.authentication.AccoutAuthentication;

@Configuration("CountAuthenticationConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CountAuthenticationConfiguration implements AuthenticationEventExecutionPlanConfigurer  {
	@Autowired
	private CasConfigurationProperties casProperties;
	
	@Autowired(required = false)
   // @Qualifier("queryAndEncodePasswordPolicyConfiguration")
    private PasswordPolicyConfiguration queryAndEncodePasswordPolicyConfiguration;

	@Autowired
	@Qualifier("jdbcPrincipalFactory")
    public PrincipalFactory jdbcPrincipalFactory;
	
	@Autowired
	@Qualifier("servicesManager")
	private ServicesManager servicesManager;

	@Bean(name = "jdbcAuthenticationHandlers")
    public Collection<AuthenticationHandler> jdbcAuthenticationHandlers() {
        final Collection<AuthenticationHandler> handlers = new HashSet<>();
        handlers.add(myAuthenticationHandler());
        return handlers;
    }
	
	@Bean
	public AuthenticationHandler myAuthenticationHandler() {
		final JdbcAuthenticationProperties jdbc = casProperties.getAuthn().getJdbc();
		final QueryEncodeJdbcAuthenticationProperties b = jdbc.getEncode().get(0);
		
		final AccoutAuthentication h = new AccoutAuthentication(b.getName(), servicesManager,
				jdbcPrincipalFactory, b.getOrder(), JpaBeans.newDataSource(b), b.getAlgorithmName(), b.getSql(), b.getPasswordFieldName(),
	            b.getSaltFieldName(), b.getExpiredFieldName(), b.getDisabledFieldName(), b.getNumberOfIterationsFieldName(), b.getNumberOfIterations(),
	            b.getStaticSalt());

	        h.setPasswordEncoder(PasswordEncoderUtils.newPasswordEncoder(b.getPasswordEncoder()));
	        h.setPrincipalNameTransformer(PrincipalNameTransformerUtils.newPrincipalNameTransformer(b.getPrincipalTransformation()));

	        if (queryAndEncodePasswordPolicyConfiguration != null) {
	            h.setPasswordPolicyConfiguration(queryAndEncodePasswordPolicyConfiguration);
	        }

	        h.setPrincipalNameTransformer(PrincipalNameTransformerUtils.newPrincipalNameTransformer(b.getPrincipalTransformation()));

	        if (StringUtils.isNotBlank(b.getCredentialCriteria())) {
	            h.setCredentialSelectionPredicate(CoreAuthenticationUtils.newCredentialSelectionPredicate(b.getCredentialCriteria()));
	        }

	        //LOGGER.debug("Created authentication handler [{}] to handle database url at [{}]", h.getName(), b.getUrl());
	        return h;
	}

	@Override
	public void configureAuthenticationExecutionPlan(final AuthenticationEventExecutionPlan plan) {
		plan.registerAuthenticationHandler(myAuthenticationHandler());
	}
}
