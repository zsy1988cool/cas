package com.lucky.sso.login.webflow.action.usercheck;

import java.util.Set;

import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.AccountNotFoundException;

import org.apereo.cas.authentication.exceptions.AccountDisabledException;
import org.apereo.cas.web.flow.actions.AuthenticationExceptionHandlerAction;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.webflow.execution.RequestContext;

public class UserCheckExceptionHandlerAction extends AuthenticationExceptionHandlerAction {

	private static final String DEFAULT_USERCHECK_MESSAGE_BUNDLE_PREFIX = "usercheckFailure.";
	private static final String USERCHECK_UNKNOWN = "UNKNOWN";

	/**
	 * String appended to exception class name to create a message bundle key for
	 * that particular error.
	 */
	private String messageBundlePrefix = DEFAULT_USERCHECK_MESSAGE_BUNDLE_PREFIX;

	public UserCheckExceptionHandlerAction(final Set<Class<? extends Throwable>> errors) {
		super(errors);
	}

	/**
	 * Maps an user check exception onto a state name. Also sets an ERROR severity
	 * message in the message context.
	 *
	 * @param e              Authentication error to handle.
	 * @param requestContext the spring context
	 * @return Name of next flow state to transition to or {@value #UNKNOWN}
	 */
	public String handle(final Exception e, final RequestContext requestContext) {
		final MessageContext messageContext = requestContext.getMessageContext();

		if (e instanceof AccountNotFoundException || e instanceof AccountDisabledException
				|| e instanceof AccountLockedException) {
			return handleAccountException(e, requestContext);
		}

		final String messageCode = this.messageBundlePrefix + USERCHECK_UNKNOWN;
		messageContext.addMessage(new MessageBuilder().error().code(messageCode).build());
		return USERCHECK_UNKNOWN;
	}

	/**
	 * Maps an authentication exception onto a state name equal to the simple class
	 * name of the handler errors. with highest precedence. Also sets an ERROR
	 * severity message in the message context of the form
	 * {@code [messageBundlePrefix][exceptionClassSimpleName]} for for the first
	 * handler error that is configured. If no match is found, {@value #UNKNOWN} is
	 * returned.
	 *
	 * @param e              Authentication error to handle.
	 * @param requestContext the spring context
	 * @return Name of next flow state to transition to or {@value #UNKNOWN}
	 */
	protected String handleAccountException(final Exception e, final RequestContext requestContext) {
		final MessageContext messageContext = requestContext.getMessageContext();
//        String handlerErrorName = "AccountNotFoundException";
		String handlerErrorName = e.getClass().getSimpleName();
		final String messageCode = this.messageBundlePrefix + handlerErrorName;
		messageContext.addMessage(new MessageBuilder().error().code(messageCode).build());
		return handlerErrorName;
	}
}
