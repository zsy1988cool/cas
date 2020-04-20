package com.lucky.sso.login.webflow.config;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.audit.AuditableExecution;
import org.apereo.cas.authentication.AuthenticationServiceSelectionPlan;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.PrincipalException;
import org.apereo.cas.authentication.adaptive.UnauthorizedAuthenticationException;
import org.apereo.cas.authentication.exceptions.AccountDisabledException;
import org.apereo.cas.authentication.exceptions.AccountPasswordMustChangeException;
import org.apereo.cas.authentication.exceptions.InvalidLoginLocationException;
import org.apereo.cas.authentication.exceptions.InvalidLoginTimeException;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.jdbc.JdbcAuthenticationProperties;
import org.apereo.cas.configuration.model.support.jdbc.QueryEncodeJdbcAuthenticationProperties;
import org.apereo.cas.configuration.model.support.throttle.ThrottleProperties;
import org.apereo.cas.configuration.support.JpaBeans;
import org.apereo.cas.services.MultifactorAuthenticationProviderSelector;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.services.UnauthorizedServiceForPrincipalException;
import org.apereo.cas.ticket.UnsatisfiedAuthenticationPolicyException;
import org.apereo.cas.ticket.registry.TicketRegistrySupport;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.configurer.DefaultLoginWebflowConfigurer;
import org.apereo.cas.web.flow.resolver.impl.AbstractCasWebflowEventResolver;
import org.apereo.cas.web.support.NoOpThrottledSubmissionHandlerInterceptor;
import org.apereo.cas.web.support.ThrottledSubmissionHandlerInterceptor;
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

import com.lucky.sso.login.webflow.action.failure.AccountThrottledSubmissionHandler;
import com.lucky.sso.login.webflow.action.failure.HandleLoginFailureCheckAction;
import com.lucky.sso.login.webflow.action.usercheck.UserCheckAction;
import com.lucky.sso.login.webflow.action.usercheck.UserCheckExceptionHandlerAction;
import com.lucky.sso.login.webflow.action.usercheck.UserCheckWebflowEventResolver;
import com.lucky.sso.login.webflow.configurer.AccountLoginWebflowConfigurer;
import com.lucky.sso.login.webflow.handler.AccountsUserAuthenticationHandler;

@Configuration("AccoutWebflowConfiguration")
@EnableConfigurationProperties({CasConfigurationProperties.class})
public class AccoutWebflowConfiguration {
	@Autowired
	private CasConfigurationProperties casProperties;
	
	@Autowired
	@Qualifier("servicesManager")
	private ServicesManager servicesManager;
	
	@Autowired
	@Qualifier("jdbcPrincipalFactory")
    public PrincipalFactory jdbcPrincipalFactory;
	
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
        return new UserCheckExceptionHandlerAction(handledUsercheckExceptions());
    }
    

    @Bean(name="handleLoginFailureCheckAction")
    public Action handleLoginFailureCheckAction() {
    	return new HandleLoginFailureCheckAction(accountThrottledSubmissionHandler());
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
    
    // 错误次数处理
    @Bean(name = "authenticationThrottle")
    public ThrottledSubmissionHandlerInterceptor authenticationThrottle() {
    	return new NoOpThrottledSubmissionHandlerInterceptor();
    }

    @Bean
    public DataSource inspektrAuditTrailDataSource() {
        return JpaBeans.newDataSource(casProperties.getAuthn().getThrottle().getJdbc());
    }

    @Bean
    public AccountThrottledSubmissionHandler accountThrottledSubmissionHandler() {
//    	int recaptchahold = accountWebflowProperties.getRecaptchahold();
//    	int smshold = accountWebflowProperties.getSmshold();
//    	int lockaddseconds = accountWebflowProperties.getLockaddseconds();
//    	int locklimitseconds = accountWebflowProperties.getLocklimitseconds();
        final ThrottleProperties throttle = casProperties.getAuthn().getThrottle();
        final ThrottleProperties.Failure failure = throttle.getFailure();
        return new AccountThrottledSubmissionHandler(failure.getThreshold(),
            failure.getRangeSeconds(),
            inspektrAuditTrailDataSource(),
            throttle.getAppcode(),
            throttle.getJdbc().getAuditQuery(),
            failure.getCode());
    }
}
