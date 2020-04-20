package com.lucky.sso.login.webflow.action.captcha;

import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * @author Seyi.Zhou
 * @date: 2020年4月20日
 * @Description: todo
 */
public class CaptchaVerificationAction extends AbstractAction {
	private CaptchaVerificationResolver capchaVerificationResolver;

	public CaptchaVerificationAction(CaptchaVerificationResolver capchaVerificationResolver) {
		this.capchaVerificationResolver = capchaVerificationResolver;
	}
	
	@Override
	protected Event doExecute(RequestContext context) throws Exception {
		Event finalEvent = capchaVerificationResolver.resolveSingle(context);
		return finalEvent;
	}

}
