package com.lucky.sso.login.logout.message;

import java.util.List;

import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.AuthenticationCredentialsThreadLocalBinder;
import org.apereo.cas.logout.DefaultLogoutManager;
import org.apereo.cas.logout.LogoutExecutionPlan;
import org.apereo.cas.logout.LogoutMessageCreator;
import org.apereo.cas.logout.LogoutRequest;
import org.apereo.cas.logout.SingleLogoutServiceMessageHandler;
import org.apereo.cas.ticket.TicketGrantingTicket;

/**
 * @author Seyi.Zhou
 * @date:  2020年3月30日 上午10:47:52
 * @Description: 帐号单点登出处理
 */
public class AccountLogoutManager extends DefaultLogoutManager {

	public AccountLogoutManager(LogoutMessageCreator logoutMessageBuilder,
			SingleLogoutServiceMessageHandler singleLogoutServiceMessageHandler, boolean singleLogoutCallbacksDisabled,
			LogoutExecutionPlan logoutExecutionPlan) {
		super(logoutMessageBuilder, singleLogoutServiceMessageHandler, singleLogoutCallbacksDisabled, logoutExecutionPlan);
	}

	@Override
	public List<LogoutRequest> performLogout(TicketGrantingTicket ticketToBeLoggedOut) {
		Authentication latestAuthentication = ticketToBeLoggedOut.getAuthentication();
		AuthenticationCredentialsThreadLocalBinder.bindCurrent(latestAuthentication);
		return super.performLogout(ticketToBeLoggedOut);
	}
}
