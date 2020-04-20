package com.lucky.sso.login.webflow.action.captcha;

import java.util.Set;

import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.AuthenticationServiceSelectionPlan;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.exceptions.UnresolvedPrincipalException;
import org.apereo.cas.services.MultifactorAuthenticationProviderSelector;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.ticket.registry.TicketRegistrySupport;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.resolver.impl.AbstractCasWebflowEventResolver;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.CookieGenerator;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.lucky.sso.authentication.credential.AccountCredential;

public class CaptchaVerificationResolver extends AbstractCasWebflowEventResolver {

	public CaptchaVerificationResolver(AuthenticationSystemSupport authenticationSystemSupport,
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
			if(credential == null || !(credential instanceof AccountCredential)) {
				throw new UnresolvedPrincipalException();
			}

			AccountCredential accountCredential = (AccountCredential)credential;
			String capcha = accountCredential.getCapcha();
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			String capchaFromSession = attributes.getRequest().getSession().getAttribute("captcha_code").toString();
	
			if (capcha == null || !capcha.equalsIgnoreCase(capchaFromSession)) {
				throw new CaptchaErrorException("Sorry, capcha not correct !");
			}
			return CollectionUtils.wrapSet(newEvent(CasWebflowConstants.TRANSITION_ID_SUCCESS));
		} catch (final Exception e) {
			Event event = newEvent(CasWebflowConstants.TRANSITION_ID_ERROR, e);
			return CollectionUtils.wrapSet(event);
		}
	}
}