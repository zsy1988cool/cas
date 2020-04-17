package com.lucky.sso.config;

import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.audit.AuditableExecution;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationServiceSelectionPlan;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.CoreAuthenticationUtils;
import org.apereo.cas.authentication.PrincipalException;
import org.apereo.cas.authentication.adaptive.UnauthorizedAuthenticationException;
import org.apereo.cas.authentication.exceptions.AccountDisabledException;
import org.apereo.cas.authentication.exceptions.AccountPasswordMustChangeException;
import org.apereo.cas.authentication.exceptions.InvalidLoginLocationException;
import org.apereo.cas.authentication.exceptions.InvalidLoginTimeException;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalNameTransformerUtils;
import org.apereo.cas.authentication.support.password.PasswordEncoderUtils;
import org.apereo.cas.authentication.support.password.PasswordPolicyConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.jdbc.JdbcAuthenticationProperties;
import org.apereo.cas.configuration.model.support.jdbc.QueryEncodeJdbcAuthenticationProperties;
import org.apereo.cas.configuration.support.JpaBeans;
import org.apereo.cas.services.MultifactorAuthenticationProviderSelector;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.services.UnauthorizedServiceForPrincipalException;
import org.apereo.cas.ticket.UnsatisfiedAuthenticationPolicyException;
import org.apereo.cas.ticket.registry.TicketRegistrySupport;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.configurer.DefaultLoginWebflowConfigurer;
import org.apereo.cas.web.flow.resolver.impl.AbstractCasWebflowEventResolver;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.CookieGenerator;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.execution.Action;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.lucky.sso.authentication.AccoutAuthentication;
import com.lucky.sso.login.webflow.action.UserCheckAction;
import com.lucky.sso.login.webflow.action.UserCheckWebflowEventResolver;
import com.lucky.sso.login.webflow.action.UsercheckExceptionHandlerAction;
import com.lucky.sso.login.webflow.configurer.AccountLoginWebflowConfigurer;
import com.lucky.sso.login.webflow.handler.AccountsUserAuthenticationHandler;

@Configuration("CountAuthenticationConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class AccoutAuthenticationConfiguration implements AuthenticationEventExecutionPlanConfigurer  {
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
    
    @Autowired
    FlowBuilderServices flowBuilderServices;
    @Autowired
    @Qualifier("loginFlowRegistry")
    FlowDefinitionRegistry loginFlowRegistry;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    @Qualifier("logoutFlowRegistry")
    private FlowDefinitionRegistry logoutFlowRegistry;
	
    @Bean(name="defaultWebflowConfigurer")
    public CasWebflowConfigurer defaultWebflowConfigurer() {
        final DefaultLoginWebflowConfigurer c = new AccountLoginWebflowConfigurer(flowBuilderServices, loginFlowRegistry, applicationContext, casProperties);
        c.setLogoutFlowDefinitionRegistry(logoutFlowRegistry);
        return c;
    }
    
    @Bean(name="userCheckAction")
    public Action authenticationViaFormAction() {
        return new UserCheckAction(initialUserCheckWebflowEventResolver());
    }
    
    @Bean(name="usercheckExceptionHandler")
    public Action usercheckExceptionHandler() {
        return new UsercheckExceptionHandlerAction(handledUsercheckExceptions());
    }
    
    @Bean
    public Set<Class<? extends Throwable>> handledUsercheckExceptions() {
        /*
         * Order is important here; We want the account policy exceptions to be handled
         * first before moving onto more generic errors. In the event that multiple handlers
         * are defined, where one fails due to account policy restriction and one fails
         * due to a bad password, we want the error associated with the account policy
         * to be processed first, rather than presenting a more generic error associated
         */
        final Set<Class<? extends Throwable>> errors = new LinkedHashSet<>();
        errors.add(javax.security.auth.login.AccountLockedException.class);
        errors.add(javax.security.auth.login.CredentialExpiredException.class);
        errors.add(javax.security.auth.login.AccountExpiredException.class);
        errors.add(AccountDisabledException.class);
        errors.add(InvalidLoginLocationException.class);
        errors.add(AccountPasswordMustChangeException.class);
        errors.add(InvalidLoginTimeException.class);

        errors.add(javax.security.auth.login.AccountNotFoundException.class);
        errors.add(javax.security.auth.login.FailedLoginException.class);
        errors.add(UnauthorizedServiceForPrincipalException.class);
        errors.add(PrincipalException.class);
        errors.add(UnsatisfiedAuthenticationPolicyException.class);
        errors.add(UnauthorizedAuthenticationException.class);

        errors.addAll(casProperties.getAuthn().getExceptions().getExceptions());

        return errors;
    }
    
    @Autowired
    @Qualifier("defaultAuthenticationSystemSupport")
    private ObjectProvider<AuthenticationSystemSupport> authenticationSystemSupport;
    
    @Autowired
    @Qualifier("centralAuthenticationService")
    private ObjectProvider<CentralAuthenticationService> centralAuthenticationService;
    
    @Autowired
    @Qualifier("defaultTicketRegistrySupport")
    private ObjectProvider<TicketRegistrySupport> ticketRegistrySupport;
    
    @Autowired
    @Qualifier("warnCookieGenerator")
    private ObjectProvider<CookieGenerator> warnCookieGenerator;
    
    @Autowired
    @Qualifier("authenticationServiceSelectionPlan")
    private ObjectProvider<AuthenticationServiceSelectionPlan> authenticationServiceSelectionPlan;
    
    @Autowired
    @Qualifier("multifactorAuthenticationProviderSelector")
    private MultifactorAuthenticationProviderSelector multifactorAuthenticationProviderSelector;
    
    @Autowired
    @Qualifier("registeredServiceAccessStrategyEnforcer")
    private AuditableExecution registeredServiceAccessStrategyEnforcer;

    @Bean(name="userCheckWebflowEventResolver")
    public AbstractCasWebflowEventResolver initialUserCheckWebflowEventResolver() {
        final UserCheckWebflowEventResolver r = new UserCheckWebflowEventResolver(
            authenticationSystemSupport.getIfAvailable(),
            centralAuthenticationService.getIfAvailable(),
            servicesManager,
            ticketRegistrySupport.getIfAvailable(),
            warnCookieGenerator.getIfAvailable(),
            authenticationServiceSelectionPlan.getIfAvailable(),
            multifactorAuthenticationProviderSelector);
        return r;
    }
    @Bean
	public AccountsUserAuthenticationHandler userNameAuthenticationHandler() {
    	final JdbcAuthenticationProperties jdbc = casProperties.getAuthn().getJdbc();
		final QueryEncodeJdbcAuthenticationProperties b = jdbc.getEncode().get(0);
		
		final AccountsUserAuthenticationHandler h = new AccountsUserAuthenticationHandler(b.getName(), servicesManager,
				jdbcPrincipalFactory, b.getOrder(), JpaBeans.newDataSource(b));
	        return h;
    }
}
