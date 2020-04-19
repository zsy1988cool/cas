package com.lucky.sso.login.webflow.action.usercheck;

import org.apereo.cas.web.flow.resolver.impl.AbstractCasWebflowEventResolver;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class UserCheckAction extends AbstractAction {
	
	AbstractCasWebflowEventResolver userCheckWebflowEventResolver;
	
	public UserCheckAction(AbstractCasWebflowEventResolver userCheckWebflowEventResolver) {
		this.userCheckWebflowEventResolver = userCheckWebflowEventResolver;
	}

	@Override
	protected Event doExecute(RequestContext context) throws Exception {
		Event finalEvent = userCheckWebflowEventResolver.resolveSingle(context);
		return finalEvent;
	}
}
