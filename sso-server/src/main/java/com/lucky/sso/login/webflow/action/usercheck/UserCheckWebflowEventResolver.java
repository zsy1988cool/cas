package com.lucky.sso.login.webflow.action.usercheck;

import org.apereo.cas.util.CollectionUtils;
import java.util.Set;

import javax.security.auth.login.FailedLoginException;
import javax.servlet.http.HttpServletResponse;

import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.AuthenticationException;
import org.apereo.cas.authentication.AuthenticationServiceSelectionPlan;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.services.MultifactorAuthenticationProviderSelector;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.ticket.AbstractTicketException;
import org.apereo.cas.ticket.registry.TicketRegistrySupport;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.resolver.impl.AbstractCasWebflowEventResolver;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.CookieGenerator;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.lucky.sso.login.webflow.constants.AccountsConstants;
import com.lucky.sso.login.webflow.handler.AccountsUserAuthenticationHandler;

public class UserCheckWebflowEventResolver extends AbstractCasWebflowEventResolver {

	@Autowired
	AccountsUserAuthenticationHandler accountsUserAuthenticationHandler;

	public UserCheckWebflowEventResolver(AuthenticationSystemSupport authenticationSystemSupport,
			CentralAuthenticationService centralAuthenticationService, ServicesManager servicesManager,
			TicketRegistrySupport ticketRegistrySupport, CookieGenerator warnCookieGenerator,
			AuthenticationServiceSelectionPlan authenticationRequestServiceSelectionStrategies,
			MultifactorAuthenticationProviderSelector multifactorAuthenticationProviderSelector) {
		super(authenticationSystemSupport, centralAuthenticationService, servicesManager, ticketRegistrySupport,
				warnCookieGenerator, authenticationRequestServiceSelectionStrategies,
				multifactorAuthenticationProviderSelector);
	}

	@Override
	public Set<Event> resolveInternal(RequestContext context) {
		try {
			final Credential credential = getCredentialFromContext(context);
			if (credential != null && accountsUserAuthenticationHandler.supports(credential)) {
				accountsUserAuthenticationHandler.authenticate(credential);
			}

			return CollectionUtils.wrapSet(grantUserCredentialToAuthenticationResult(context, credential));
		} catch (final Exception e) {
			Event event = returnUserCheckExceptionEventIfNeeded(e);
			if (event == null) {
				event = newEvent(CasWebflowConstants.TRANSITION_ID_ERROR, e);
			}
			// final HttpServletResponse response =
			// WebUtils.getHttpServletResponseFromExternalWebflowContext(context);
			// response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return CollectionUtils.wrapSet(event);
		}
	}

	private Event returnUserCheckExceptionEventIfNeeded(final Exception e) {
		final Exception ex;
		if (e instanceof AuthenticationException || e instanceof AbstractTicketException
				|| e instanceof FailedLoginException) {
			ex = e;
		} else if (e.getCause() instanceof AuthenticationException || e.getCause() instanceof AbstractTicketException) {
			ex = (Exception) e.getCause();
		} else {
			return null;
		}
		return newEvent(AccountsConstants.TRANSITION_ID_USER_CHECK_FAILURE, ex);
	}

	/**
	 * Grant user.
	 *
	 * @param context                     the context
	 * @param authenticationResultBuilder the authentication result builder
	 * @param service                     the service
	 * @return the event
	 */
	protected Event grantUserCredentialToAuthenticationResult(final RequestContext context,
			final Credential credential) {
		WebUtils.putCredential(context, credential);
		return newEvent(CasWebflowConstants.TRANSITION_ID_SUCCESS);
	}
}
