package com.lucky.sso.login.webflow.action.captcha;

import org.apereo.cas.web.flow.CasWebflowConstants;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.action.EventFactorySupport;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class CaptchaExceptionHandlerAction extends AbstractAction {
	private static final String DEFAULT_MESSAGE_BUNDLE_PREFIX = "authenticationFailure.";

	@Override
	protected Event doExecute(RequestContext requestContext) throws Exception {
		final Event currentEvent = requestContext.getCurrentEvent();

		final Exception error = currentEvent.getAttributes().get(CasWebflowConstants.TRANSITION_ID_ERROR,
				Exception.class);
		if (error != null) {
			final String event = handle(error, requestContext);
			return new EventFactorySupport().event(this, event, currentEvent.getAttributes());
		}
		return new EventFactorySupport().event(this, "error");
	}

	public String handle(final Exception e, final RequestContext requestContext) {
		final MessageContext messageContext = requestContext.getMessageContext();
		String handlerErrorName = e.getClass().getSimpleName();
		
		final String messageCode = DEFAULT_MESSAGE_BUNDLE_PREFIX + handlerErrorName;
		messageContext.addMessage(new MessageBuilder().error().code(messageCode).build());
		
		return handlerErrorName;
	}
}
