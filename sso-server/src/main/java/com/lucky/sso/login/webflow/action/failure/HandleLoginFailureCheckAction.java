package com.lucky.sso.login.webflow.action.failure;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apereo.cas.authentication.Credential;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.lucky.sso.login.webflow.handler.util.AccountWebUtils;

public class HandleLoginFailureCheckAction extends AbstractAction {
	
	private final AccountThrottledSubmissionHandler accountThrottleSubmissionHandler;

	public HandleLoginFailureCheckAction(AccountThrottledSubmissionHandler accountThrottleSubmissionHandler) {
		this.accountThrottleSubmissionHandler = accountThrottleSubmissionHandler;
	}
	
	@Override
	protected Event doExecute(RequestContext context) throws Exception {
		Credential credential = WebUtils.getCredential(context);
		String username = credential.getId();
		if(username == null || username.isEmpty()) {
			return error();
		}
		
		List<Date> failureTimes = accountThrottleSubmissionHandler.accessFailuresDatetimes(username);
		int failureCount = failureTimes.size();

		// 是否验证码校验
		int recaptchahold = accountThrottleSubmissionHandler.getRecaptchahold();
		Boolean capchaEnabled = (failureCount >= recaptchahold);
		AccountWebUtils.putCapchaEnabled(context, capchaEnabled);
		
		// 是否锁定
		int threshold = accountThrottleSubmissionHandler.getFailureThreshold();
		Boolean locked = (failureCount >= threshold);
		if(locked) {
			Date firstFailureTime = failureTimes.get(failureCount - 1);
			Date unlockDateTime = accountThrottleSubmissionHandler.getUnlockDate(firstFailureTime);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String message="解锁时间" + df.format(unlockDateTime);

			System.out.println(message);
			AccountWebUtils.putLoginLocked(context, locked, message);
		}
		
		return success();
	}

}
