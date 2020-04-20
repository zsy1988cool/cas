package com.lucky.sso.login.webflow.configurer;

import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.CredentialExpiredException;
import javax.security.auth.login.FailedLoginException;

import org.apereo.cas.authentication.PrincipalException;
import org.apereo.cas.authentication.RememberMeUsernamePasswordCredential;
import org.apereo.cas.authentication.adaptive.UnauthorizedAuthenticationException;
import org.apereo.cas.authentication.exceptions.AccountDisabledException;
import org.apereo.cas.authentication.exceptions.AccountPasswordMustChangeException;
import org.apereo.cas.authentication.exceptions.InvalidLoginLocationException;
import org.apereo.cas.authentication.exceptions.InvalidLoginTimeException;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.UnauthorizedServiceForPrincipalException;
import org.apereo.cas.ticket.UnsatisfiedAuthenticationPolicyException;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.configurer.DefaultLoginWebflowConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.ActionState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.builder.BinderConfiguration;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

import com.lucky.sso.authentication.credential.AccountCredential;
import com.lucky.sso.login.webflow.constants.AccountsConstants;

public class AccountLoginWebflowConfigurer extends DefaultLoginWebflowConfigurer {

	public AccountLoginWebflowConfigurer(FlowBuilderServices flowBuilderServices,
			FlowDefinitionRegistry flowDefinitionRegistry, ApplicationContext applicationContext,
			CasConfigurationProperties casProperties) {
		super(flowBuilderServices, flowDefinitionRegistry, applicationContext, casProperties);
	}
	
	/**
     * Create default action states.
     *
     * @param flow the flow
     */
	@Override
    protected void createDefaultActionStates(final Flow flow) {
        createInitialAuthenticationRequestValidationCheckAction(flow);
        createCreateTicketGrantingTicketAction(flow);
        createSendTicketGrantingTicketAction(flow);
        createGenerateServiceTicketAction(flow);
        createGatewayServicesMgmtAction(flow);
        createServiceAuthorizationCheckAction(flow);
        createRedirectToServiceActionState(flow);
        createHandleAuthenticationFailureAction(flow);
        createHandleUsercheckFailureAction(flow);
        createTerminateSessionAction(flow);
        createTicketGrantingTicketCheckAction(flow);
        createLoginFailureCheckAction(flow);
    }
	
	 /**
     * Create handle usercheck failure action.
     *
     * @param flow the flow
     */
    protected void createHandleUsercheckFailureAction(final Flow flow) {
        final ActionState handler = createActionState(flow, AccountsConstants.STATE_ID_HANDLE_USERCHECK_FAILURE,
        		AccountsConstants.ACTION_ID_USERCHECK_EXCEPTION_HANDLER);
        createTransitionForState(handler, AccountDisabledException.class.getSimpleName(), AccountsConstants.STATE_ID_VIEW_USERCHECK_FORM);
        createTransitionForState(handler, FailedLoginException.class.getSimpleName(), AccountsConstants.STATE_ID_VIEW_USERCHECK_FORM);
        createTransitionForState(handler, AccountNotFoundException.class.getSimpleName(), AccountsConstants.STATE_ID_VIEW_USERCHECK_FORM);
        createTransitionForState(handler, UnauthorizedServiceForPrincipalException.class.getSimpleName(), AccountsConstants.STATE_ID_VIEW_USERCHECK_FORM);
        createTransitionForState(handler, CasWebflowConstants.STATE_ID_SERVICE_UNAUTHZ_CHECK, AccountsConstants.STATE_ID_VIEW_USERCHECK_FORM);
        createStateDefaultTransition(handler, AccountsConstants.STATE_ID_VIEW_USERCHECK_FORM);
    }
    
    /**
     * Create handle captcha verify action.
     *
     * @param flow the flow
     */
    protected void createHandleCaptchaVerificationAction(final Flow flow) {
        final ActionState handler = createActionState(flow, AccountsConstants.STATE_ID_HANDLE_CAPTCHA_VERIFY,
        		AccountsConstants.ACTION_ID_CAPTCHA_VERIFICATION_ACTION);
        createTransitionForState(handler, CaptchaErrorException.class.getSimpleName(), AccountsConstants.STATE_ID_VIEW_USERCHECK_FORM);
        createTransitionForState(handler, FailedLoginException.class.getSimpleName(), AccountsConstants.STATE_ID_VIEW_USERCHECK_FORM);
        createTransitionForState(handler, AccountNotFoundException.class.getSimpleName(), AccountsConstants.STATE_ID_VIEW_USERCHECK_FORM);
        createTransitionForState(handler, UnauthorizedServiceForPrincipalException.class.getSimpleName(), AccountsConstants.STATE_ID_VIEW_USERCHECK_FORM);
        createStateDefaultTransition(handler, CasWebflowConstants.STATE_ID_VIEW_LOGIN_FORM);
    }
	
	 /**
     * Create handle authentication failure action.
     *
     * @param flow the flow
     */
	@Override
    protected void createHandleAuthenticationFailureAction(final Flow flow) {
        final ActionState handler = createActionState(flow, CasWebflowConstants.STATE_ID_HANDLE_AUTHN_FAILURE,
            CasWebflowConstants.ACTION_ID_AUTHENTICATION_EXCEPTION_HANDLER);
  
        createTransitionForState(handler, AccountLockedException.class.getSimpleName(), AccountsConstants.STATE_ID_HANDLE_LOGIN_FAILURE_CHECK);
        createTransitionForState(handler, AccountPasswordMustChangeException.class.getSimpleName(), AccountsConstants.STATE_ID_HANDLE_LOGIN_FAILURE_CHECK);
        createTransitionForState(handler, CredentialExpiredException.class.getSimpleName(), AccountsConstants.STATE_ID_HANDLE_LOGIN_FAILURE_CHECK);
        createTransitionForState(handler, InvalidLoginLocationException.class.getSimpleName(), AccountsConstants.STATE_ID_HANDLE_LOGIN_FAILURE_CHECK);
        createTransitionForState(handler, InvalidLoginTimeException.class.getSimpleName(), AccountsConstants.STATE_ID_HANDLE_LOGIN_FAILURE_CHECK);
        createTransitionForState(handler, FailedLoginException.class.getSimpleName(), AccountsConstants.STATE_ID_HANDLE_LOGIN_FAILURE_CHECK);
        createTransitionForState(handler, PrincipalException.class.getSimpleName(), AccountsConstants.STATE_ID_HANDLE_LOGIN_FAILURE_CHECK);
        createTransitionForState(handler, UnsatisfiedAuthenticationPolicyException.class.getSimpleName(), AccountsConstants.STATE_ID_HANDLE_LOGIN_FAILURE_CHECK);
        createTransitionForState(handler, UnauthorizedAuthenticationException.class.getSimpleName(), AccountsConstants.STATE_ID_HANDLE_LOGIN_FAILURE_CHECK);
        createTransitionForState(handler, CasWebflowConstants.STATE_ID_SERVICE_UNAUTHZ_CHECK, CasWebflowConstants.STATE_ID_SERVICE_UNAUTHZ_CHECK);
        createStateDefaultTransition(handler, CasWebflowConstants.STATE_ID_VIEW_LOGIN_FORM);
    }
	
	/**
     * Create login fail action.
     *
     * @param flow the flow
     */
    protected void createLoginFailureCheckAction(final Flow flow) {
        final ActionState action = createActionState(flow, AccountsConstants.STATE_ID_HANDLE_LOGIN_FAILURE_CHECK,
        		AccountsConstants.ACTION_ID_HANDLE_LOGIN_FAILURE_CHECK);
        createTransitionForState(action, CasWebflowConstants.TRANSITION_ID_AUTHENTICATION_FAILURE, CasWebflowConstants.STATE_ID_VIEW_LOGIN_FORM);
        createTransitionForState(action, CasWebflowConstants.TRANSITION_ID_ERROR, AccountsConstants.STATE_ID_VIEW_USERCHECK_FORM);
        createTransitionForState(action, CasWebflowConstants.TRANSITION_ID_SUCCESS, CasWebflowConstants.STATE_ID_VIEW_LOGIN_FORM);
        createTransitionForState(action, CasWebflowConstants.TRANSITION_ID_SUCCESS_WITH_WARNINGS, CasWebflowConstants.VIEW_ID_SHOW_AUTHN_WARNING_MSGS);
    }
    
    /**
     * Create remember me authn webflow config.
     *
     * @param flow the flow
     */
    @Override
    protected void createRememberMeAuthnWebflowConfig(final Flow flow) {
        if (casProperties.getTicket().getTgt().getRememberMe().isEnabled()) {
            createFlowVariable(flow, CasWebflowConstants.VAR_ID_CREDENTIAL, RememberMeUsernamePasswordCredential.class);
            final ViewState state = getState(flow, CasWebflowConstants.STATE_ID_VIEW_LOGIN_FORM, ViewState.class);
            final BinderConfiguration cfg = getViewStateBinderConfiguration(state);
            cfg.addBinding(new BinderConfiguration.Binding("rememberMe", null, false));
        } else {
            createFlowVariable(flow, CasWebflowConstants.VAR_ID_CREDENTIAL, AccountCredential.class);
        }
        
        final ViewState state = (ViewState) flow.getState(CasWebflowConstants.STATE_ID_VIEW_LOGIN_FORM);
        final BinderConfiguration cfg = getViewStateBinderConfiguration(state);
        cfg.addBinding(new BinderConfiguration.Binding("email", null, true));
        cfg.addBinding(new BinderConfiguration.Binding("telephone", null, true));
        cfg.addBinding(new BinderConfiguration.Binding("capcha", null, true));
        cfg.addBinding(new BinderConfiguration.Binding("capchaEnabled", null, true));
    }
}
