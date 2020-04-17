package com.lucky.sso.login.webflow.configurer;

import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.CredentialExpiredException;
import javax.security.auth.login.FailedLoginException;

import org.apereo.cas.authentication.PrincipalException;
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
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

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
        createTransitionForState(handler, AccountLockedException.class.getSimpleName(), AccountsConstants.STATE_ID_VIEW_USERCHECK_FORM);
        createTransitionForState(handler, AccountPasswordMustChangeException.class.getSimpleName(), AccountsConstants.STATE_ID_VIEW_USERCHECK_FORM);
        createTransitionForState(handler, CredentialExpiredException.class.getSimpleName(), AccountsConstants.STATE_ID_VIEW_USERCHECK_FORM);
        createTransitionForState(handler, InvalidLoginLocationException.class.getSimpleName(), AccountsConstants.STATE_ID_VIEW_USERCHECK_FORM);
        createTransitionForState(handler, InvalidLoginTimeException.class.getSimpleName(), AccountsConstants.STATE_ID_VIEW_USERCHECK_FORM);
        createTransitionForState(handler, FailedLoginException.class.getSimpleName(), AccountsConstants.STATE_ID_VIEW_USERCHECK_FORM);
        createTransitionForState(handler, AccountNotFoundException.class.getSimpleName(), AccountsConstants.STATE_ID_VIEW_USERCHECK_FORM);
        createTransitionForState(handler, UnauthorizedServiceForPrincipalException.class.getSimpleName(), AccountsConstants.STATE_ID_VIEW_USERCHECK_FORM);
        createTransitionForState(handler, PrincipalException.class.getSimpleName(), AccountsConstants.STATE_ID_VIEW_USERCHECK_FORM);
        createTransitionForState(handler, UnsatisfiedAuthenticationPolicyException.class.getSimpleName(), AccountsConstants.STATE_ID_VIEW_USERCHECK_FORM);
        createTransitionForState(handler, UnauthorizedAuthenticationException.class.getSimpleName(), AccountsConstants.STATE_ID_VIEW_USERCHECK_FORM);
        createTransitionForState(handler, CasWebflowConstants.STATE_ID_SERVICE_UNAUTHZ_CHECK, AccountsConstants.STATE_ID_VIEW_USERCHECK_FORM);
        createStateDefaultTransition(handler, AccountsConstants.STATE_ID_VIEW_USERCHECK_FORM);

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
        createTransitionForState(handler, AccountDisabledException.class.getSimpleName(), CasWebflowConstants.VIEW_ID_ACCOUNT_DISABLED);
        createTransitionForState(handler, AccountLockedException.class.getSimpleName(), CasWebflowConstants.VIEW_ID_ACCOUNT_LOCKED);
        createTransitionForState(handler, AccountPasswordMustChangeException.class.getSimpleName(), CasWebflowConstants.VIEW_ID_MUST_CHANGE_PASSWORD);
        createTransitionForState(handler, CredentialExpiredException.class.getSimpleName(), CasWebflowConstants.VIEW_ID_EXPIRED_PASSWORD);
        createTransitionForState(handler, InvalidLoginLocationException.class.getSimpleName(), CasWebflowConstants.VIEW_ID_INVALID_WORKSTATION);
        createTransitionForState(handler, InvalidLoginTimeException.class.getSimpleName(), CasWebflowConstants.VIEW_ID_INVALID_AUTHENTICATION_HOURS);
        createTransitionForState(handler, FailedLoginException.class.getSimpleName(), CasWebflowConstants.STATE_ID_VIEW_LOGIN_FORM);
        createTransitionForState(handler, AccountNotFoundException.class.getSimpleName(), CasWebflowConstants.STATE_ID_VIEW_LOGIN_FORM);
        createTransitionForState(handler, UnauthorizedServiceForPrincipalException.class.getSimpleName(), CasWebflowConstants.STATE_ID_VIEW_LOGIN_FORM);
        createTransitionForState(handler, PrincipalException.class.getSimpleName(), CasWebflowConstants.STATE_ID_VIEW_LOGIN_FORM);
        createTransitionForState(handler, UnsatisfiedAuthenticationPolicyException.class.getSimpleName(), CasWebflowConstants.STATE_ID_VIEW_LOGIN_FORM);
        createTransitionForState(handler, UnauthorizedAuthenticationException.class.getSimpleName(), CasWebflowConstants.VIEW_ID_AUTHENTICATION_BLOCKED);
        createTransitionForState(handler, CasWebflowConstants.STATE_ID_SERVICE_UNAUTHZ_CHECK, CasWebflowConstants.STATE_ID_SERVICE_UNAUTHZ_CHECK);
        createStateDefaultTransition(handler, CasWebflowConstants.STATE_ID_VIEW_LOGIN_FORM);
    }
}
