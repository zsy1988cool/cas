package com.lucky.sso.logout.message;

import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.AuthenticationCredentialsThreadLocalBinder;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.logout.LogoutMessageCreator;
import org.apereo.cas.logout.LogoutRequest;
import org.apereo.cas.ticket.UniqueTicketIdGenerator;
import org.apereo.cas.util.DefaultUniqueTicketIdGenerator;
import org.apereo.cas.util.ISOStandardDateFormat;

/**
 * @author Seyi.Zhou
 * @date:  2020年3月30日 上午10:47:52
 * @Description: 帐号单点登出消息生成类
 */
public class AccountLogoutMessageCreator implements LogoutMessageCreator  {
	
	/** A ticket Id generator. */
    private static final UniqueTicketIdGenerator GENERATOR = new DefaultUniqueTicketIdGenerator(18);

    /** The logout request template. */
    private static final String LOGOUT_REQUEST_TEMPLATE =
            "{\"id\":\"%s\",\"timestamp\":\"%s\",\"st\":\"%s\",\"user\":\"%s\"}";
    
    @Override
    public String create(final LogoutRequest request) {
    	Authentication authentication  = AuthenticationCredentialsThreadLocalBinder.getCurrentAuthentication();
    	
    	String id = "";
    	Principal principal = authentication.getPrincipal();
    	if(principal != null) {
    		id = principal.getId();
    	}
    	
        final String logoutRequest = String.format(LOGOUT_REQUEST_TEMPLATE, GENERATOR.getNewTicketId("LR"),
                new ISOStandardDateFormat().getCurrentDateAndTime(), request.getTicketId(), id);
        
        return logoutRequest;
    }
}
